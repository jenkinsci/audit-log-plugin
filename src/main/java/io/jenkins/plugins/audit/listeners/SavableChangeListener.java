package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Fingerprint;
import hudson.model.FingerprintCleanupThread;
import hudson.model.FingerprintMap;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import hudson.tasks.Fingerprinter;
import io.jenkins.plugins.audit.event.CredentialsUsage;
import jenkins.model.FingerprintFacet;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@Extension
public class SavableChangeListener extends SaveableListener {
    @Override
    public void onChange(Saveable o, XmlFile file) {
        CredentialsUsage credentialsUsage = LogEventFactory.getEvent(CredentialsUsage.class);
        if(o instanceof Fingerprint){
            Fingerprint fp = (Fingerprint) o;
            credentialsUsage.setFileName(fp.getFileName());
            credentialsUsage.setName(fp.getDisplayName());
            credentialsUsage.setTimestamp(fp.getTimestampString());
            credentialsUsage.setUsages(fp.getUsages().toString());

            credentialsUsage.logEvent();
        }


    }

}
