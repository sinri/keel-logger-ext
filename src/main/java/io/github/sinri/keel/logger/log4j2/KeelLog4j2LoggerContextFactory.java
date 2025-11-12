package io.github.sinri.keel.logger.log4j2;

import io.github.sinri.keel.logger.api.LogLevel;
import io.github.sinri.keel.logger.api.consumer.TopicRecordConsumer;
import io.github.sinri.keel.logger.api.event.EventRecord;
import io.vertx.core.Handler;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.function.Supplier;

public final class KeelLog4j2LoggerContextFactory implements LoggerContextFactory {

    private final KeelLog4j2LoggerContext loggerContext;

    public KeelLog4j2LoggerContextFactory(
            @Nonnull Supplier<TopicRecordConsumer> adapterSupplier,
            @Nonnull LogLevel visibleBaseLevel,
            @Nullable Handler<EventRecord> issueRecordInitializer
    ) {
        this.loggerContext = new KeelLog4j2LoggerContext(adapterSupplier, visibleBaseLevel, issueRecordInitializer);
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext) {
        return this.loggerContext;
    }

    @Override
    public LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name) {
        return this.loggerContext;
    }

    @Override
    public void removeContext(LoggerContext context) {
        // do nothing
    }
}
