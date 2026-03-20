package io.github.sinri.keel.logger.ext.slf4j;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@NullMarked
public class KeelSLF4JServiceProviderTest {

    @Test
    public void testInitializeAndGetters() {
        KeelSLF4JServiceProvider provider = new KeelSLF4JServiceProvider();

        provider.initialize();

        Assertions.assertNotNull(provider.getLoggerFactory());
        Assertions.assertInstanceOf(KeelSlf4jLoggerFactory.class, provider.getLoggerFactory());

        Assertions.assertNotNull(provider.getMarkerFactory());
        Assertions.assertNotNull(provider.getMDCAdapter());
        Assertions.assertEquals(KeelSLF4JServiceProvider.REQUESTED_API_VERSION, provider.getRequestedApiVersion());
    }
}
