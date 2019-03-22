package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.model.listeners.RunListener;
import org.apache.logging.log4j.audit.LogEventFactory;
import io.jenkins.plugins.audit.event.BuildStart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Extension
public class BuildStartListener extends RunListener<Run> {
    /**
     * Fired when a build is started, event logged via Log4j-audit.
     *
     * @param run of type Run having the build information
     * @param listener of type TaskListener that the onStarted method expects
     */
    @Override
    public void onStarted(Run run, TaskListener listener) {
        BuildStart buildStart = LogEventFactory.getEvent(BuildStart.class);

        List causeObjects = run.getCauses();
        List<String> causes = new ArrayList<>(causeObjects.size());
        for (Object cause: causeObjects) {
            Cause c = (Cause)cause;
            causes.add(c.getShortDescription());
        }

        buildStart.setBuildNumber(run.getNumber());
        buildStart.setCause(causes);
        buildStart.setProjectName(run.getParent().getFullName());
        buildStart.setTimestamp(new Date(run.getStartTimeInMillis()).toString());
        User user = User.current();
        if(user != null)
            buildStart.setUserId(user.getId());
        else
            buildStart.setUserId(null);

        buildStart.logEvent();
    }

    /**
     * Fired when a build is completed, event logged via Log4j-audit.
     *
     * @param run of type Run having the build information
     * @param listener of type TaskListener that the onCompleted method expects
     */
    @Override
    public void onCompleted(Run run, TaskListener listener) {
        BuildFinish buildFinish = LogEventFactory.getEvent(BuildFinish.class);

        List causeObjects = run.getCauses();
        List<String> causes = new ArrayList<>(causeObjects.size());
        for (Object cause: causeObjects) {
            Cause c = (Cause)cause;
            causes.add(c.getShortDescription());
        }

        buildFinish.setBuildNumber(run.getNumber());
        buildFinish.setCause(causes);
        buildFinish.setProjectName(run.getParent().getFullName());
        buildFinish.setTimestamp(new Date(run.getStartTimeInMillis()).toString());
        User user = User.current();
        if(user != null)
            buildFinish.setUserId(user.getId());
        else
            buildFinish.setUserId(null);

        buildFinish.logEvent();
    }



    /**
     * Returns a registered {@link BuildStartListener} instance.
     */
    public static ExtensionList<BuildStartListener> getInstance() {
        return ExtensionList.lookup(BuildStartListener.class);
    }
}
