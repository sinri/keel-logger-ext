package io.github.sinri.keel.logger.record;


import io.github.sinri.keel.logger.adapter.writer.QueuedLogWriter;
import io.github.sinri.keel.logger.api.record.LoggingRecord;
import io.github.sinri.keel.logger.api.record.LoggingRecorder;

import javax.annotation.Nonnull;

public abstract class QueuedLogRecorder implements LoggingRecorder {
    @Nonnull
    private final String topic;

    public QueuedLogRecorder(@Nonnull String topic) {
        this.topic = topic;
    }

    @Nonnull
    abstract protected QueuedLogWriter<LoggingRecord> writer();

    @Nonnull
    @Override
    public final String topic() {
        return topic;
    }

    @Override
    public final void recordLog(@Nonnull LoggingRecord record) {
        this.writer().write(topic, record);
    }

}
