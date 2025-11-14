package io.github.sinri.keel.logger.issue;

import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.issue.BaseIssueRecorder;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.consumer.StdoutTopicRecordConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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
