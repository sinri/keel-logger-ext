package io.github.sinri.keel.logger.factory;


import io.github.sinri.keel.logger.api.event.EventRecorder;
import io.github.sinri.keel.logger.api.factory.BaseRecorderFactory;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecorder;
import io.github.sinri.keel.logger.event.StdoutEventRecorder;
import io.github.sinri.keel.logger.issue.StdoutIssueRecorder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class StdoutRecorderFactory extends BaseRecorderFactory {
    private static final StdoutRecorderFactory instance = new StdoutRecorderFactory();

    private StdoutRecorderFactory() {
    }

    public static StdoutRecorderFactory getInstance() {
        return instance;
    }

    @Override
    public EventRecorder createEventRecorder(@NotNull String topic) {
        return new StdoutEventRecorder(topic);
    }

    @Override
    public <L extends IssueRecord<L>> IssueRecorder<L> createIssueRecorder(@NotNull String topic, @NotNull Supplier<L> issueRecordSupplier) {
        return new StdoutIssueRecorder<>(topic, issueRecordSupplier);
    }
}
