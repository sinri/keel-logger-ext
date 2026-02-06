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
 * 在 Keel 日志系统中封装实现的 SLF4J 日志记录器工厂。
 *
 * @see KeelSlf4jLogger
 * @see ILoggerFactory
 * @since 5.0.0
 */
@NullMarked
final class KeelSlf4jLoggerFactory implements ILoggerFactory {

    /**
     * 用于获取 {@link LogWriterAdapter} 实例的供应者。
     * <p>
     * 通过供应者可以实现延迟初始化以及运行期的后端适配器切换。
     * 工厂创建的所有日志记录器共享同一个供应者，从而保持一致的输出行为。
     */
    private final Supplier<LogWriterAdapter> adapterSupplier;
    private final @Nullable Consumer<Log> logInitializer;

    /**
     * 日志记录器缓存（按名称唯一）。
     * <p>
     * 按 SLF4J 规范要求：对同一个名称，多次获取应返回同一个 Logger 实例。
     * 这里使用常驻缓存策略，实例会在工厂生命周期内一直保留。
     */
    private final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();

    private final boolean verbose;

    /**
     * 创建日志记录器工厂。
     * <p>
     * 工厂创建的所有日志记录器都会通过该供应者获取 {@link LogWriterAdapter}，
     * 供应者应返回可用的适配器实例（建议保持配置一致）。
     *
     * @param adapterSupplier 用于获取日志写入适配器实例的供应者；不可为 null，且应返回非 null 的适配器
     * @param logInitializer  写入前用于初始化 {@link Log} 的钩子；可为 null
     * @param verbose         是否输出调试信息到标准输出
     * @throws NullPointerException 当 adapterSupplier 为 null 时抛出
     */
    public KeelSlf4jLoggerFactory(
            Supplier<LogWriterAdapter> adapterSupplier,
            @Nullable Consumer<Log> logInitializer, boolean verbose) {
        this.adapterSupplier = adapterSupplier;
        this.logInitializer = logInitializer;
        this.verbose = verbose;
    }

    /**
     * 获取指定名称的日志记录器实例。
     * <p>
     * 按 SLF4J 约定：对同一个名称，多次调用必须返回同一个 Logger 实例。
     * 若缓存中不存在，则创建新的 {@link KeelSlf4jLogger}，并使用：
     * <ul>
     *   <li>配置好的适配器供应者</li>
     *   <li>默认可见基础级别 {@link LogLevel#INFO}</li>
     *   <li>传入的名称作为日志主题（topic）</li>
     * </ul>
     * <p>
     * <strong>线程安全：</strong>创建过程以 adapterSupplier 作为锁进行同步，
     * 以避免多线程同时请求同名 Logger 时的竞态问题。
     *
     * @param name 日志记录器名称；通常使用类名或组件标识
     * @return 对应名称的日志记录器实例（不返回 null）
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
