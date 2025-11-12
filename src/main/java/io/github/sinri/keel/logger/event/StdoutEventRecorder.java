package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.base.event.BaseEventRecorder;
import io.github.sinri.keel.logger.consumer.StdoutTopicRecordConsumer;

import javax.annotation.Nonnull;

public final class StdoutEventRecorder extends BaseEventRecorder {

    public StdoutEventRecorder(@Nonnull String topic) {
        super(topic, new StdoutTopicRecordConsumer());
    }
}
