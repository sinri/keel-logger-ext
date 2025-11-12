package io.github.sinri.keel.logger.consumer;

import io.github.sinri.keel.logger.base.consumer.BaseTopicRecordConsumer;
import io.github.sinri.keel.utils.StringUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public final class StdoutTopicRecordConsumer extends BaseTopicRecordConsumer {
    @Override
    public String renderThrowable(@Nonnull Throwable throwable) {
        return StringUtils.renderThrowableChain(throwable);
    }

    @Override
    public String renderClassification(@Nonnull List<String> classification) {
        return new JsonArray(classification).encode();
    }

    @Override
    public String renderContext(@Nonnull Map<String, Object> context) {
        return new JsonObject(context).encodePrettily();
    }
}
