package io.jenkins.plugins.audit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;

@Plugin(name = "AuditTestConfigFactory", category = ConfigurationFactory.CATEGORY)
@Order(100)
public class TestConfigurationFactory extends ConfigurationFactory {
    @Override
    protected String[] getSupportedTypes() {
        return new String[]{"*"};
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
        return createConfiguration(name, ConfigurationBuilderFactory.newConfigurationBuilder());
    }

    private static Configuration createConfiguration(String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        return builder.setConfigurationName(name)
                .setStatusLevel(Level.WARN)
                .setPackages("io.jenkins.plugins.audit.plugins")
                .setConfigurationName("JenkinsAuditLog")
                .add(builder.newAppender("AuditList", "List"))
                .add(builder.newLogger("AuditLogger", Level.ALL).add(builder.newAppenderRef("AuditList")))
                .add(builder.newRootLogger(Level.OFF))
                .build();
    }
}
