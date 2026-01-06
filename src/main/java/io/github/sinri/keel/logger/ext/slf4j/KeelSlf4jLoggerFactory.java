package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 在 Keel 日志体系下封装实现的 slf4j 体系日志记录器工厂。
 *
 * @see KeelSlf4jLogger
 * @see ILoggerFactory
 * @since 5.0.0
 */
@NullMarked
final class KeelSlf4jLoggerFactory implements ILoggerFactory {

    /**
     * Supplier for obtaining the {@link LogWriterAdapter} instance used by created loggers.
     * <p>
     * This supplier allows for lazy initialization and dynamic configuration of the logging backend.
     * The same supplier instance is shared among all loggers created by this factory, enabling
     * consistent logging behavior across the application.
     */
    private final Supplier<LogWriterAdapter> adapterSupplier;
    private final @Nullable Consumer<Log> logInitializer;

    /**
     * Cache for storing created logger instances to ensure singleton behavior per logger name.
     * <p>
     * This cache prevents the creation of multiple logger instances for the same name,
     * which is a requirement of the SLF4J specification. The cache uses an everlasting
     * strategy, meaning logger instances are retained for the lifetime of the factory.
     */
    private final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();

    private final boolean verbose;

    /**
     * Constructs a new KeelLoggerFactory with the specified adapter supplier.
     * <p>
     * The adapter supplier will be used to obtain {@link LogWriterAdapter} instances
     * for all loggers created by this factory. The supplier should return a consistent
     * adapter instance or instances with compatible configuration.
     *
     * @param adapterSupplier the supplier for obtaining issue recorder adapter instances;
     *                        must not be null and should return non-null adapters
     * @throws NullPointerException if adapterSupplier is null
     */
    public KeelSlf4jLoggerFactory(
            Supplier<LogWriterAdapter> adapterSupplier,
            @Nullable Consumer<Log> logInitializer, boolean verbose) {
        this.adapterSupplier = adapterSupplier;
        this.logInitializer = logInitializer;
        this.verbose = verbose;
    }

    /**
     * Returns a logger instance for the specified name.
     * <p>
     * This method implements the SLF4J contract by returning the same logger instance
     * for multiple calls with the same name. If a logger with the given name doesn't
     * exist in the cache, a new {@link KeelSlf4jLogger} instance is created with:
     * <ul>
     *   <li>The configured adapter supplier</li>
     *   <li>A default log level of {@link LogLevel#INFO}</li>
     *   <li>The provided name as the logger topic</li>
     * </ul>
     * <p>
     * <strong>Thread Safety:</strong> Logger creation is synchronized on the adapter supplier
     * to prevent race conditions when multiple threads request the same logger name simultaneously.
     *
     * @param name the name of the logger to retrieve; typically a class name or component identifier
     * @return a logger instance for the specified name; never null
     * @throws RuntimeException if logger creation fails due to adapter supplier issues
     */
    @Override
    public Logger getLogger(String name) {
        if (loggerCache.containsKey(name)) {
            return loggerCache.get(name);
        } else {
            synchronized (adapterSupplier) {
                var logger = new KeelSlf4jLogger(adapterSupplier, LogLevel.INFO, name, logInitializer);
                //Keel.getLogger().notice("Keel Logging for slf4j built logger for [" + name + "]");
                if (verbose) {
                    System.out.println("Keel Logging for slf4j built logger for [" + name + "]");
                }
                loggerCache.put(name, logger);
                return logger;
            }
        }
    }
}
