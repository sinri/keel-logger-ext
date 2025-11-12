package io.github.sinri.keel.logger.factory;

import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.LogWriter;
import io.github.sinri.keel.logger.api.adapter.Render;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.event.EventRecorder;
import io.github.sinri.keel.logger.api.factory.RecorderFactory;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecorder;
import io.github.sinri.keel.logger.api.issue.LoggingIssueRecorder;
import io.github.sinri.keel.logger.api.record.LoggingRecord;
import io.github.sinri.keel.logger.event.AbstractEventRecorder;
import io.github.sinri.keel.logger.issue.AbstractIssueRecorder;
import io.github.sinri.keel.logger.issue.AbstractLoggingIssueRecorder;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class AbstractRecorderFactory<R> implements RecorderFactory<R> {
    abstract protected <L extends IssueRecord<L>> Render<L, R> renderIssueRecord();

    abstract protected Render<EventRecord, R> renderEventRecord();

    abstract protected LogWriter<R> writer();

    @Override
    public EventRecorder<R> createEventLogRecorder(@Nonnull String topic) {
        Adapter<EventRecord, R> adapter = Adapter.build(renderEventRecord(), writer());
        return new AbstractEventRecorder<>(topic) {
            @Nonnull
            @Override
            public Adapter<EventRecord, R> adapter() {
                return adapter;
            }
        };
    }


    @Override
    public <L extends IssueRecord<L>> IssueRecorder<L, R> createIssueRecorder(@Nonnull String topic, @Nonnull Supplier<L> issueRecordSupplier) {
        Render<L, R> render = renderIssueRecord();
        Adapter<L, R> adapter = Adapter.build(render, writer());
        return new AbstractIssueRecorder<>(topic) {
            @Nonnull
            @Override
            public Supplier<L> issueRecordSupplier() {
                return issueRecordSupplier;
            }

            @Nonnull
            @Override
            public Adapter<L, R> adapter() {
                return adapter;
            }
        };
    }

    @Override
    public <L extends IssueRecord<L>> LoggingIssueRecorder<L> createLoggingIssueRecorder(@Nonnull String topic, @Nonnull Supplier<L> issueRecordSupplier) {
        Adapter<L, LoggingRecord> adapter = Adapter.build(renderIssueRecord(), writer());
        return new AbstractLoggingIssueRecorder<L>(topic) {
            @Nonnull
            @Override
            public Supplier<L> issueRecordSupplier() {
                return issueRecordSupplier;
            }

            @Nonnull
            @Override
            public Adapter<L, LoggingRecord> adapter() {
                return adapter;
            }
        };
    }
}
