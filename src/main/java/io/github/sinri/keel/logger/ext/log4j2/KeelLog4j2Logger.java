package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.jspecify.annotations.Nullable;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志体系下封装实现的 Log4j2 体系日志记录器。
 *
 * @since 5.0.0
 */
final class KeelLog4j2Logger extends AbstractLogger {
    private final Supplier<LogWriterAdapter> adapterSupplier;
    private final String topic;
    private final LogLevel visibleBaseLevel;
    private final @Nullable Consumer<Log> logInitializer;
    private final boolean verbose;

    public KeelLog4j2Logger(
            Supplier<LogWriterAdapter> adapterSupplier,
            LogLevel visibleBaseLevel,
            String topic,
            @Nullable Consumer<Log> logInitializer,
            boolean verbose
    ) {
        super(topic, null, null);
        this.adapterSupplier = adapterSupplier;
        this.topic = topic;
        this.visibleBaseLevel = visibleBaseLevel;
        this.logInitializer = logInitializer;
        this.verbose = verbose;
    }

    private static LogLevel transLevel(Level level) {
        if (level == Level.TRACE || level == Level.ALL) {
            return LogLevel.TRACE;
        } else if (level == Level.DEBUG) {
            return LogLevel.DEBUG;
        } else if (level == Level.INFO) {
            return LogLevel.INFO;
        } else if (level == Level.WARN) {
            return LogLevel.WARNING;
        } else if (level == Level.ERROR) {
            return LogLevel.ERROR;
        } else if (level == Level.FATAL) {
            return LogLevel.FATAL;
        } else if (level == Level.OFF) {
            return LogLevel.SILENT;
        } else {
            return LogLevel.SILENT;
        }
    }

    private static Level transLevel(@Nullable LogLevel level) {
        if (level == null) return Level.OFF;
        return switch (level) {
            case TRACE -> Level.TRACE;
            case DEBUG -> Level.DEBUG;
            case INFO, NOTICE -> Level.INFO;
            case WARNING -> Level.WARN;
            case ERROR -> Level.ERROR;
            case FATAL -> Level.FATAL;
            case SILENT -> Level.OFF;
        };
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
        return isEnabled(level, marker, (Object) message, t);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
        return isEnabled(level, marker, (Object) message, t);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        LogLevel keelLogLevel = transLevel(level);
        return keelLogLevel.isEnoughSeriousAs(visibleBaseLevel);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
        return isEnabled(level, marker, (Object) message, t);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message) {
        return isEnabled(level, marker, message, (Throwable) null);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        LogLevel keelLogLevel = transLevel(level);
        return keelLogLevel.isEnoughSeriousAs(visibleBaseLevel);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
        return isEnabled(level, marker, message, new Object[]{p0});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
        return isEnabled(level, marker, message, new Object[]{p0, p1});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4, p5});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4, p5, p6});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4, p5, p6, p7});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4, p5, p6, p7, p8});
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return isEnabled(level, marker, message, new Object[]{p0, p1, p2, p3, p4, p5, p6, p7, p8, p9});
    }

    @Override
    public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
        LogWriterAdapter adapter = this.adapterSupplier.get();
        if (adapter == null) return;

        Log keelEventLog = new Log();
        if (logInitializer != null) {
            logInitializer.accept(keelEventLog);
        }
        // keelEventLog.classification(fqcn);
        keelEventLog.level(transLevel(level));
        if (t != null) {
            keelEventLog.exception(t);
        }
        keelEventLog.message(message.getFormattedMessage());

        // 添加线程上下文信息到日志事件
        addThreadContextToLog(keelEventLog);

        adapter.accept(topic, keelEventLog);
    }

    /**
     * Adds thread context information to the log event.
     * <p>
     * This method retrieves the current thread's context map from Log4j's
     * ThreadContext and adds it to the Keel log event as context information.
     *
     * @param keelEventLog the log event to add context to
     */
    private void addThreadContextToLog(Log keelEventLog) {
        try {
            var contextMap = ThreadContext.getContext();
            if (contextMap != null && !contextMap.isEmpty()) {
                contextMap.forEach(keelEventLog::context);
            }
        } catch (Exception e) {
            // 忽略上下文获取错误，不影响日志记录
            // 可以考虑记录一个调试级别的错误，但避免递归日志记录
            if (verbose) {
                System.err.println("Failed to add thread context to log event: " + e.getMessage());
                e.printStackTrace(new PrintWriter(System.err));
            }
        }
    }

    @Override
    public Level getLevel() {
        return transLevel(visibleBaseLevel);
    }
}
