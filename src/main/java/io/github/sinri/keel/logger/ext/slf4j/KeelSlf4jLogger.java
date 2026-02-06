package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志系统中封装实现的 SLF4J 日志记录器。
 * <p>
 * SLF4J 的 {@link Marker} 会被转换为 Keel 的 classification（分类）信息。
 *
 * @since 5.0.0
 */
final class KeelSlf4jLogger implements Logger {
    /**
     * 用于获取 {@link LogWriterAdapter} 的供应者。
     * <p>
     * 通过供应者可以实现延迟初始化以及运行期的适配器切换。
     */
    private final Supplier<LogWriterAdapter> adapterSupplier;

    /**
     * 当前日志记录器的主题（topic）/名称。
     * <p>
     * 通常使用类名或组件标识。
     */
    private final String topic;

    /**
     * 对外可见的基础级别（最小处理级别）。
     * <p>
     * 低于该级别的日志事件会被过滤。
     */
    private final LogLevel visibleBaseLevel;
    private final @Nullable Consumer<Log> logInitializer;

    /**
     * 创建一个 SLF4J 日志记录器实例。
     *
     * @param adapterSupplier  用于获取日志写入适配器的供应者
     * @param visibleBaseLevel 对外可见的基础级别（最小处理级别）
     * @param topic            日志主题（topic）/名称
     * @param logInitializer   写入前用于初始化 {@link Log} 的钩子；可为 null
     */
    KeelSlf4jLogger(
            Supplier<LogWriterAdapter> adapterSupplier,
            LogLevel visibleBaseLevel,
            String topic,
            @Nullable Consumer<Log> logInitializer) {
        this.adapterSupplier = adapterSupplier;
        this.topic = topic;
        this.visibleBaseLevel = visibleBaseLevel;
        this.logInitializer = logInitializer;
    }

    /**
     * 获取日志记录器名称。
     *
     * @return 日志主题（topic）/名称
     */
    @Override
    public String getName() {
        return topic;
    }

    /**
     * 获取对外可见的基础级别（最小处理级别）。
     *
     * @return 对外可见的基础级别
     */
    private LogLevel getVisibleBaseLevel() {
        return visibleBaseLevel;
    }

    private Log createIssueRecordTemplate() {
        var x = new Log();
        if (this.logInitializer != null) {
            this.logInitializer.accept(x);
        }
        return x;
    }

    /**
     * 记录一条日志事件（先创建模板，再由处理器补充字段）。
     * <p>
     * 适配器内部可能以异步方式处理实际写入。
     *
     * @param issueHandler 用于补充/修改日志事件的处理器
     */
    private void record(Consumer<Log> issueHandler) {
        Log issue = createIssueRecordTemplate();
        issueHandler.accept(issue);

        if (issue.level().isEnoughSeriousAs(getVisibleBaseLevel())) {
            var adapter = adapterSupplier.get();
            if (adapter != null) {
                adapter.accept(getName(), issue);
            }
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return LogLevel.TRACE.isEnoughSeriousAs(getVisibleBaseLevel());
    }

    @Override
    public void trace(String msg) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.message(msg);
        });
    }

    @Override
    public void trace(String format, Object arg) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void trace(String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void trace(String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
        });
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, argArray).getMessage());
        });
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.TRACE);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isDebugEnabled() {
        return LogLevel.DEBUG.isEnoughSeriousAs(getVisibleBaseLevel());
    }

    @Override
    public void debug(String msg) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.message(msg);
        });
    }

    @Override
    public void debug(String format, Object arg) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void debug(String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void debug(String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
        });
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.DEBUG);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
            log.exception(t);
        });
    }

    /**
     * 将 SLF4J {@link Marker} 转换为 Keel 的 classification（分类）列表。
     * <p>
     * 返回值包含标记自身名称以及其引用的所有标记名称；当 marker 为 null 时返回空列表。
     *
     * @param marker SLF4J 标记；可为 null
     * @return classification（分类）列表
     */
    private List<String> transformMarkerToClassification(@Nullable Marker marker) {
        List<String> classification = new ArrayList<>();
        if (marker != null) {
            classification.add(marker.getName());
            if (marker.hasReferences()) {
                marker.iterator().forEachRemaining(x -> {
                    classification.add(x.getName());
                });
            }
        }
        return classification;
    }

    @Override
    public boolean isInfoEnabled() {
        return LogLevel.INFO.isEnoughSeriousAs(getVisibleBaseLevel());
    }

    @Override
    public void info(String msg) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.message(msg);
        });
    }

    @Override
    public void info(String format, Object arg) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void info(String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void info(String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
        });
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.INFO);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isWarnEnabled() {
        return LogLevel.WARNING.isEnoughSeriousAs(getVisibleBaseLevel());
    }

    @Override
    public void warn(String msg) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.message(msg);
        });
    }

    @Override
    public void warn(String format, Object arg) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void warn(String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void warn(String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
        });
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.WARNING);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isErrorEnabled() {
        return LogLevel.ERROR.isEnoughSeriousAs(this.getVisibleBaseLevel());
    }

    @Override
    public void error(String msg) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.message(msg);
        });
    }

    @Override
    public void error(String format, Object arg) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void error(String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void error(String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.message(msg);
            log.exception(t);
        });
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
        });
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg}).getMessage());
        });
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2}).getMessage());
        });
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.classification(transformMarkerToClassification(marker));
            log.message(MessageFormatter.arrayFormat(format, arguments).getMessage());
        });
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        record(log -> {
            log.level(LogLevel.ERROR);
            log.classification(transformMarkerToClassification(marker));
            log.message(msg);
            log.exception(t);
        });
    }
}