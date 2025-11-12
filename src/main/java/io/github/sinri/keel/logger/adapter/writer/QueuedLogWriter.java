package io.github.sinri.keel.logger.adapter.writer;

import io.github.sinri.keel.base.verticles.KeelVerticleImpl;
import io.github.sinri.keel.logger.api.adapter.PersistentLogWriter;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public abstract class QueuedLogWriter<R> extends KeelVerticleImpl implements PersistentLogWriter<R> {
    private final Map<String, Queue<R>> queueMap = new ConcurrentHashMap<>();
    private final AtomicBoolean closeFlag = new AtomicBoolean(false);

    public QueuedLogWriter() {
    }

    protected int bufferSize() {
        return 128;
    }

    @Nonnull
    abstract protected Future<Void> processLogRecords(@Nonnull String topic, @Nonnull List<R> batch);

    @Override
    public void write(@Nonnull String topic, @Nonnull R renderedEntity) {
        this.queueMap.computeIfAbsent(topic, k -> new SynchronousQueue<>())
                     .add(renderedEntity);
    }

    @Override
    public void writeBatch(@Nonnull String topic, @Nonnull List<R> renderedEntities) {
        this.queueMap.computeIfAbsent(topic, k -> new SynchronousQueue<>())
                     .addAll(renderedEntities);
    }

    @Override
    protected Future<Void> startVerticle() {
        Keel.asyncCallRepeatedly(repeatedlyCallTask -> {
                Set<String> topics = this.queueMap.keySet();
                AtomicInteger counter = new AtomicInteger(0);
                return Keel.asyncCallIteratively(topics, topic -> {
                               Queue<R> queue = this.queueMap.get(topic);
                               List<R> bufferOfTopic = new ArrayList<>();
                               while (true) {
                                   R r = queue.poll();
                                   if (r == null) break;
                                   bufferOfTopic.add(r);
                                   counter.incrementAndGet();
                                   if (bufferOfTopic.size() >= this.bufferSize()) {
                                       break;
                                   }
                               }

                               if (bufferOfTopic.isEmpty()) return Future.succeededFuture();

                               return processLogRecords(topic, bufferOfTopic);
                           })
                           .eventually(() -> {
                               if (counter.get() == 0) {
                                   if (closeFlag.get()) {
                                       repeatedlyCallTask.stop();
                                       return Future.succeededFuture();
                                   }
                                   return Keel.asyncSleep(100L);
                               } else {
                                   return Future.succeededFuture();
                               }
                           });
            })
            .onComplete(ar -> {
                this.undeployMe();
            });
        return Future.succeededFuture();
    }

    @Override
    public void close() {
        this.closeFlag.set(true);
    }
}
