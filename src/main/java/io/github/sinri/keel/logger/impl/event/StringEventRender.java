package io.github.sinri.keel.logger.impl.event;

import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.api.event.EventRender;
import io.github.sinri.keel.utils.StackUtils;
import io.github.sinri.keel.utils.time.TimeUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static io.github.sinri.keel.utils.time.TimeUtils.MYSQL_DATETIME_MS_PATTERN;

public class StringEventRender implements EventRender<String> {
    private static final StringEventRender instance = new StringEventRender();

    private StringEventRender() {
    }

    public static StringEventRender getInstance() {
        return instance;
    }

    @Nonnull
    protected String renderThrowable(@Nonnull Throwable throwable) {
        return StackUtils.renderThrowableChain(throwable);
    }

    @Nonnull
    @Override
    public String render(@Nonnull String topic, @Nonnull EventRecord loggingEntity) {
        StringBuilder s = new StringBuilder("㏒ ");
        s.append(TimeUtils.getCurrentDateExpression(MYSQL_DATETIME_MS_PATTERN));
        s.append(" [").append(loggingEntity.level().name()).append("]");
        s.append(" ").append(topic);
        s.append(" on ").append(loggingEntity.threadInfo());

        List<String> classification = loggingEntity.classification();
        if (classification != null && !classification.isEmpty()) {
            s.append("\n @ ").append(String.join("∷", classification));
        }

        Map<String, Object> map = loggingEntity.context().toMap();
        if (!map.isEmpty()) {
            map.forEach((k, v) -> s.append("\n ▪ ").append(k).append(": ").append(v));
        }
        Throwable exception = loggingEntity.exception();
        if (exception != null) {
            s.append("\n ⊹ Exception Thrown:\n").append(renderThrowable(exception));
        }
        return s.toString();
    }
}
