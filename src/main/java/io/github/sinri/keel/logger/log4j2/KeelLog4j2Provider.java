package io.github.sinri.keel.logger.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.Adapter;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.github.sinri.keel.logger.impl.event.PlainEventAdapter;
import io.vertx.core.Handler;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class KeelLog4j2Provider extends Provider {
    public static final int DEFAULT_PRIORITY = 50;
    public static final String DEFAULT_VERSIONS = "2.x";
    private volatile KeelLog4j2LoggerContextFactory loggerContextFactory;

    public KeelLog4j2Provider() {
        this(DEFAULT_PRIORITY, DEFAULT_VERSIONS);
    }

    protected KeelLog4j2Provider(int priority, String versions) {
        super(priority, versions);
    }

    @Nonnull
    @Override
    public LoggerContextFactory getLoggerContextFactory() {
        if (loggerContextFactory == null) {
            synchronized (this) {
                if (loggerContextFactory == null) {
                    loggerContextFactory = new KeelLog4j2LoggerContextFactory(
                            getAdapterSupplier(),
                            getVisibleBaseLevel(),
                            getIssueRecordInitializer()
                    );
                }
            }
        }
        return loggerContextFactory;
    }

    @Override
    public @Nullable Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
        // 按照 Log4j SPI 的设计原意，此方法只负责返回工厂类型
        // 实际的实例化工作由 getLoggerContextFactory() 负责
        return KeelLog4j2LoggerContextFactory.class;
    }

    /**
     * Provides the visible base level for logging.
     * <p>
     * Override this method to customize the minimum log level that will be
     * processed.
     *
     * @return the minimum {@link LogLevel} that will be processed
     */
    @Nonnull
    protected LogLevel getVisibleBaseLevel() {
        return LogLevel.INFO;
    }

    /**
     * Provides a {@link Supplier} that supplies the {@link Adapter} instance.
     *
     * <p>
     * Override this method to use another {@link Adapter} to
     * record issues.
     *
     * @return the adapter supplier
     */
    @Nonnull
    protected Supplier<Adapter<EventRecord, String>> getAdapterSupplier() {
        return PlainEventAdapter::getInstance;
    }

    @Nullable
    protected Handler<EventRecord> getIssueRecordInitializer() {
        return null;
    }
}
