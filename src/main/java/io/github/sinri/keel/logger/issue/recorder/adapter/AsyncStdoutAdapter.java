package io.github.sinri.keel.logger.issue.recorder.adapter;

import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;
import io.github.sinri.keel.logger.issue.recorder.render.KeelIssueRecordRender;
import io.github.sinri.keel.logger.issue.recorder.render.KeelIssueRecordRenderBuilder;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @since 3.1.10
 */
public class AsyncStdoutAdapter implements KeelIssueRecorderAdapter {
    private final Vertx vertx;
    private final Queue<WrappedIssueRecord> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean closed;

    private AsyncStdoutAdapter(Vertx vertx) {
        this.vertx = vertx;
        closed = false;
        start();
    }

    private void writeOneIssueRecord(@Nonnull String topic, @Nonnull KeelIssueRecord<?> issueRecord) {
        String s = this.issueRecordRender().renderIssueRecord(topic, issueRecord);
        System.out.println(s);
    }

    @Override
    public KeelIssueRecordRender<String> issueRecordRender() {
        return KeelIssueRecordRenderBuilder.renderForString();
    }

    @Override
    public void record(@Nonnull String topic, @Nullable KeelIssueRecord<?> issueRecord) {
        if (issueRecord != null) {
            this.queue.add(new WrappedIssueRecord(topic, issueRecord));
        }
    }

    private void start() {
        vertx.executeBlocking(() -> {
                 while (true) {
                     WrappedIssueRecord wrappedIssueRecord = queue.poll();
                     if (wrappedIssueRecord == null) break;
                     writeOneIssueRecord(wrappedIssueRecord.topic, wrappedIssueRecord.issueRecord);
                 }
                 return null;
             })
             .andThen(ar -> vertx.setTimer(100L, id -> start()));
    }

    @Override
    public Future<Void> gracefullyClose() {
        closed = true;
        return Future.succeededFuture();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
         * @since 4.0.0
         */
        private record WrappedIssueRecord(String topic, KeelIssueRecord<?> issueRecord) {
    }
}
