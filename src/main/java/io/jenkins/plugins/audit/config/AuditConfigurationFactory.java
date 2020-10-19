package io.jenkins.plugins.audit.config;

import jenkins.model.Jenkins;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;
import java.nio.file.Path;

@Plugin(name = "AuditConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class AuditConfigurationFactory extends ConfigurationFactory {

    private static Configuration createConfiguration(String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        AuditLogConfiguration auditLogConfiguration = AuditLogConfiguration.getInstance();
        builder.setConfigurationName(name)
                .setStatusLevel(Level.ERROR)
                .setPackages("io.jenkins.plugins.audit.plugins")
                .setConfigurationName("JenkinsAuditLog");
        Path logs = Jenkins.get().getRootDir().toPath().resolve("logs").resolve("audit");
        // HTML audit log browser (keeps 30 days of logs currently)
        builder.add(builder.newAppender("html", "RollingRandomAccessFile")
                .addAttribute("fileName", logs.resolve("html").resolve("audit.html"))
                .addAttribute("filePattern", logs.resolve("html").resolve("audit-%d{yyyy-MM-dd}.html"))
                .add(builder.newLayout("CustomHTMLLayout")
                        .addAttribute("title", "Audit Log")
                        .addAttribute("charset", "UTF-8")
                        .addAttribute("locationInfo", false))
                .addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
                .addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", 30)));
        // not trying to pollute any logs here
        builder.add(builder.newRootLogger(Level.OFF));
        LoggerComponentBuilder auditLogger = builder.newLogger("AuditLogger", Level.ALL)
                .addAttribute("additivity", false)
                .add(builder.newAppenderRef("html"));
        String appenderType = auditLogConfiguration.getAppenderType();
        if (appenderType.equals("jsonLayout")) {
            String auditLogDestination = auditLogConfiguration.getLogDestination();
            builder.add(builder.newAppender("audit", "RollingRandomAccessFile")
                    .addAttribute("fileName", auditLogDestination)
                    .addAttribute("filePattern", auditLogDestination + ".%d{yyyyMMdd_HHmmss}-%i.log.gz")
                    .add(builder.newLayout("JsonLayout").addAttribute("properties", true))
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "50 MB"))
                    .addComponent(builder.newComponent("DefaultRolloverStrategy").addAttribute("max", 30)));
            auditLogger.add(builder.newAppenderRef("audit"));
        } else if (appenderType.equals("syslogLayout")) {
            builder.add(builder.newAppender("audit", "Syslog")
                    .addAttribute("format", "RFC5424")
                    .addAttribute("host", auditLogConfiguration.getSyslogHost())
                    .addAttribute("port", auditLogConfiguration.getSyslogPort())
                    .addAttribute("protocol", "TCP")
                    .addAttribute("appName", "ALP")
                    .addAttribute("mdcId", "mdc")
                    .addAttribute("includeMDC", true)
                    .addAttribute("facility", "LOCAL0")
                    .addAttribute("enterpriseNumber", auditLogConfiguration.getEnterpriseNumber())
                    .addAttribute("newLine", true)
                    .addAttribute("messageId", "Server")
                    .addAttribute("id", "App"));
            auditLogger.add(builder.newAppenderRef("audit"));
        }
        return builder.add(auditLogger).build();
    }

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
}
