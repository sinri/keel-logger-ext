# Keel Logger Extension 5.0.1 开发者指南

## 概述

Keel Logger Extension 是一个日志桥接库，将业界标准的 **SLF4J 2.x** 和 **Log4j2** 日志 API
桥接到 Keel 自研日志系统（`keel-logger-api`）。

当应用引入本库后，所有通过 SLF4J 或 Log4j2 API 输出的日志（包括来自第三方库如 Vert.x、Netty 等的日志）
都会被统一路由到 Keel 日志系统进行处理和输出。

### 基本原理

```
第三方库 / 业务代码
    │
    ├── SLF4J API ──→ KeelSLF4JServiceProvider ──→ KeelSlf4jLoggerFactory ──→ KeelSlf4jLogger ──┐
    │                                                                                            │
    └── Log4j2 API ─→ KeelLog4j2LoggerContextFactory ─→ KeelLog4j2LoggerContext ─→ KeelLog4j2Logger ─┤
                                                                                                     │
                                                                                                     ▼
                                                                            Keel LogWriterAdapter（统一日志输出）
```

桥接通过 **Java SPI** 机制自动生效——只需将本库加入 classpath，无需额外配置。

## 环境要求

- Java 17+
- keel-logger-api 5.0.1
- SLF4J 2.0.17
- Log4j2 API 2.24.3

## 快速开始

### 引入依赖

Gradle（Kotlin DSL）：

```kotlin
dependencies {
    implementation("io.github.sinri:keel-logger-ext:5.0.1")
}
```

Maven：

```xml

<dependency>
    <groupId>io.github.sinri</groupId>
    <artifactId>keel-logger-ext</artifactId>
    <version>5.0.1</version>
</dependency>
```

引入后 SLF4J 和 Log4j2 的日志输出自动路由到 Keel 日志系统，无需额外配置。

### 调试模式

通过 JVM 系统属性开启详细日志，可观察 Logger 的创建过程：

```
-Dkeel.logger.ext.verbose=true
```

## 架构详解

### SLF4J 桥接

| 类                          | 职责                                                                               |
|----------------------------|----------------------------------------------------------------------------------|
| `KeelSLF4JServiceProvider` | SLF4J SPI 入口。通过 `META-INF/services/org.slf4j.spi.SLF4JServiceProvider` 被发现和加载    |
| `KeelSlf4jLoggerFactory`   | 实现 `ILoggerFactory`。以 `ConcurrentHashMap.computeIfAbsent` 保证同名 Logger 实例唯一（线程安全） |
| `KeelSlf4jLogger`          | 实现 `org.slf4j.Logger`。将 SLF4J 日志调用转换为 Keel `Log` 对象并交给 `LogWriterAdapter` 处理     |

**映射规则：**

| SLF4J 概念                            | Keel 概念                                          |
|-------------------------------------|--------------------------------------------------|
| Logger name                         | topic                                            |
| Level (TRACE/DEBUG/INFO/WARN/ERROR) | LogLevel (TRACE/DEBUG/INFO/WARNING/ERROR)        |
| Marker（及其引用链）                       | classification（分类列表）                             |
| MDC                                 | 不映射（返回 NOPMDCAdapter，建议使用 Keel 的 `context` 方法替代） |

### Log4j2 桥接

| 类                                | 职责                                                                                         |
|----------------------------------|--------------------------------------------------------------------------------------------|
| `KeelLog4j2LoggerContextFactory` | Log4j2 SPI 入口。通过 `META-INF/services/org.apache.logging.log4j.spi.LoggerContextFactory` 被发现 |
| `KeelLog4j2LoggerContext`        | 实现 `LoggerContext`。管理 Logger 实例缓存（双重检查锁定保证线程安全）                                            |
| `KeelLog4j2Logger`               | 继承 `AbstractLogger`。将 Log4j2 日志调用转换为 Keel `Log` 对象                                         |
| `KeelLog4j2Provider`             | **已废弃**。旧版 SPI 入口，保留仅为向后兼容                                                                 |

**映射规则：**

| Log4j2 概念                                     | Keel 概念                                                |
|-----------------------------------------------|--------------------------------------------------------|
| Logger name                                   | topic                                                  |
| Level (TRACE/DEBUG/INFO/WARN/ERROR/FATAL/OFF) | LogLevel (TRACE/DEBUG/INFO/WARNING/ERROR/FATAL/SILENT) |
| ThreadContext (MDC)                           | context（键值对自动写入日志事件）                                   |
| MessageFactory                                | 不支持自定义（verbose 模式下会输出警告）                               |

### 日志级别过滤

两侧都支持 `visibleBaseLevel`（可见基础级别），低于该级别的日志不会被处理。
默认值为 `LogLevel.INFO`。

## 自定义扩展

### 自定义 SLF4J 服务提供者

继承 `KeelSLF4JServiceProvider` 并重写以下方法：

```java
public class MyServiceProvider extends KeelSLF4JServiceProvider {

    @Override
    protected Supplier<LogWriterAdapter> getAdapterSupplier() {
        return MyLogWriter::getInstance;
    }

    @Override
    protected LogLevel getVisibleBaseLevel() {
        return LogLevel.DEBUG;  // 允许 DEBUG 及以上级别
    }

    @Override
    protected Consumer<Log> getLogInitializer() {
        return log -> log.context("app", "my-app");  // 每条日志自动添加应用标识
    }
}
```

然后在 `META-INF/services/org.slf4j.spi.SLF4JServiceProvider` 中替换为你的类名。

### 自定义 Log4j2 工厂

通过构造函数参数化 `KeelLog4j2LoggerContextFactory`：

```java
new KeelLog4j2LoggerContextFactory(
        MyLogWriter::getInstance,   // 自定义适配器
        LogLevel.DEBUG,             // 可见基础级别
        log ->log.

context("app","my-app")  // 日志初始化器
);
```

## 5.0.1 版本变更

### Bug 修复

- **KeelSlf4jLoggerFactory 竞态条件**：`getLogger()` 方法存在 TOCTOU 竞态，
  多线程下可能为同一名称创建多个 Logger 实例，违反 SLF4J 规范。
  已改用 `ConcurrentHashMap.computeIfAbsent()` 彻底修复。

### 功能改进

- **SLF4J 日志级别可配置**：`KeelSLF4JServiceProvider` 新增 `getVisibleBaseLevel()` 可重写方法，
  与 Log4j2 侧行为对称。`KeelSlf4jLoggerFactory` 构造函数新增 `visibleBaseLevel` 参数。

- **MarkerFactory / MDCAdapter 不再返回 null**：改为返回 SLF4J 内置的 `BasicMarkerFactory` 和
  `NOPMDCAdapter`。第三方库调用 `MarkerFactory.getMarker()` 或 `MDC.put()` 时不再抛出 NPE。

- **SLF4J API 版本号常量化**：`KeelSLF4JServiceProvider.REQUESTED_API_VERSION` 常量可被外部引用。

- **Log4j2 MessageFactory 警告**：`KeelLog4j2LoggerContext.getLogger(name, messageFactory)` 在
  verbose 模式下会输出 MessageFactory 被忽略的警告。

### 构建改进

- **Jackson 依赖优化**：从 `api` 作用域改为 `constraints` 块，不再污染消费者依赖树。
- **拼写修正**：`gradle.properties` 中 "Denpendency" → "Dependency"。

### 破坏性变更

- `KeelSlf4jLoggerFactory` 构造函数签名变更：新增 `visibleBaseLevel` 参数（第二个参数）。
  由于该类为 `final` 包私有类，对外部使用者无影响。
- `KeelSLF4JServiceProvider.getMarkerFactory()` 和 `getMDCAdapter()` 不再返回 `null`。
  如果消费代码依赖 null 返回值做判断，需要调整。
