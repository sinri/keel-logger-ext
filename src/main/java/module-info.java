module io.github.sinri.keel.logger {
    requires io.vertx.core;
    requires io.github.sinri.keel.logger.api;
    requires io.github.sinri.keel.base;
    requires org.apache.logging.log4j;
    requires org.slf4j;

    exports io.github.sinri.keel.logger.slf4j;
    exports io.github.sinri.keel.logger.issue;
    exports io.github.sinri.keel.logger.event;
    exports io.github.sinri.keel.logger.consumer;
    exports io.github.sinri.keel.logger.factory;
    exports io.github.sinri.keel.logger.log4j2;
}