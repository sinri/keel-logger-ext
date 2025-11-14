package io.github.sinri.keel.logger.issue;

import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.issue.BaseIssueRecorder;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.consumer.StdoutTopicRecordConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Keel 体系下的面向标准输出的特定问题记录器。
 *
 * @param <T> 特定问题记录的类型
 * @since 5.0.0
 */
public class StdoutIssueRecorder<T extends IssueRecord<T>> extends BaseIssueRecorder<T> {

    private final StdoutTopicRecordConsumer consumer;

    public StdoutIssueRecorder(@NotNull String topic, @NotNull Supplier<T> issueRecordSupplier) {
        super(topic, issueRecordSupplier);
        this.consumer = new StdoutTopicRecordConsumer();
    }

    @NotNull
    @Override
    public TopicRecordConsumer consumer() {
        return consumer;
    }
}
