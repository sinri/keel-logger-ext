package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import io.github.sinri.keel.logger.api.log.Log;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@NullMarked
public class KeelSlf4jLoggerTest {

    @Test
    public void testTraceLevel() {
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

        KeelSlf4jLogger logger = new KeelSlf4jLogger(adapterSupplier, LogLevel.TRACE, "test-topic", null);

        Assertions.assertTrue(logger.isTraceEnabled());

        logger.trace("trace message");
        Assertions.assertEquals(1, logs.size());
        Assertions.assertEquals(LogLevel.TRACE, logs.get(0).level());
        Assertions.assertEquals("trace message", logs.get(0).message());

        logs.clear();
        logger.trace("trace message with arg {}", "arg1");
        Assertions.assertEquals(1, logs.size());
        Assertions.assertEquals("trace message with arg arg1", logs.get(0).message());
    }

    @Test
    public void testTraceLevelDisabled() {
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

        // Set visible level to DEBUG, so TRACE should be disabled
        KeelSlf4jLogger logger = new KeelSlf4jLogger(adapterSupplier, LogLevel.DEBUG, "test-topic", null);

        Assertions.assertFalse(logger.isTraceEnabled());

        logger.trace("trace message");
        Assertions.assertEquals(0, logs.size());
    }
}
