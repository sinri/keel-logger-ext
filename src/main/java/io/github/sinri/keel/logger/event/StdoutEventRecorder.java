package io.github.sinri.keel.logger.event;

import io.github.sinri.keel.logger.adapter.adapter.EventForStdoutAdapter;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.event.EventRecord;

import javax.annotation.Nonnull;

public class StdoutEventRecorder extends AbstractEventRecorder<String> {
    private final Adapter<EventRecord, String> adapter;

    public StdoutEventRecorder(String topic) {
        super(topic);
        this.adapter = EventForStdoutAdapter.getInstance();
    }

    @Nonnull
    @Override
    public Adapter<EventRecord, String> adapter() {
        return adapter;
    }
}
