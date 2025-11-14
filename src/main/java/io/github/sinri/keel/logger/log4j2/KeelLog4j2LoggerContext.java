package io.github.sinri.keel.logger.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.vertx.core.Handler;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

// import static io.github.sinri.keel.facade.KeelInstance.Keel;

public final class KeelLog4j2LoggerContext implements LoggerContext {
    private final Map<String, KeelLog4j2Logger> loggerMap;
    @NotNull
    private final Supplier<TopicRecordConsumer> adapterSupplier;
    @NotNull
    private final LogLevel visibleBaseLevel;
    @Nullable
    private final Handler<EventRecord> issueRecordInitializer;

    public KeelLog4j2LoggerContext(
            @NotNull Supplier<TopicRecordConsumer> adapterSupplier,
            @NotNull LogLevel visibleBaseLevel,
            @Nullable Handler<EventRecord> issueRecordInitializer
    ) {
        this.loggerMap = new ConcurrentHashMap<>();
        this.adapterSupplier = adapterSupplier;
        this.visibleBaseLevel = visibleBaseLevel;
        this.issueRecordInitializer = issueRecordInitializer;
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
                    var logger = new KeelLog4j2Logger(this.adapterSupplier, visibleBaseLevel, name, issueRecordInitializer);
                    loggerMap.put(name, logger);
                    // Keel.getLogger().notice("Keel Logging for log4j built logger for [" + name + "]");
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
