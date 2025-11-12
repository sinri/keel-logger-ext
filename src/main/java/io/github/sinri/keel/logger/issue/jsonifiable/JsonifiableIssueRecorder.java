package io.github.sinri.keel.logger.issue.jsonifiable;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.issue.AbstractIssueRecorder;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class JsonifiableIssueRecorder<T extends JsonifiableIssueRecord<T>>
        extends AbstractIssueRecorder<T, JsonObject> {

    @Nonnull
    private final Supplier<T> issueRecordSupplier;
    @Nonnull
    private final Adapter<T, JsonObject> adapter;

    public JsonifiableIssueRecorder(@Nonnull String topic, @Nonnull Supplier<T> issueRecordSupplier, @Nonnull LogLevel visibleLevel) {
        super(topic);
        this.issueRecordSupplier = issueRecordSupplier;
        // this.render = new JsonifiableIssueRecordRender<>();
        this.adapter = initializeAdapter();
    }

    @Nonnull
    abstract protected Adapter<T, JsonObject> initializeAdapter();

    @Nonnull
    @Override
    public final Supplier<T> issueRecordSupplier() {
        return issueRecordSupplier;
    }

    @Nonnull
    @Override
    public Adapter<T, JsonObject> adapter() {
        return adapter;
    }

}
