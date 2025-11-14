package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.event.BaseEventRecorder;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.event.EventRecorder;
import org.jetbrains.annotations.NotNull;


public final class SilentEventRecorder extends BaseEventRecorder {

    public SilentEventRecorder(@NotNull String topic) {
        super(topic);
    }

    @NotNull
    @Override
    public EventRecorder visibleLevel(@NotNull LogLevel level) {
        throw new UnsupportedOperationException("SilentEventRecorder can not change visible level as it is no effect.");
    }

    @Override
    public void recordEvent(@NotNull EventRecord eventRecord) {
        // do nothing to keep silent
    }
}
