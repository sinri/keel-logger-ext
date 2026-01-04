package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 在 Keel 日志体系下封装实现的 slf4j 体系日志记录服务提供者。
 * <p>
 *  <ul>
 *      <li>{@link #getMarkerFactory()} 返回 {@code null}，不支持标记功能</li>
 *      <li>{@link #getMDCAdapter()} 返回 {@code null}，不支持 MDC 功能</li>
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
     * Keel 日志记录器工厂实例。
     * <p>
     * 在 {@link #initialize()} 方法中初始化，负责创建和缓存 {@link KeelSlf4jLogger} 实例。
     */
    private KeelSlf4jLoggerFactory loggerFactory;

    /**
     * 获取日志记录器工厂实例。
     * <p>
     * 该方法被声明为 {@code final}，确保子类无法修改日志记录器工厂的获取逻辑。
     *
     * @return Keel 日志记录器工厂实例
     */
    @Override
    public final KeelSlf4jLoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * 获取标记工厂实例。
     * <p>
     * 当前实现不支持 SLF4J 标记功能，始终返回 {@code null}。
     * 这意味着使用 {@link org.slf4j.Marker} 相关的日志方法时，
     * 标记信息将被忽略。
     *
     * @return {@code null} - 不支持标记功能
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return null;
    }

    /**
     * 获取映射诊断上下文 (MDC) 适配器。
     * <p>
     * 当前实现不支持 SLF4J MDC 功能，始终返回 {@code null}。
     * 这意味着 {@link org.slf4j.MDC} 相关操作将不会生效。
     * <p>
     * 建议使用 Keel 日志系统的 {@code attribute} 和 {@code context}
     * 方法来实现类似的上下文追踪功能。
     *
     * @return {@code null} - 不支持 MDC 功能
     */
    @Override
    public MDCAdapter getMDCAdapter() {
        return null;
    }

    /**
     * 获取所请求的 SLF4J API 版本。
     * <p>
     * 指定与 SLF4J 2.0.17 版本兼容，这是 Keel 日志系统
     * 当前支持和测试的 SLF4J 版本。
     *
     * @return SLF4J API 版本号 "2.0.17"
     */
    @Override
    public String getRequestedApiVersion() {
        return "2.0.17";
    }

    /**
     * 初始化 SLF4J 服务提供者。
     * <p>
     * 该方法被声明为 {@code final}，确保子类无法修改初始化逻辑。
     * 在初始化过程中，会创建一个新的 {@link KeelSlf4jLoggerFactory} 实例，
     * 该工厂使用通过 {@link #getAdapterSupplier()} 方法获取的适配器提供者。
     * <p>
     * 初始化完成后，可以通过 {@link #getLoggerFactory()} 方法获取
     * 日志记录器工厂实例。
     */
    @Override
    public final void initialize() {
        loggerFactory = new KeelSlf4jLoggerFactory(getAdapterSupplier(), getLogInitializer(), isVerbose());
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

    @Nullable
    protected Consumer<Log> getLogInitializer() {
        return null;
    }
}
