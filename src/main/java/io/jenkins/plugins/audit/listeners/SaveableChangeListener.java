package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Fingerprint;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import io.jenkins.plugins.audit.event.CredentialsUsage;
import org.apache.logging.log4j.audit.LogEventFactory;


@Extension
public class SaveableChangeListener extends SaveableListener {
    /**
     * Fired when a saveable object is created. But for now this is customized to only log
     * the saveable fingerprint instances.
     *
     * @param o the saveable object.
     * @param file the XmlFile for this saveable object.
     */
    @Override
    public void onChange(Saveable o, XmlFile file) {
        CredentialsUsage credentialsUsage = LogEventFactory.getEvent(CredentialsUsage.class);
        if(o instanceof Fingerprint){

            Fingerprint fp = (Fingerprint) o;
            credentialsUsage.setFileName(fp.getFileName());
            credentialsUsage.setName(fp.getDisplayName());
            credentialsUsage.setTimestamp(fp.getTimestampString());
            fp.getUsages().forEach((k, v) ->{
                credentialsUsage.setUsage(v.toString());
                credentialsUsage.logEvent();
            });
        }


    }

}
