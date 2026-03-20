# Keel Logger Extension — AI 编码工具参考文档

> 本文档专为 AI 编码助手（Cursor、Copilot、Codeium 等）设计，
> 提供项目的结构化元信息，帮助 AI 快速理解代码库并生成正确的修改。

## 项目身份

- **名称**：keel-logger-ext
- **GroupId**：io.github.sinri
- **版本**：5.0.1-SNAPSHOT（发布时为 5.0.1）
- **语言**：Java 17
- **构建工具**：Gradle (Kotlin DSL)
- **模块名**：`io.github.sinri.keel.logger.ext`（JPMS）
- **许可证**：GPL-v3.0

## 项目用途

将 SLF4J 2.x 和 Log4j2 的标准日志 API 桥接到 Keel 自研日志系统（`keel-logger-api`）。
通过 Java SPI 机制自动加载，第三方库的日志输出被统一路由到 Keel 的 `LogWriterAdapter`。

## 依赖关系

```
keel-logger-ext
├── api: org.slf4j:slf4j-api:2.0.17
├── api: org.apache.logging.log4j:log4j-api:2.24.3
├── api: io.github.sinri:keel-logger-api:5.0.1
├── compileOnly: org.jspecify:jspecify:1.0.0
├── constraints: com.fasterxml.jackson.core:jackson-core:2.18.6  (漏洞修复覆盖)
└── testImplementation: io.vertx:vertx-junit5:5.0.8
```

## 源码结构

```
src/main/java/
├── module-info.java
└── io/github/sinri/keel/logger/ext/
    ├── package-info.java
    ├── slf4j/
    │   ├── package-info.java
    │   ├── KeelSLF4JServiceProvider.java   ← SPI 入口 (public)，子类可重写扩展点
    │   ├── KeelSlf4jLoggerFactory.java     ← ILoggerFactory 实现 (final 包私有)
    │   └── KeelSlf4jLogger.java            ← Logger 实现 (final 包私有)
    └── log4j2/
        ├── package-info.java
        ├── KeelLog4j2LoggerContextFactory.java  ← SPI 入口 (public final)
        ├── KeelLog4j2LoggerContext.java          ← LoggerContext 实现 (final 包私有)
        ├── KeelLog4j2Logger.java                 ← AbstractLogger 实现 (final 包私有)
        └── KeelLog4j2Provider.java               ← @Deprecated (public)

src/main/resources/META-INF/services/
├── org.apache.logging.log4j.spi.LoggerContextFactory  → KeelLog4j2LoggerContextFactory
└── org.slf4j.spi.SLF4JServiceProvider                 → KeelSLF4JServiceProvider

src/test/java/  (JUnit 5 + Vert.x JUnit5)
├── SpiTest.java                                 ← SPI 发现验证
├── slf4j/
│   ├── KeelSLF4JServiceProviderTest.java
│   ├── KeelSlf4jLoggerFactoryTest.java
│   └── KeelSlf4jLoggerTest.java
└── log4j2/
    ├── KeelLog4j2LoggerContextFactoryTest.java
    └── KeelLog4j2LoggerTest.java
```

## 类关系图

```
SLF4J 侧：
  SLF4JServiceProvider (SPI接口)
    └── KeelSLF4JServiceProvider (public，可继承)
            ├── 创建 → KeelSlf4jLoggerFactory (final 包私有)
            │             └── 创建 → KeelSlf4jLogger (final 包私有)
            │                          └── 委托 → LogWriterAdapter (keel-logger-api)
            ├── getAdapterSupplier()      ← 可重写：自定义日志输出后端
            ├── getVisibleBaseLevel()     ← 可重写：自定义最小日志级别 (默认 INFO)
            └── getLogInitializer()       ← 可重写：自定义日志初始化器

Log4j2 侧：
  LoggerContextFactory (SPI接口)
    └── KeelLog4j2LoggerContextFactory (public final)
            └── 持有 → KeelLog4j2LoggerContext (final 包私有)
                          └── 创建 → KeelLog4j2Logger (final 包私有)
                                       └── 委托 → LogWriterAdapter (keel-logger-api)

  Provider (已废弃)
    └── KeelLog4j2Provider (@Deprecated，public，可继承)
            ├── getAdapterSupplier()      ← 可重写
            ├── getVisibleBaseLevel()     ← 可重写
            └── getLogInitializer()       ← 可重写
```

## 核心设计模式与约束

### SPI 自动发现

- SLF4J：通过 `META-INF/services/org.slf4j.spi.SLF4JServiceProvider` + `module-info.java` 中 `provides` 声明
- Log4j2：通过 `META-INF/services/org.apache.logging.log4j.spi.LoggerContextFactory` + `module-info.java` 中
  `provides` 声明
- 修改 SPI 入口类时需同步更新三处：Java 文件、services 文件、module-info.java

### Logger 缓存策略

- SLF4J 侧：`ConcurrentHashMap.computeIfAbsent()` 保证线程安全与实例唯一
- Log4j2 侧：`containsKey` + `synchronized(loggerMap)` 双重检查锁定保证线程安全与实例唯一
- 两侧的 Logger 缓存均为常驻（不支持移除），生命周期与工厂/上下文一致

### Supplier 延迟获取模式

- 所有 Logger 通过 `Supplier<LogWriterAdapter>` 获取适配器，支持延迟初始化和运行时切换
- `adapterSupplier.get()` 可能返回 null（当 Keel 日志系统尚未初始化时），此时日志静默丢弃

### 级别映射

- SLF4J WARN → Keel WARNING
- Log4j2 WARN → Keel WARNING
- Log4j2 FATAL → Keel FATAL
- Log4j2 ALL → Keel TRACE
- Log4j2 OFF → Keel SILENT
- Keel NOTICE → Log4j2 INFO（双向映射中 NOTICE 被合并到 INFO）

### Marker / MDC 处理

- SLF4J Marker → Keel classification 列表（包含自身名称 + 引用链中所有名称）
- SLF4J MDC → 无实际效果（`NOPMDCAdapter`），建议使用 Keel `Log.context()` 替代
- Log4j2 ThreadContext → 自动写入 Keel `Log.context()`

## 编码规范

- 不使用 Lombok
- JavaDoc 中使用 `<p>` 分隔段落，不写闭合标签 `</p>`
- 使用 `@NullMarked`（jspecify）标注类级别的 null 安全性
- 使用 `@Nullable` 标注可空参数/返回值
- 包私有类声明为 `final`
- public 类根据扩展需求决定是否 `final`
- 测试使用 JUnit 5（`@Test`、`Assertions`）
- 所有源文件编码为 UTF-8
- 日志系统属性 `keel.logger.ext.verbose=true` 控制调试输出

## 构建命令

```bash
./gradlew compileJava          # 编译主代码
./gradlew compileTestJava      # 编译测试代码
./gradlew test                 # 运行测试
./gradlew javadoc              # 生成 JavaDoc
./gradlew publish              # 发布（SNAPSHOT → 内部 Nexus，正式版 → Sonatype Central）
```

## 发布策略

- 版本号含 `SNAPSHOT` → 发布到内部 Nexus Snapshots 仓库
- 版本号含 `-alpha`/`-beta` 等预发布后缀 → 发布到内部 Nexus Releases 仓库
- 纯数字版本号（如 `5.0.1`）→ 发布到 Sonatype Central（Maven Central），自动签名
- 签名使用 GnuPG，密钥配置在 `gradle.properties` 中

## 修改代码时的注意事项

1. **SPI 三点一致性**：新增或修改 SPI 实现时，确保 Java 类、`META-INF/services/` 文件、`module-info.java` 三处同步更新
2. **构造函数兼容性**：`KeelSlf4jLoggerFactory` 和 `KeelLog4j2LoggerContext` 是包私有的，修改其构造函数不影响外部使用者；但
   `KeelSLF4JServiceProvider` 和 `KeelLog4j2LoggerContextFactory` 是 public 的，修改其签名属于破坏性变更
3. **线程安全**：Logger 缓存的线程安全是核心约束，任何修改都必须保证同名 Logger 返回同一实例
4. **null 安全**：`adapterSupplier.get()` 可能返回 null，所有调用处需防御处理
5. **版本号同步**：`KeelSLF4JServiceProvider.REQUESTED_API_VERSION` 常量需与 `gradle.properties` 中的
   `slf4jApiVersion` 保持一致
6. **测试覆盖**：修改任何 public / protected 方法后应更新对应测试
7. **Jackson 依赖**：以 `constraints` 方式引入，仅覆盖传递依赖的版本，项目本身不使用 Jackson
