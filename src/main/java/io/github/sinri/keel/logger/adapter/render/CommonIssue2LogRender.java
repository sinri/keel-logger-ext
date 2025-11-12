package io.github.sinri.keel.logger.adapter.render;

import io.github.sinri.keel.logger.api.event.Event2LogRender;
import io.github.sinri.keel.logger.api.issue.Issue2EventRender;
import io.github.sinri.keel.logger.api.issue.Issue2LogRender;
import io.github.sinri.keel.logger.api.issue.IssueRecord;

import javax.annotation.Nonnull;

public class CommonIssue2LogRender<T extends IssueRecord<T>> implements Issue2LogRender<T> {
    private final Issue2EventRender<T> issue2EventRender = new Issue2EventRender<>();

    @Nonnull
    @Override
    public Issue2EventRender<T> issue2EventRender() {
        return issue2EventRender;
    }

    @Nonnull
    @Override
    public Event2LogRender event2LogRender() {
        return CommonEvent2LogRender.getInstance();
    }
}
