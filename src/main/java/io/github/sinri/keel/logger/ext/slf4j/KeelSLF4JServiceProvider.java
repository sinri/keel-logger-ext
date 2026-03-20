package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.LateObject;
import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 在 Keel 日志系统中封装实现的 SLF4J 日志记录服务提供者。
 * <p>
 *  <ul>
 *      <li>{@link #getMarkerFactory()} 返回 {@link BasicMarkerFactory}，提供基础标记支持</li>
 *      <li>{@link #getMDCAdapter()} 返回 {@link NOPMDCAdapter}，MDC 操作无实际效果</li>
 *      <li>建议使用 Keel 日志系统的 {@code attribute} 和 {@code context} 方法替代 MDC</li>
 *  </ul>
 * <p>
 * 可以使用本类或本类的集成类作为具体的服务提供者，通过 Java SPI 机制自动发现和加载，
 * 即在 {@code META-INF/services/org.slf4j.spi.SLF4JServiceProvider} 文件中声明具体的实现类。
 *
 * @author Sinri Edogawa
 * @see SLF4JServiceProvider
 * @see KeelSlf4jLoggerFactory
 * @see KeelSlf4jLogger
 * @since 5.0.0
 */
@NullMarked
public class KeelSLF4JServiceProvider implements SLF4JServiceProvider {

    /**
     * 本服务提供者所兼容的 SLF4J API 版本。
     * <p>
     * 更新 SLF4J 依赖版本（gradle.properties 中的 {@code slf4jApiVersion}）时应同步更新此常量。
     */
    public static final String REQUESTED_API_VERSION = "2.0.17";

    /**
     * Keel 日志记录器工厂实例。
     * <p>
     * 在 {@link #initialize()} 方法中初始化，负责创建和缓存 {@link KeelSlf4jLogger} 实例。
     */
    private final LateObject<KeelSlf4jLoggerFactory> lateLoggerFactory = new LateObject<>();
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();
    private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

    /**
     * 获取日志记录器工厂实例。
     * <p>
     * 该方法被声明为 {@code final}，确保子类无法修改日志记录器工厂的获取逻辑。
     *
     * @return Keel 日志记录器工厂实例
     */
    @Override
    public final ILoggerFactory getLoggerFactory() {
        return lateLoggerFactory.get();
    }

    /**
     * 获取标记工厂实例。
     * <p>
     * 返回 {@link BasicMarkerFactory}，提供基础的标记创建和管理支持。
     * <p>
     * 注意：日志记录器实现仍会在日志方法中接收 {@link org.slf4j.Marker} 参数，
     * 并将其转换为 Keel 的 classification（分类）信息。
     *
     * @return {@link BasicMarkerFactory} 实例
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    /**
     * 获取映射诊断上下文 (MDC) 适配器。
     * <p>
     * 返回 {@link NOPMDCAdapter}，{@link org.slf4j.MDC} 相关操作不会抛出异常，
     * 但也不会产生实际效果。
     * <p>
     * 建议使用 Keel 日志系统的 {@code attribute} 和 {@code context}
     * 方法来实现类似的上下文追踪功能。
     *
     * @return {@link NOPMDCAdapter} 实例
     */
    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    /**
     * 获取所请求的 SLF4J API 版本。
     * <p>
     * 指定与 SLF4J 2.0.17 版本兼容，这是 Keel 日志系统
     * 当前支持和测试的 SLF4J 版本。
     *
     * @return SLF4J API 版本号
     * @see #REQUESTED_API_VERSION
     */
    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    /**
     * 初始化 SLF4J 服务提供者。
     * <p>
     * 该方法被声明为 {@code final}，确保子类无法修改初始化逻辑。
     * 在初始化过程中，会创建一个新的 {@link KeelSlf4jLoggerFactory} 实例，
     * 该工厂使用通过 {@link #getAdapterSupplier()} 方法获取的适配器提供者，
     * 以及通过 {@link #getVisibleBaseLevel()} 方法获取的可见基础级别。
     * <p>
     * 初始化完成后，可以通过 {@link #getLoggerFactory()} 方法获取
     * 日志记录器工厂实例。
     */
    @Override
    public final void initialize() {
        var loggerFactory = new KeelSlf4jLoggerFactory(
                getAdapterSupplier(), getVisibleBaseLevel(), getLogInitializer(), isVerbose());
        lateLoggerFactory.set(loggerFactory);
    }

    public boolean isVerbose() {
        return System.getProperty("keel.logger.ext.verbose", "false").equalsIgnoreCase("true");
    }

    /**
     * 提供用于获取 {@link LogWriterAdapter} 实例的供应者。
     * <p>
     * 该方法返回一个 {@code Supplier}，用于延迟获取 Keel 日志系统的
     * 问题记录适配器实例。适配器实例通过 Keel 日志记录中心的
     * {@code issueRecordCenter().getAdapter()} 方法获取。
     * <p>
     * 子类可以重写此方法来提供自定义的适配器供应者实现，
     * 以满足特定的日志记录需求。
     *
     * @return 提供 {@link LogWriterAdapter} 实例的供应者。
     */
    protected Supplier<LogWriterAdapter> getAdapterSupplier() {
        return BaseLogWriter::getInstance;
    }

    /**
     * 提供对外可见的基础日志级别。
     * <p>
     * 子类可以重写本方法，用于自定义将被处理的最小日志级别（低于该级别的日志将被过滤）。
     * 默认返回 {@link LogLevel#INFO}。
     *
     * @return 将被处理的最小 {@link LogLevel}
     */
    protected LogLevel getVisibleBaseLevel() {
        return LogLevel.INFO;
    }

    @Nullable
    protected Consumer<Log> getLogInitializer() {
        return null;
    }
}
