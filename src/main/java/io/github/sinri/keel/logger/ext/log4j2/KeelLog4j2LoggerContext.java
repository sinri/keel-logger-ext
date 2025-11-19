package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志体系下封装实现的 Log4j2 体系日志记录器上下文。
 *
 * @since 5.0.0
 */
final class KeelLog4j2LoggerContext implements LoggerContext {
    private final Map<String, KeelLog4j2Logger> loggerMap;
    @NotNull
    private final Supplier<LogWriterAdapter> adapterSupplier;
    @NotNull
    private final LogLevel visibleBaseLevel;
    @Nullable
    private final Consumer<Log> logInitializer;

    public KeelLog4j2LoggerContext(
            @NotNull Supplier<LogWriterAdapter> adapterSupplier,
            @NotNull LogLevel visibleBaseLevel,
            @Nullable Consumer<Log> logInitializer
    ) {
        this.loggerMap = new ConcurrentHashMap<>();
        this.adapterSupplier = adapterSupplier;
        this.visibleBaseLevel = visibleBaseLevel;
        this.logInitializer = logInitializer;
    }

    @Override
    public Object getExternalContext() {
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
                    var logger = new KeelLog4j2Logger(this.adapterSupplier, visibleBaseLevel, name, logInitializer);
                    loggerMap.put(name, logger);
                    System.out.println("Keel Logging for log4j built logger for [" + name + "]");
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
