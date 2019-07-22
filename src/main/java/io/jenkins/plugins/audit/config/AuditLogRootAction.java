package io.jenkins.plugins.audit.config;

import hudson.Extension;
import hudson.model.RootAction;
import jenkins.model.Jenkins;

@Extension
public class AuditLogRootAction implements RootAction {
    @Override
    public String getIconFileName() {
        return "notepad.png";
    }

    @Override
    public String getDisplayName() {
        return "Audit Logs";
    }

    @Override
    public String getUrlName() {
        return Jenkins.get().getRootUrl() + "userContent/audit-log-plugin" ;
    }
}
