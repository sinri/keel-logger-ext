package io.github.sinri.keel.logger.ext.slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeelSLF4JServiceProviderTest {

    @Test
    public void testInitializeAndGetters() {
        KeelSLF4JServiceProvider provider = new KeelSLF4JServiceProvider();

        provider.initialize();

        Assertions.assertNotNull(provider.getLoggerFactory());
        Assertions.assertInstanceOf(KeelSlf4jLoggerFactory.class, provider.getLoggerFactory());

        Assertions.assertNull(provider.getMarkerFactory());
        Assertions.assertNull(provider.getMDCAdapter());
        Assertions.assertEquals("2.0.17", provider.getRequestedApiVersion());
    }
}
