package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志体系下封装实现的 Log4j2 体系日志记录器上下文工厂。
 * <p>
 * 基于 SPI 的相关使用方式： 在 {@code META-INF/services/org.apache.logging.log4j.spi.LoggerContextFactory}
 * 文件写入本类全名 {@code io.github.sinri.keel.logger.log4j2.KeelLog4j2LoggerContextFactory} ，
 * 使得 ServiceLoader 机制下 Log4j2 的 LogManager 会自动加载本类提供日志记录服务体系。
 *
 * @since 5.0.0
 */
public final class KeelLog4j2LoggerContextFactory implements LoggerContextFactory {

    private final KeelLog4j2LoggerContext loggerContext;

    public KeelLog4j2LoggerContextFactory(
            @NotNull Supplier<LogWriterAdapter> adapterSupplier,
            @NotNull LogLevel visibleBaseLevel,
            @Nullable Consumer<Log> logInitializer
    ) {
        this.loggerContext = new KeelLog4j2LoggerContext(adapterSupplier, visibleBaseLevel, logInitializer);
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
        return this.loggerContext;
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
        return this.loggerContext;
    }

    @Override
    public void removeContext(LoggerContext context) {
        // do nothing
    }
}
