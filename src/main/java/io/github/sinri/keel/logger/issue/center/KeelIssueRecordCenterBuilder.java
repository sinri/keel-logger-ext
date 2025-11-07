package io.github.sinri.keel.logger.issue.center;

import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorderBuilder;
import io.github.sinri.keel.logger.issue.recorder.adapter.KeelIssueRecorderAdapter;
import io.github.sinri.keel.logger.issue.recorder.adapter.SilentAdapter;
import io.github.sinri.keel.logger.issue.recorder.adapter.SyncStdoutAdapter;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class KeelIssueRecordCenterBuilder {
    private final static KeelIssueRecordCenter outputCenter = build(SyncStdoutAdapter.getInstance());
    private final static KeelIssueRecordCenter silentCenter = build(SilentAdapter.getInstance());

    /**
     * @param adapter the KeelIssueRecorderAdapter instance.
     * @return the built KeelIssueRecordCenter instance.
     * @since 4.0.0
     */
    public static KeelIssueRecordCenter build(@Nonnull KeelIssueRecorderAdapter adapter) {
        return new KeelIssueRecordCenter() {
            @Nonnull
            @Override
            public KeelIssueRecorderAdapter getAdapter() {
                return adapter;
            }

            @Nonnull
            @Override
            public <T extends KeelIssueRecord<T>> KeelIssueRecorder<T> generateIssueRecorder(@Nonnull String topic, @Nonnull Supplier<T> issueRecordBuilder) {
                return KeelIssueRecorderBuilder.build(this, issueRecordBuilder, topic);
            }
        };
    }

    public static KeelIssueRecordCenter outputCenter() {
        return outputCenter;
    }

    public static KeelIssueRecordCenter silentCenter() {
        return silentCenter;
    }
}
