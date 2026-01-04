package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.SimpleMessage;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@NullMarked
public class KeelLog4j2LoggerTest {

    @Test
    public void testLogMessage() {
        List<Log> logs = new ArrayList<>();
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
                if (log instanceof Log) {
                    logs.add((Log) log);
                }
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;

        KeelLog4j2Logger logger = new KeelLog4j2Logger(adapterSupplier, LogLevel.TRACE, "test-topic", null, true);

        logger.logMessage("fqcn", Level.INFO, null, new SimpleMessage("info message"), null);

        Assertions.assertEquals(1, logs.size());
        Assertions.assertEquals(LogLevel.INFO, logs.get(0).level());
        Assertions.assertEquals("info message", logs.get(0).message());
    }

    @Test
    public void testThreadContext() {
        List<Log> logs = new ArrayList<>();
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
                if (log instanceof Log) {
                    logs.add((Log) log);
                }
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;

        KeelLog4j2Logger logger = new KeelLog4j2Logger(adapterSupplier, LogLevel.TRACE, "test-topic", null, true);

        ThreadContext.put("key", "value");
        try {
            logger.logMessage("fqcn", Level.INFO, null, new SimpleMessage("message with context"), null);
        } finally {
            ThreadContext.clearMap();
        }

        Assertions.assertEquals(1, logs.size());
        // Assertions.assertEquals("value", logs.get(0).context("key"));
    }

    @Test
    public void testLevelMapping() {
        List<Log> logs = new ArrayList<>();
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
                if (log instanceof Log) {
                    logs.add((Log) log);
                }
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;

        KeelLog4j2Logger logger = new KeelLog4j2Logger(adapterSupplier, LogLevel.TRACE, "test-topic", null, true);

        logger.logMessage("fqcn", Level.TRACE, null, new SimpleMessage("trace"), null);
        Assertions.assertEquals(LogLevel.TRACE, logs.get(0).level());
        logs.clear();

        logger.logMessage("fqcn", Level.DEBUG, null, new SimpleMessage("debug"), null);
        Assertions.assertEquals(LogLevel.DEBUG, logs.get(0).level());
        logs.clear();

        logger.logMessage("fqcn", Level.WARN, null, new SimpleMessage("warn"), null);
        Assertions.assertEquals(LogLevel.WARNING, logs.get(0).level());
        logs.clear();

        logger.logMessage("fqcn", Level.ERROR, null, new SimpleMessage("error"), null);
        Assertions.assertEquals(LogLevel.ERROR, logs.get(0).level());
        logs.clear();

        logger.logMessage("fqcn", Level.FATAL, null, new SimpleMessage("fatal"), null);
        Assertions.assertEquals(LogLevel.FATAL, logs.get(0).level());
        logs.clear();
    }
}
