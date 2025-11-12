package io.github.sinri.keel.logger.adapter.render;

import io.github.sinri.keel.logger.api.issue.IssueRecordRender;
import io.github.sinri.keel.logger.issue.jsonifiable.JsonifiableIssueRecord;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

public class CommonIssue2JsonObjectRender<T extends JsonifiableIssueRecord<T>> implements IssueRecordRender<T, JsonObject> {

    @Nonnull
    @Override
    public JsonObject render(@Nonnull String topic, @Nonnull T loggingEntity) {
        return loggingEntity.toJsonObject();
    }

}
