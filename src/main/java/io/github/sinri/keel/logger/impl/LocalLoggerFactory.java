package io.github.sinri.keel.logger.impl;

import io.github.sinri.keel.logger.api.GenericLoggerFactory;
import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.event.EventRecorder;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecorder;
import io.github.sinri.keel.logger.api.record.LogRecorder;
import io.github.sinri.keel.logger.impl.event.PlainEventRecorder;
import io.github.sinri.keel.logger.impl.issue.plain.PlainIssueRecorder;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LocalLoggerFactory implements GenericLoggerFactory<String> {

    @Override
    public LogRecorder<String> createLogRecorder(@Nonnull String topic) {
        return LogRecorder.embedded(topic);
    }

    @Override
    public EventRecorder<String> createEventLogRecorder(@Nonnull String topic) {
        return new PlainEventRecorder(topic, LogLevel.INFO);
    }

    @Override
    public <L extends IssueRecord<L>> IssueRecorder<L, String> createIssueRecorder(@Nonnull String topic, @Nonnull Supplier<L> issueRecordSupplier) {
        return new PlainIssueRecorder<>(topic, issueRecordSupplier, LogLevel.INFO);
    }

}
