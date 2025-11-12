package io.github.sinri.keel.logger.issue.plain;

import io.github.sinri.keel.logger.adapter.render.CommonIssue2StringRender;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.StringToStdoutWriter;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.issue.AbstractIssueRecorder;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class StdoutIssueRecorder<T extends IssueRecord<T>> extends AbstractIssueRecorder<T, String> {
    @Nonnull
    private final Supplier<T> issueRecordSupplier;
    @Nonnull
    private final Adapter<T, String> adapter;

    public StdoutIssueRecorder(@Nonnull String topic, @Nonnull Supplier<T> issueRecordSupplier) {
        super(topic);
        this.issueRecordSupplier = issueRecordSupplier;

        CommonIssue2StringRender<T> render = new CommonIssue2StringRender<>();
        this.adapter = Adapter.build(render, StringToStdoutWriter.getInstance());
    }

    @Nonnull
    @Override
    public final Supplier<T> issueRecordSupplier() {
        return issueRecordSupplier;
    }

    @Nonnull
    @Override
    public Adapter<T, String> adapter() {
        return adapter;
    }

}
