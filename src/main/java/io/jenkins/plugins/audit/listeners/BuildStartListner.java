package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import org.apache.logging.log4j.audit.LogEventFactory;
import io.jenkins.plugins.audit.event.BuildStart;

import java.util.Date;


@Extension
public class BuildStartListner extends RunListener<Run> {
    /**
     * Fired when a build is started, event logged via Log4j-audit.
     *
     * @param run of type Run having the build information
     * @param listener of type TaskListener that the onStarted method expects
     */
    @Override
    public void onStarted(Run run, TaskListener listener) {
        BuildStart buildStart = LogEventFactory.getEvent(BuildStart.class);

        buildStart.setBuildNumber(run.getNumber());
        //run.getCause(<?>).getShortDescription(); Not sure how to get the cause.
        //buildStart.setCause(run.getCauses()); Returns list of causes
        buildStart.setCause("Manual");
        buildStart.setProjectName(run.getParent().getFullName());
        buildStart.setTimestamp(new Date(run.getStartTimeInMillis()).toString());

        buildStart.logEvent();
    }

    /**
     * Returns a registered {@link BuildStartListner} instance.
     */
    public static ExtensionList<BuildStartListner> getInstance() {
        return ExtensionList.lookup(BuildStartListner.class);
    }
}
