package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.event.EventRecorder;
import io.github.sinri.keel.logger.base.event.BaseEventRecorder;

import javax.annotation.Nonnull;

public final class SilentEventRecorder extends BaseEventRecorder {

    public SilentEventRecorder(@Nonnull String topic) {
        super(topic);
    }

    @Nonnull
    @Override
    public LogLevel visibleLevel() {
        return LogLevel.SILENT;
    }

    @Nonnull
    @Override
    public EventRecorder visibleLevel(@Nonnull LogLevel level) {
        // do nothing to keep silent
        return this;
    }

    @Override
    public void recordEvent(@Nonnull EventRecord eventRecord) {
        // do nothing to keep silent
    }
}
