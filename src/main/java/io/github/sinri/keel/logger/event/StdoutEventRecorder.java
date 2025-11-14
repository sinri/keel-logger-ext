package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.event.BaseEventRecorder;
import io.github.sinri.keel.logger.consumer.StdoutTopicRecordConsumer;
import org.jetbrains.annotations.NotNull;


public final class StdoutEventRecorder extends BaseEventRecorder {

    public StdoutEventRecorder(@NotNull String topic) {
        super(topic, new StdoutTopicRecordConsumer());
    }
}
