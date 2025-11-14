package io.github.sinri.keel.logger.consumer;

import io.github.sinri.keel.base.utils.StringUtils;
import io.github.sinri.keel.logger.api.consumer.BaseTopicRecordConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class StdoutTopicRecordConsumer extends BaseTopicRecordConsumer {
    @Override
    public String renderThrowable(@NotNull Throwable throwable) {
        return StringUtils.renderThrowableChain(throwable);
    }

    @Override
    public String renderClassification(@NotNull List<String> classification) {
        return new JsonArray(classification).encode();
    }

    @Override
    public String renderContext(@NotNull Map<String, Object> context) {
        return new JsonObject(context).encodePrettily();
    }
}
