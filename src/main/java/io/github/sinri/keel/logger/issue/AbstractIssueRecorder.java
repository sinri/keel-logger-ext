package io.github.sinri.keel.logger.issue;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecorder;

import javax.annotation.Nonnull;

public abstract class AbstractIssueRecorder<T extends IssueRecord<T>, R> implements IssueRecorder<T, R> {
    @Nonnull
    private final String topic;
    @Nonnull
    private LogLevel visibleLevel;

    public AbstractIssueRecorder(@Nonnull String topic) {
        this.topic = topic;
        this.visibleLevel = LogLevel.INFO;
    }

    @Nonnull
    @Override
    public LogLevel visibleLevel() {
        return visibleLevel;
    }

    @Nonnull
    @Override
    public AbstractIssueRecorder<T, R> visibleLevel(@Nonnull LogLevel level) {
        this.visibleLevel = level;
        return this;
    }

    @Nonnull
    @Override
    public String topic() {
        return topic;
    }
}
