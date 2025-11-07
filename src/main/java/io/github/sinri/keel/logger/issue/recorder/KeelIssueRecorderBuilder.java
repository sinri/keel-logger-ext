package io.github.sinri.keel.logger.issue.recorder;

import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class KeelIssueRecorderBuilder {
    public static <T extends KeelIssueRecord<T>> KeelIssueRecorder<T> build(
            @Nonnull KeelIssueRecordCenter issueRecordCenter,
            @Nonnull Supplier<T> issueRecordBuilder,
            @Nonnull String topic
    ) {
        return new KeelIssueRecorderImpl<>(issueRecordCenter, issueRecordBuilder, topic);
    }

    /**
     * @since 4.0.0
     */
    public static <T extends KeelIssueRecord<T>> KeelIssueRecorder<T> buildSilentIssueRecorder() {
        return new SilentIssueRecorder<>();
    }

    /**
     * @since 4.0.0
     */
    public static <T extends KeelIssueRecord<T>> KeelIssueRecorder<KeelEventLog> toEventLogger(@Nonnull KeelIssueRecorder<T> issueRecorder) {
        return KeelIssueRecorderBuilder.build(
                issueRecorder.issueRecordCenter(),
                () -> {
                    T t = issueRecorder.issueRecordBuilder().get();
                    // if null returned, log is ignored.
                    if (t == null) {
                        return null;
                    }
                    return new KeelEventLog(t);
                },
                issueRecorder.topic()
        );
    }
}
