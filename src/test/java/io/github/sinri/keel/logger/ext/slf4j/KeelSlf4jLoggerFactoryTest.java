package io.github.sinri.keel.logger.ext.slf4j;

import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class KeelSlf4jLoggerFactoryTest {

    @Test
    public void testGetLogger() {
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;
        KeelSlf4jLoggerFactory factory = new KeelSlf4jLoggerFactory(adapterSupplier, null, true);

        Logger logger = factory.getLogger("test-logger");
        Assertions.assertNotNull(logger);
        Assertions.assertInstanceOf(KeelSlf4jLogger.class, logger);
        Assertions.assertEquals("test-logger", logger.getName());
    }
}
