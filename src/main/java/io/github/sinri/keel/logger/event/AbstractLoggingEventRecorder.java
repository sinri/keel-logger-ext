package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.record.LoggingRecord;

import javax.annotation.Nonnull;

public abstract class AbstractLoggingEventRecorder extends AbstractEventRecorder<LoggingRecord> {
    public AbstractLoggingEventRecorder(@Nonnull String topic) {
        super(topic);
    }
}
