package io.github.sinri.keel.logger.impl.event;

import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.adapter.LogWriter;
import io.github.sinri.keel.logger.api.adapter.Render;
import io.github.sinri.keel.logger.api.adapter.StdoutStringWriter;
import io.github.sinri.keel.logger.api.event.EventRecord;

import javax.annotation.Nonnull;

public class PlainEventAdapter implements Adapter<EventRecord, String> {
    private final static PlainEventAdapter instance = new PlainEventAdapter();

    private PlainEventAdapter() {
    }

    public static PlainEventAdapter getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public Render<EventRecord, String> render() {
        return StringEventRender.getInstance();
    }

    @Nonnull
    @Override
    public LogWriter<String> writer() {
        return StdoutStringWriter.getInstance();
    }

}
