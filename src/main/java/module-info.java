module io.github.sinri.keel.logger.ext {
    requires io.github.sinri.keel.logger.api;
    requires org.apache.logging.log4j;
    requires org.slf4j;

    exports io.github.sinri.keel.logger.ext.slf4j;
    exports io.github.sinri.keel.logger.ext.log4j2;

    provides org.apache.logging.log4j.spi.LoggerContextFactory
            with io.github.sinri.keel.logger.ext.log4j2.KeelLog4j2LoggerContextFactory;
    provides org.slf4j.spi.SLF4JServiceProvider
            with io.github.sinri.keel.logger.ext.slf4j.KeelSLF4JServiceProvider;

    uses org.apache.logging.log4j.spi.LoggerContextFactory;
    uses org.slf4j.spi.SLF4JServiceProvider;
}