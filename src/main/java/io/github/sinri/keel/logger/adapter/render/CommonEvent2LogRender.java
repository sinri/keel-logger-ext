package io.github.sinri.keel.logger.adapter.render;

import io.github.sinri.keel.logger.api.event.Event2LogRender;
import io.github.sinri.keel.utils.StackUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CommonEvent2LogRender implements Event2LogRender {
    private static final CommonEvent2LogRender INSTANCE = new CommonEvent2LogRender();

    private CommonEvent2LogRender() {
    }

    public static CommonEvent2LogRender getInstance() {
        return INSTANCE;
    }

    @Override
    public String renderThrowable(@Nonnull Throwable throwable) {
        return StackUtils.renderThrowableChain(throwable);
    }

    @Override
    public String renderClassification(@Nonnull List<String> classification) {
        return new JsonArray(classification).encode();
    }

    @Override
    public String renderContext(@Nonnull Map<String, Object> context) {
        return new JsonObject(context).encode();
    }
}
