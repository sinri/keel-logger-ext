package io.github.sinri.keel.logger.issue.recorder.render;

import io.vertx.core.json.JsonObject;

public final class KeelIssueRecordRenderBuilder {
    public static KeelIssueRecordRender<String> renderForString() {
        return KeelIssueRecordStringRender.getInstance();
    }

    public static KeelIssueRecordRender<JsonObject> renderForJsonObject() {
        return KeelIssueRecordJsonObjectRender.getInstance();
    }
}
