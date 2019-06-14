package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Fingerprint;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import jenkins.model.FingerprintFacet;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@Extension
public class SavableChangeListener extends SaveableListener {
    @Override
    public void onChange(Saveable o, XmlFile file) {
        System.out.println("Inside onChange");
        System.out.println(o);

        if(o instanceof Fingerprint){
            Fingerprint fp = (Fingerprint) o;
            Collection<FingerprintFacet> fbc =  fp.getFacets();
            System.out.println(fbc);
        }
    }

}
