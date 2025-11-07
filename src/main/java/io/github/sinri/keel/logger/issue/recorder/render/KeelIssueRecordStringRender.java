package io.github.sinri.keel.logger.issue.recorder.render;

import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;
import io.github.sinri.keel.utils.StackUtils;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @since 3.1.10
 */
public class KeelIssueRecordStringRender implements KeelIssueRecordRender<String> {
    private static final KeelIssueRecordStringRender instance = new KeelIssueRecordStringRender();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    protected KeelIssueRecordStringRender() {

    }

    public static KeelIssueRecordStringRender getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public String renderIssueRecord(@Nonnull String topic, @Nonnull KeelIssueRecord<?> issueRecord) {
        StringBuilder s = new StringBuilder("㏒ ");
        s.append(formatter.format(new Date(issueRecord.timestamp())));
        s.append(" [").append(issueRecord.level().name()).append("]");
        s.append(" ").append(topic)
         .append(" (")
         .append(String.join(",", issueRecord.classification()))
         .append(")");
        s.append(" on ").append(issueRecord.getThreadInfo());
        if (!issueRecord.attributes().isEmpty()) {
            issueRecord.attributes().forEach(attribute -> s.append("\n ▪ ").append(attribute.getKey()).append(": ")
                                                           .append(attribute.getValue()));
        }
        Throwable exception = issueRecord.exception();
        if (exception != null) {
            s.append("\n ⊹ Exception Thrown:\n").append(renderThrowable(exception));
        }
        return s.toString();
    }

    @Nonnull
    @Override
    public String renderThrowable(@Nonnull Throwable throwable) {
        return StackUtils.renderThrowableChain(throwable, ignorableStackPackageSet());
    }


}
