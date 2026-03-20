# Keel Logger Extension

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.sinri/keel-logger-ext)
![License](https://img.shields.io/github/license/sinri/keel-logger-ext)
![Java](https://img.shields.io/badge/Java-17%2B-blue)

A logging bridge library that routes **SLF4J 2.x** and **Log4j2** API calls
to the [Keel](https://github.com/sinri/keel) logging system via Java SPI — no manual configuration needed.

## Features

- Automatic SPI discovery — add the dependency and it just works
- SLF4J 2.x Marker → Keel classification mapping
- Log4j2 ThreadContext → Keel context mapping
- Configurable minimum log level (`visibleBaseLevel`)
- Extensible via subclassing `KeelSLF4JServiceProvider`

## Requirements

- Java 17+
- [keel-logger-api](https://github.com/sinri/keel-logger-api) 5.0.1

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.sinri:keel-logger-ext:5.0.1")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.github.sinri:keel-logger-ext:5.0.1'
}
```

### Maven

```xml

<dependency>
    <groupId>io.github.sinri</groupId>
    <artifactId>keel-logger-ext</artifactId>
    <version>5.0.1</version>
</dependency>
```

## How It Works

```
Third-party libraries / Application code
    │
    ├── SLF4J API ──→ KeelSLF4JServiceProvider ──→ KeelSlf4jLoggerFactory ──→ KeelSlf4jLogger ──┐
    │                                                                                            │
    └── Log4j2 API ─→ KeelLog4j2LoggerContextFactory ─→ KeelLog4j2LoggerContext ─→ KeelLog4j2Logger ─┤
                                                                                                     ▼
                                                                            Keel LogWriterAdapter (unified output)
```

Once on the classpath, all SLF4J / Log4j2 log output is automatically routed
to Keel's `LogWriterAdapter` through Java SPI.

## Debug Mode

Pass the following JVM property to see logger creation details:

```
-Dkeel.logger.ext.verbose=true
```

## Documentation

Full documentation is available at the [GitHub Pages site](https://sinri.github.io/keel-logger-ext/).

- [Developer Guide](https://sinri.github.io/keel-logger-ext/5.0.1/developer-guide)
- [AI Coding Guide](https://sinri.github.io/keel-logger-ext/5.0.1/ai-coding-guide)

## License

[GPL-v3.0](https://www.gnu.org/licenses/gpl-3.0.txt)
