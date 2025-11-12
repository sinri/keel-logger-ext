package io.github.sinri.keel.logger;

import io.github.sinri.keel.logger.adapter.render.CommonEvent2StringRender;
import io.github.sinri.keel.logger.adapter.render.CommonIssue2LogRender;
import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.InstantLogWriter;
import io.github.sinri.keel.logger.api.adapter.LoggingRecordToStdoutWriter;
import io.github.sinri.keel.logger.api.adapter.StringToStdoutWriter;
import io.github.sinri.keel.logger.api.event.EventRecorder;
import io.github.sinri.keel.logger.api.event.Logger;
import io.github.sinri.keel.logger.api.event.LoggerFactory;
import io.github.sinri.keel.logger.api.event.RecorderFactory;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecorder;
import io.github.sinri.keel.logger.api.issue.LoggingIssueRecorder;
import io.github.sinri.keel.logger.api.record.LoggingRecord;
import io.github.sinri.keel.logger.event.StdoutEventRecorder;
import io.github.sinri.keel.logger.issue.AbstractLoggingIssueRecorder;
import io.github.sinri.keel.logger.issue.plain.StdoutIssueRecorder;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class StdoutRecorderFactory implements RecorderFactory<String>, LoggerFactory {
    private static final StdoutRecorderFactory instance = new StdoutRecorderFactory();

    private StdoutRecorderFactory() {
    }

    public static StdoutRecorderFactory getInstance() {
        return instance;
    }

    @Override
    public EventRecorder<String> createEventLogRecorder(@Nonnull String topic) {
        return new StdoutEventRecorder(topic);
    }

    @Override
    public <L extends IssueRecord<L>> IssueRecorder<L, String> createIssueRecorder(@Nonnull String topic, @Nonnull Supplier<L> issueRecordSupplier) {
        return new StdoutIssueRecorder<>(topic, issueRecordSupplier);
    }

    @Override
    public <L extends IssueRecord<L>> LoggingIssueRecorder<L> createLoggingIssueRecorder(@Nonnull String topic, @Nonnull Supplier<L> issueRecordSupplier) {
        CommonIssue2LogRender<L> render = new CommonIssue2LogRender<>();
        InstantLogWriter<LoggingRecord> writer = new LoggingRecordToStdoutWriter();

        Adapter<L, LoggingRecord> adapter = Adapter.build(render, writer);
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

    @Nonnull
    @Override
    public Logger createLogger(@Nonnull String topic) {
        return new Logger(topic, LogLevel.INFO, Adapter.build(CommonEvent2StringRender.getInstance(), StringToStdoutWriter.getInstance()));
    }
}
