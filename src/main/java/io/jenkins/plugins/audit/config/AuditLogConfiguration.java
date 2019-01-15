package io.jenkins.plugins.audit.config;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.apache.logging.log4j.core.LoggerContext;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Objects;

@Extension
public class AuditLogConfiguration extends GlobalConfiguration {
    private String logDestination;

    public AuditLogConfiguration() {
        load();
        reloadLogger();
    }

    public String getLogDestination() {
        return logDestination;
    }

    @DataBoundSetter
    public void setLogDestination(String logDestination) {
        this.logDestination = logDestination;
        save();
        reloadLogger();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        AuditLogConfiguration that = (AuditLogConfiguration) o;
        return Objects.equals(getLogDestination(), that.getLogDestination());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogDestination());
    }

    private void reloadLogger() {
        if(this.logDestination != null){
            System.setProperty("auditFileName", this.logDestination);
        } else {
            System.clearProperty("auditFileName");
        }
        LoggerContext.getContext(false).reconfigure();
    }
}
