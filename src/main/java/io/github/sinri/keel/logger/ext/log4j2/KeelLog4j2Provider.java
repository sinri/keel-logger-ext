package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LateObject;
import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 在 Keel 日志系统中封装实现的 Log4j2 日志记录器提供者，可以用于 SPI 机制下的服务提供者发现。
 *
 * @since 5.0.0
 * @deprecated 最新的用法已不需要通过本类实现 SPI；直接以 {@link KeelLog4j2LoggerContextFactory}实现 SPI。
 */
@Deprecated(since = "5.0.0")
@NullMarked
public class KeelLog4j2Provider extends Provider {
    public static final int DEFAULT_PRIORITY = 50;
    public static final String DEFAULT_VERSIONS = "2.x";
    private final LateObject<KeelLog4j2LoggerContextFactory> lateLoggerContextFactory = new LateObject<>();

    public KeelLog4j2Provider() {
        this(DEFAULT_PRIORITY, DEFAULT_VERSIONS);
    }

    protected KeelLog4j2Provider(int priority, String versions) {
        super(priority, versions);
    }

    @Override
    public LoggerContextFactory getLoggerContextFactory() {
        return lateLoggerContextFactory.ensure(() -> new KeelLog4j2LoggerContextFactory(
                getAdapterSupplier(),
                getVisibleBaseLevel(),
                getLogInitializer()
        ));
    }

    @Override
    @Nullable
    public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
        // 按照 Log4j SPI 的设计原意，此方法只负责返回工厂类型
        // 实际的实例化工作由 getLoggerContextFactory() 负责
        return KeelLog4j2LoggerContextFactory.class;
    }

    /**
     * 提供对外可见的基础日志级别。
     * <p>
     * 子类可以重写本方法，用于自定义将被处理的最小日志级别（低于该级别的日志将被过滤）。
     *
     * @return 将被处理的最小 {@link LogLevel}
     */
    protected LogLevel getVisibleBaseLevel() {
        return LogLevel.INFO;
    }

    /**
     * 提供用于获取 {@link LogWriterAdapter} 实例的 {@link Supplier}。
     *
     * <p>
     * 子类可以重写此方法以提供自定义的 {@link LogWriterAdapter}，用于输出日志事件。
     *
     * @return 适配器的供应者
     */
    protected Supplier<LogWriterAdapter> getAdapterSupplier() {
        return BaseLogWriter::getInstance;
    }

    @Nullable
    protected Consumer<Log> getLogInitializer() {
        return null;
    }
}
