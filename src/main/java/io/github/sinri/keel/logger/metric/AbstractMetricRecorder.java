package io.github.sinri.keel.logger.metric;

import io.github.sinri.keel.logger.api.metric.MetricRecord;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


abstract public class AbstractMetricRecorder implements Closeable {
    private final AtomicBoolean endSwitch = new AtomicBoolean(false);
    private final Queue<MetricRecord<?>> metricRecordQueue = new ConcurrentLinkedQueue<>();

    private final Vertx vertx;

    public AbstractMetricRecorder(Vertx vertx) {
        this.vertx = vertx;
    }

    public void recordMetric(MetricRecord<?> metricRecord) {
        this.metricRecordQueue.add(metricRecord);
    }

    protected int bufferSize() {
        return 1000;
    }

    /**
     * Override this to change the topic of metric recorder.
     *
     * @since 4.0.0
     */
    protected String topic() {
        return "metric";
    }

    public void start() {
        Future.succeededFuture()
              .compose(v -> {
                  List<MetricRecord<?>> buffer = new ArrayList<>();

                  while (true) {
                      MetricRecord<?> metricRecord = metricRecordQueue.poll();
                      if (metricRecord == null) break;

                      buffer.add(metricRecord);
                      if (buffer.size() >= bufferSize()) break;
                  }

                  if (!buffer.isEmpty()) {
                      return handleForTopic(topic(), buffer);
                  }

                  return Future.succeededFuture();
              })
              .andThen(ar -> {
                  if (!endSwitch.get()) {
                      vertx.setTimer(1000L, id -> start());
                  }
              });
    }

    /**
     * Closes the metric recorder by marking the end of metric recording operations.
     * Once this method is invoked, the recorder will stop processing and no additional
     * metrics will be recorded.
     * <p>
     * This method should be called to properly terminate the recorder's lifecycle
     * and release associated resources.
     * <p>
     * The override method should call the super method to ensure proper resource closure.
     *
     * @throws IOException if an I/O error occurs during the close operation
     * @since 4.1.3
     */
    @Override
    public void close() throws IOException {
        endSwitch.set(true);
    }

    abstract protected Future<Void> handleForTopic(String topic, List<MetricRecord<?>> buffer);
}
