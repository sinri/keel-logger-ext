package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 在 Keel 日志系统中封装实现的 Log4j2 日志记录器上下文工厂。
 * <p>
 * 基于 SPI 的使用方式：在
 * {@code META-INF/services/org.apache.logging.log4j.spi.LoggerContextFactory}
 * 文件中写入本类全名
 * {@code io.github.sinri.keel.logger.ext.log4j2.KeelLog4j2LoggerContextFactory}，
 * 从而使 Log4j2 的 LogManager 通过 ServiceLoader 自动加载本工厂以提供日志记录能力。
 *
 * @since 5.0.0
 */
@NullMarked
public final class KeelLog4j2LoggerContextFactory implements LoggerContextFactory {

    private final KeelLog4j2LoggerContext loggerContext;

    public KeelLog4j2LoggerContextFactory() {
        this(BaseLogWriter::getInstance, LogLevel.INFO, null);
    }

    public KeelLog4j2LoggerContextFactory(
            Supplier<LogWriterAdapter> adapterSupplier,
            LogLevel visibleBaseLevel,
            @Nullable Consumer<Log> logInitializer) {
        this.loggerContext = new KeelLog4j2LoggerContext(adapterSupplier, visibleBaseLevel, logInitializer, isVerbose());
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
        return this.loggerContext;
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext,
                                    URI configLocation, String name) {
        return this.loggerContext;
    }

    @Override
    public void removeContext(LoggerContext context) {
        // do nothing
    }

    public boolean isVerbose() {
        return System.getProperty("keel.logger.ext.verbose", "false").equalsIgnoreCase("true");
    }
}
