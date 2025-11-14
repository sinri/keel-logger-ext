package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.api.event.BaseEventRecorder;
import io.github.sinri.keel.logger.consumer.StdoutTopicRecordConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Keel 体系下的面向标准输出的事件日志记录器。
 * <p>
 * 将事件日志输出到标准输出。
 *
 * @since 5.0.0
 */
public final class StdoutEventRecorder extends BaseEventRecorder {

    public StdoutEventRecorder(@NotNull String topic) {
        super(topic, new StdoutTopicRecordConsumer());
    }
}
