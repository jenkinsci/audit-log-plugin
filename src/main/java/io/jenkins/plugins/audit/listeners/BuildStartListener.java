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

import static io.jenkins.plugins.audit.helpers.Factory.formatDateISO;


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
        buildStart.setTimestamp(formatDateISO(new Date(run.getStartTimeInMillis())));
        User user = User.current();
        if(user != null)
            buildStart.setUserId(user.getId());
        else
            buildStart.setUserId(null);

        buildStart.logEvent();
    }

    /**
     * Returns a registered {@link BuildStartListener} instance.
     */
    public static ExtensionList<BuildStartListener> getInstance() {
        return ExtensionList.lookup(BuildStartListener.class);
    }
}
