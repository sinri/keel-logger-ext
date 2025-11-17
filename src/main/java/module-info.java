module io.github.sinri.keel.logger.ext {
    requires io.github.sinri.keel.logger.api;
    requires org.apache.logging.log4j;
    requires org.slf4j;

    exports io.github.sinri.keel.logger.ext.slf4j;
    exports io.github.sinri.keel.logger.ext.log4j2;
}