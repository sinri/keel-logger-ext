package io.github.sinri.keel.logger.ext.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.adapter.BaseLogWriter;
import io.github.sinri.keel.logger.api.adapter.LogWriterAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.function.Supplier;

public class KeelLog4j2LoggerContextFactoryTest {

    @Test
    public void testGetContext() {
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;

        KeelLog4j2LoggerContextFactory factory = new KeelLog4j2LoggerContextFactory(adapterSupplier, LogLevel.INFO,
                null);

        LoggerContext context1 = factory.getContext("fqcn", null, null, true);
        Assertions.assertNotNull(context1);
        Assertions.assertInstanceOf(KeelLog4j2LoggerContext.class, context1);

        LoggerContext context2 = factory.getContext("fqcn", null, null, true, URI.create("file:///tmp"), "name");
        Assertions.assertNotNull(context2);
        Assertions.assertSame(context1, context2);
    }

    @Test
    public void testRemoveContext() {
        LogWriterAdapter adapter = new BaseLogWriter() {
            @Override
            public void accept(String topic, io.github.sinri.keel.logger.api.log.SpecificLog<?> log) {
            }
        };
        Supplier<LogWriterAdapter> adapterSupplier = () -> adapter;

        KeelLog4j2LoggerContextFactory factory = new KeelLog4j2LoggerContextFactory(adapterSupplier, LogLevel.INFO,
                null);
        LoggerContext context = factory.getContext("fqcn", null, null, true);

        Assertions.assertDoesNotThrow(() -> factory.removeContext(context));
    }
}
