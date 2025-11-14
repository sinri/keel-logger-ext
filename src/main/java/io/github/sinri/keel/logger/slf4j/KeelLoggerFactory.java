package io.github.sinri.keel.logger.slf4j;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * A factory implementation for creating SLF4J Logger instances that integrate with the Keel logging framework.
 * <p>
 * This factory implements the SLF4J {@link ILoggerFactory} interface and creates {@link KeelSlf4jLogger} instances
 * that bridge SLF4J logging calls to the Keel issue recording system. The factory uses a caching mechanism to
 * ensure that multiple requests for the same logger name return the same logger instance.
 * <p>
 * The factory is configured with a {@link TopicRecordConsumer} supplier that provides the underlying
 * logging infrastructure. This allows for flexible configuration and lazy initialization of the logging backend.
 * <p>
 * <strong>Thread Safety:</strong> This factory is thread-safe. Logger creation is synchronized to prevent
 * race conditions when multiple threads request the same logger simultaneously.
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>{@code
 * // Create a factory with a stdout adapter
 * KeelLoggerFactory factory = new KeelLoggerFactory(() -> SyncStdoutAdapter.getInstance());
 *
 * // Get a logger instance
 * Logger logger = factory.getLogger("com.example.MyClass");
 * }</pre>
 *
 * @see KeelSlf4jLogger
 * @see ILoggerFactory
 * @since 4.1.1
 */
public final class KeelLoggerFactory implements ILoggerFactory {

    /**
     * Supplier for obtaining the {@link TopicRecordConsumer} instance used by created loggers.
     * <p>
     * This supplier allows for lazy initialization and dynamic configuration of the logging backend.
     * The same supplier instance is shared among all loggers created by this factory, enabling
     * consistent logging behavior across the application.
     */
    @NotNull
    private final Supplier<TopicRecordConsumer> adapterSupplier;
    @Nullable
    private final Handler<EventRecord> issueRecordInitializer;

    /**
     * Cache for storing created logger instances to ensure singleton behavior per logger name.
     * <p>
     * This cache prevents the creation of multiple logger instances for the same name,
     * which is a requirement of the SLF4J specification. The cache uses an everlasting
     * strategy, meaning logger instances are retained for the lifetime of the factory.
     */
    private final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();

    /**
     * Constructs a new KeelLoggerFactory with the specified adapter supplier.
     * <p>
     * The adapter supplier will be used to obtain {@link TopicRecordConsumer} instances
     * for all loggers created by this factory. The supplier should return a consistent
     * adapter instance or instances with compatible configuration.
     *
     * @param adapterSupplier the supplier for obtaining issue recorder adapter instances;
     *                        must not be null and should return non-null adapters
     * @throws NullPointerException if adapterSupplier is null
     */
    public KeelLoggerFactory(
            @NotNull Supplier<TopicRecordConsumer> adapterSupplier,
            @Nullable Handler<EventRecord> issueRecordInitializer) {
        this.adapterSupplier = adapterSupplier;
        this.issueRecordInitializer = issueRecordInitializer;
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
        if(loggerCache.containsKey(name)){
            return loggerCache.get(name);
        }else{
            synchronized (adapterSupplier) {
                var logger = new KeelSlf4jLogger(adapterSupplier, LogLevel.INFO, name, issueRecordInitializer);
                //Keel.getLogger().notice("Keel Logging for slf4j built logger for [" + name + "]");
                System.out.println("Keel Logging for slf4j built logger for [" + name + "]");
                loggerCache.put(name, logger);
                return logger;
            }
        }
    }
}
