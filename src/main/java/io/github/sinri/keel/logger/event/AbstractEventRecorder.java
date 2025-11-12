package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.event.EventRecorder;

import javax.annotation.Nonnull;

public abstract class AbstractEventRecorder<R> implements EventRecorder<R> {
    @Nonnull
    private final String topic;
    private LogLevel visibleLevel;

    public AbstractEventRecorder(@Nonnull String topic) {
        this.topic = topic;
        this.visibleLevel = LogLevel.INFO;
    }

    @Nonnull
    @Override
    public final LogLevel visibleLevel() {
        return visibleLevel;
    }

    @Nonnull
    @Override
    public final EventRecorder<R> visibleLevel(@Nonnull LogLevel level) {
        this.visibleLevel = level;
        return this;
    }

    @Nonnull
    @Override
    public final String topic() {
        return topic;
    }
}
