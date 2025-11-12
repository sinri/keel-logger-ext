package io.github.sinri.keel.logger.issue;

import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.LoggingIssueRecorder;
import io.github.sinri.keel.logger.api.record.LoggingRecord;

import javax.annotation.Nonnull;

public abstract class AbstractLoggingIssueRecorder<T extends IssueRecord<T>>
        extends AbstractIssueRecorder<T, LoggingRecord>
        implements LoggingIssueRecorder<T> {
    public AbstractLoggingIssueRecorder(@Nonnull String topic) {
        super(topic);
    }

}
