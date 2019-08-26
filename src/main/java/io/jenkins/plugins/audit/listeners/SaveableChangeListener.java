package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Fingerprint;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import io.jenkins.plugins.audit.event.UseCredentials;
import org.apache.logging.log4j.audit.LogEventFactory;

import java.util.Hashtable;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.formatDateISO;


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

        if(o instanceof Fingerprint){
            UseCredentials useCredentials = LogEventFactory.getEvent(UseCredentials.class);
            Fingerprint fp = (Fingerprint) o;
            useCredentials.setFileName(fp.getFileName());
            useCredentials.setName(fp.getDisplayName());
            useCredentials.setTimestamp(formatDateISO(fp.getTimestamp().getTime()));
            Hashtable<String, Fingerprint.RangeSet> usages = fp.getUsages();
            if (usages != null) {
                usages.values().forEach(value -> {
                    useCredentials.setUsage(value.toString());
                    useCredentials.logEvent();
                });
            }
        }


    }

}
