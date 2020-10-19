package io.jenkins.plugins.audit.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class AuditLogConfiguration extends GlobalConfiguration {

    public static AuditLogConfiguration getInstance() {
        return ExtensionList.lookupSingleton(AuditLogConfiguration.class);
    }

    private String logDestination;
    private String appenderType;
    private String syslogHost;
    private int syslogPort;
    private int enterpriseNumber;

    public AuditLogConfiguration() {
        load();
    }

    @NonNull
    @Override
    public GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(GlobalConfigurationCategory.Security.class);
    }

    public String getAppenderType() {
        return StringUtils.defaultIfBlank(appenderType, "jsonLayout");
    }

    @DataBoundSetter
    public void setAppenderType(String appenderType) {
        this.appenderType = appenderType;
        save();
        reloadLogger();
    }

    public String getLogDestination() {
        String defaultStr = Jenkins.get().getRootDir().toPath().resolve("logs").resolve("audit").resolve("audit.log").toString();
        return StringUtils.defaultIfBlank(logDestination, defaultStr);
    }

    @DataBoundSetter
    public void setLogDestination(String logDestination) {
        this.logDestination = logDestination;
        save();
        reloadLogger();
    }

    public String getSyslogHost() {
        return StringUtils.defaultIfBlank(syslogHost, "localhost");
    }

    @DataBoundSetter
    public void setSyslogHost(String syslogHost) {
        this.syslogHost = syslogHost;
        save();
        reloadLogger();
    }

    public int getSyslogPort() {
        return syslogPort > 0 ? syslogPort : 1854;
    }

    @DataBoundSetter
    public void setSyslogPort(int syslogPort) {
        this.syslogPort = syslogPort;
        save();
        reloadLogger();
    }

    public int getEnterpriseNumber() {
        return enterpriseNumber > 0 ? enterpriseNumber : 18060;
    }

    @DataBoundSetter
    public void setEnterpriseNumber(int enterpriseNumber) {
        this.enterpriseNumber = enterpriseNumber;
        save();
        reloadLogger();
    }

    private void reloadLogger() {
        org.apache.logging.log4j.spi.LoggerContext loggerContext = LogManager.getContext(false);
        if (loggerContext instanceof LoggerContext) {
            ((LoggerContext) loggerContext).reconfigure();
        }
    }

    public ListBoxModel doFillAppenderTypeItems() {
        ListBoxModel items = new ListBoxModel();
        items.add("jsonLayout");
        items.add("syslogLayout");
        return items;
    }
}
