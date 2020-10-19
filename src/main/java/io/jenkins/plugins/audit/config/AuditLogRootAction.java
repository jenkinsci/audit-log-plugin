package io.jenkins.plugins.audit.config;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.DirectoryBrowserSupport;
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
        return "auditLog" ;
    }

    public DirectoryBrowserSupport doDynamic() {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        FilePath fp = Jenkins.get().getRootPath().child("logs").child("audit").child("html");
        return new DirectoryBrowserSupport(this, fp,"Audit Logs","notepad.png",true);
    }
}
