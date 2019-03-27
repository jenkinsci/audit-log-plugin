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
import io.jenkins.plugins.audit.event.BuildFinish;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.formatDateISO;


@Extension
public class BuildListener extends RunListener<Run> {
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
        buildStart.setTimestamp(formatDateISO(run.getStartTimeInMillis()));
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

        Instant start = Instant.ofEpochMilli(run.getStartTimeInMillis());
        Instant finish = start.plusMillis(run.getDuration());
        buildFinish.setTimestamp(formatDateISO(finish.toEpochMilli()));

        User user = User.current();
        if(user != null)
            buildFinish.setUserId(user.getId());
        else
            buildFinish.setUserId(null);

        buildFinish.logEvent();
    }



    /**
     * Returns a registered {@link BuildListener} instance.
     */
    public static ExtensionList<BuildListener> getInstance() {
        return ExtensionList.lookup(BuildListener.class);
    }
}
