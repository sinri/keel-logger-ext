package io.github.sinri.keel.logger.impl.record;

import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.LogWriter;
import io.github.sinri.keel.logger.api.adapter.Render;
import io.github.sinri.keel.logger.api.record.LogRecord;
import io.github.sinri.keel.logger.impl.writer.QueuedLogWriter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class QueuedAdapter<R> implements Adapter<LogRecord, R> {
    @Nonnull
    private final Render<LogRecord, R> render;
    @Nonnull
    private final QueuedLogWriter<R> writer;

    public QueuedAdapter(@Nonnull Render<LogRecord, R> render, @Nonnull QueuedLogWriter<R> writer) {
        this.render = render;
        this.writer = writer;
    }

    @Nonnull
    @Override
    public Render<LogRecord, R> render() {
        return render;
    }

    @Nonnull
    @Override
    public LogWriter<R> writer() {
        return writer;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}