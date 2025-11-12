package io.github.sinri.keel.logger.adapter.render;

import io.github.sinri.keel.logger.api.issue.IssueRecord;
import io.github.sinri.keel.logger.api.issue.IssueRecordRender;
import io.github.sinri.keel.utils.StackUtils;
import io.github.sinri.keel.utils.time.TimeUtils;

import javax.annotation.Nonnull;
import java.util.Map;

import static io.github.sinri.keel.utils.time.TimeUtils.MYSQL_DATETIME_MS_PATTERN;

public class CommonIssue2StringRender<T extends IssueRecord<T>>
        implements IssueRecordRender<T, String> {
    @Nonnull
    @Override
    public String render(@Nonnull String topic, @Nonnull T loggingEntity) {
        StringBuilder s = new StringBuilder("㏒ ");
        s.append(TimeUtils.getCurrentDateExpression(MYSQL_DATETIME_MS_PATTERN));
        s.append(" [").append(loggingEntity.level().name()).append("]");
        s.append(" ").append(topic);
        s.append(" on ").append(loggingEntity.threadInfo());
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

    @Nonnull
    protected String renderThrowable(@Nonnull Throwable throwable) {
        return StackUtils.renderThrowableChain(throwable);
    }
}
