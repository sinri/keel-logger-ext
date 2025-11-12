package io.github.sinri.keel.logger.issue.jsonifiable;

import io.github.sinri.keel.core.json.JsonObjectConvertible;
import io.github.sinri.keel.core.json.JsonifiedThrowable;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.utils.time.TimeUtils;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Map;

import static io.github.sinri.keel.utils.time.TimeUtils.MYSQL_DATETIME_MS_PATTERN;

public abstract class JsonifiableIssueRecord<T extends JsonifiableIssueRecord<T>>
        extends IssueRecord<T>
        implements JsonObjectConvertible {

    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        JsonObject x = new JsonObject();

        x.put("timestamp", TimeUtils.getDateExpression(timestamp(), MYSQL_DATETIME_MS_PATTERN));

        x.put(EventRecord.MapKeyLevel, level());
        String message = message();
        if (message != null) {
            x.put(EventRecord.MapKeyMessage, message);
        }

        Map<String, Object> map = context().toMap();
        if (!map.isEmpty()) {
            x.put(EventRecord.MapKeyContext, new JsonObject(map));
        }

        Throwable exception = exception();
        if (exception != null) {
            x.put(EventRecord.MapKeyException, renderThrowable(exception));
        }
        return x;
    }

    @Nonnull
    protected JsonObject renderThrowable(@Nonnull Throwable throwable) {
        return JsonifiedThrowable.wrap(throwable).toJsonObject();
    }

    @Override
    public String toString() {
        return toJsonExpression();
    }
}
