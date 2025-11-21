package io.github.sinri.keel.logger.ext;

import io.github.sinri.keel.logger.ext.log4j2.KeelLog4j2LoggerContextFactory;
import io.github.sinri.keel.logger.ext.slf4j.KeelSLF4JServiceProvider;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.spi.SLF4JServiceProvider;

import java.util.ServiceLoader;

public class SpiTest {

    @Test
    public void testLog4j2Spi() {
        ServiceLoader<LoggerContextFactory> loader = ServiceLoader.load(LoggerContextFactory.class);
        boolean found = false;
        for (LoggerContextFactory factory : loader) {
            if (factory instanceof KeelLog4j2LoggerContextFactory) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "KeelLog4j2LoggerContextFactory should be found via SPI");
    }

    @Test
    public void testSlf4jSpi() {
        ServiceLoader<SLF4JServiceProvider> loader = ServiceLoader.load(SLF4JServiceProvider.class);
        boolean found = false;
        for (SLF4JServiceProvider provider : loader) {
            if (provider instanceof KeelSLF4JServiceProvider) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "KeelSLF4JServiceProvider should be found via SPI");
    }
}
