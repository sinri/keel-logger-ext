package io.github.sinri.keel.logger.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.vertx.core.Handler;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static io.github.sinri.keel.base.KeelInstance.Keel;

/**
 * 在 Keel 日志体系下封装实现的 Log4j2 体系日志记录器提供者，可以用于 SPI 机制下的服务提供者发现。
 *
 * @since 5.0.0
 * @deprecated 最新的用法已不需要通过本类实现 SPI；直接以 {@link KeelLog4j2LoggerContextFactory}实现 SPI。
 */
@Deprecated(since = "5.0.0")
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

    @NotNull
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
    @Nullable
    public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
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
    @NotNull
    protected LogLevel getVisibleBaseLevel() {
        return LogLevel.INFO;
    }

    /**
     * Provides a {@link Supplier} that supplies the {@link TopicRecordConsumer} instance.
     *
     * <p>
     * Override this method to use another {@link TopicRecordConsumer} to
     * record issues.
     *
     * @return the adapter supplier
     */
    @NotNull
    protected Supplier<TopicRecordConsumer> getAdapterSupplier() {
        return () -> Keel.getRecorderFactory().sharedTopicRecordConsumer();
    }

    @Nullable
    protected Handler<EventRecord> getIssueRecordInitializer() {
        return null;
    }
}
