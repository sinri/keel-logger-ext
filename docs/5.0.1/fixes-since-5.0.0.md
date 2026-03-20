# 5.0.1 修复计划（基于 5.0.0 的问题清单）

## 1. [BUG] KeelSlf4jLoggerFactory.getLogger() 竞态条件

**严重程度**：高  
**文件**：`KeelSlf4jLoggerFactory.java`

`getLogger()` 方法在 `containsKey` 检查与 `synchronized` 块之间存在 TOCTOU 竞态条件，
多线程并发获取同名 Logger 时可能创建多个实例并相互覆盖，违反 SLF4J 规范。

**修复方案**：改用 `ConcurrentHashMap.computeIfAbsent()` 替代手动 containsKey + synchronized 模式。

## 2. [设计] SLF4J 侧 visibleBaseLevel 硬编码为 INFO

**严重程度**：中  
**文件**：`KeelSlf4jLoggerFactory.java`、`KeelSLF4JServiceProvider.java`

与 Log4j2 侧不对称：Log4j2 侧可通过构造函数 / Provider 子类配置 visibleBaseLevel，
而 SLF4J 侧在 `getLogger()` 中硬编码为 `LogLevel.INFO`，无法调整。

**修复方案**：

- `KeelSlf4jLoggerFactory` 构造函数增加 `visibleBaseLevel` 参数。
- `KeelSLF4JServiceProvider` 增加可重写的 `getVisibleBaseLevel()` 方法。

## 3. [风险] getMarkerFactory() / getMDCAdapter() 返回 null

**严重程度**：中  
**文件**：`KeelSLF4JServiceProvider.java`

返回 null 会导致第三方库调用 `MarkerFactory.getMarker()` 或 `MDC.put()` 时抛出 NPE。

**修复方案**：返回 SLF4J 内置的 `BasicMarkerFactory` 和 `NOPMDCAdapter` 作为无操作实现。

## 4. [设计] 同步锁对象选择不当

**严重程度**：低  
**文件**：`KeelSlf4jLoggerFactory.java`

使用外部传入的 `adapterSupplier` 作为 synchronized 锁对象存在风险。

**修复方案**：随问题 1 一并修复，改用 `computeIfAbsent()` 后无需显式锁。

## 5. [设计] KeelLog4j2LoggerContext.getLogger(name, messageFactory) 忽略 MessageFactory

**严重程度**：低  
**文件**：`KeelLog4j2LoggerContext.java`

MessageFactory 参数被静默忽略。

**修复方案**：在 verbose 模式下输出警告日志，提示 MessageFactory 不被支持。

## 6. [小问题] getRequestedApiVersion() 硬编码版本号

**严重程度**：低  
**文件**：`KeelSLF4JServiceProvider.java`

版本号 "2.0.17" 与 gradle.properties 中 slf4jApiVersion 耦合但未关联。

**修复方案**：改为定义常量并在 JavaDoc 中注明与 gradle.properties 的关联。

## 7. [构建] Jackson 依赖以 api 作用域引入

**严重程度**：低  
**文件**：`build.gradle.kts`

本项目不直接使用 Jackson，以 `api` 作用域引入会污染消费者依赖树。

**修复方案**：改用 `constraints` 块声明版本覆盖。

## 8. [拼写] gradle.properties 中 "Denpendency" 拼写错误

**严重程度**：低  
**文件**：`gradle.properties`

**修复方案**：修正为 "Dependency"。
