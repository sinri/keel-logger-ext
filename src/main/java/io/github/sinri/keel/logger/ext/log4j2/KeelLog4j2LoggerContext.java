package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志体系下封装实现的 Log4j2 体系日志记录器上下文。
 *
 * @since 5.0.0
 */
@NullMarked
final class KeelLog4j2LoggerContext implements LoggerContext {
    private final Map<String, KeelLog4j2Logger> loggerMap;
    private final Supplier<LogWriterAdapter> adapterSupplier;
    private final LogLevel visibleBaseLevel;
    private final @Nullable Consumer<Log> logInitializer;
    private final boolean verbose;

    public KeelLog4j2LoggerContext(
            Supplier<LogWriterAdapter> adapterSupplier,
            LogLevel visibleBaseLevel,
            @Nullable Consumer<Log> logInitializer,
            boolean verbose
    ) {
        this.loggerMap = new ConcurrentHashMap<>();
        this.adapterSupplier = adapterSupplier;
        this.visibleBaseLevel = visibleBaseLevel;
        this.logInitializer = logInitializer;
        this.verbose = verbose;
    }

    @Override
    public @Nullable Object getExternalContext() {
        return null;
    }

    @Override
    public ExtendedLogger getLogger(String name) {
        if (loggerMap.containsKey(name)) {
            return loggerMap.get(name);
        } else {
            synchronized (loggerMap) {
                KeelLog4j2Logger existed = loggerMap.get(name);
                if (existed == null) {
                    var logger = new KeelLog4j2Logger(this.adapterSupplier, visibleBaseLevel, name, logInitializer, verbose);
                    loggerMap.put(name, logger);
                    if (verbose) {
                        System.out.println("Keel Logging for log4j built logger for [" + name + "]");
                    }
                    return logger;
                } else {
                    return existed;
                }
            }
        }
    }

    @Override
    public ExtendedLogger getLogger(String name, MessageFactory messageFactory) {
        return getLogger(name);
    }

    @Override
    public boolean hasLogger(String name) {
        return this.loggerMap.containsKey(name);
    }

    @Override
    public boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass) {
        return hasLogger(name);
    }

    @Override
    public boolean hasLogger(String name, MessageFactory messageFactory) {
        return hasLogger(name);
    }
}
