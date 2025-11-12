package io.github.sinri.keel.logger.adapter.adapter;

import io.github.sinri.keel.logger.adapter.render.CommonEvent2StringRender;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.LogWriter;
import io.github.sinri.keel.logger.api.adapter.Render;
import io.github.sinri.keel.logger.api.adapter.StringToStdoutWriter;
import io.github.sinri.keel.logger.api.event.EventRecord;

import javax.annotation.Nonnull;

public class EventForStdoutAdapter implements Adapter<EventRecord, String> {
    private final static EventForStdoutAdapter instance = new EventForStdoutAdapter();

    private EventForStdoutAdapter() {
    }

    public static EventForStdoutAdapter getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public Render<EventRecord, String> render() {
        return CommonEvent2StringRender.getInstance();
    }

    @Nonnull
    @Override
    public LogWriter<String> writer() {
        return StringToStdoutWriter.getInstance();
    }

}
