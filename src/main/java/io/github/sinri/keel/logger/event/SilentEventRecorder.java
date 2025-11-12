package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.event.EventRecorder;

import javax.annotation.Nonnull;

public class SilentEventRecorder<R> implements EventRecorder<R> {

    @Nonnull
    @Override
    public LogLevel visibleLevel() {
        return LogLevel.SILENT;
    }

    @Nonnull
    @Override
    public EventRecorder<R> visibleLevel(@Nonnull LogLevel level) {
        // do nothing to keep silent
        return this;
    }

    @Nonnull
    @Override
    public String topic() {
        return "";
    }

    @Nonnull
    @Override
    public Adapter<EventRecord, R> adapter() {
        throw new UnsupportedOperationException("SilentEventRecorder does not support adapter");
    }

    @Override
    public final void recordEvent(@Nonnull EventRecord eventRecord) {
        // do nothing to keep silent
    }
}
