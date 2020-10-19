package io.jenkins.plugins.audit.listeners;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.audit.event.BuildFinish;
import io.jenkins.plugins.audit.event.BuildStart;
import org.apache.logging.log4j.audit.LogEventFactory;

import java.util.stream.Collectors;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.currentDateTimeISO;
import static io.jenkins.plugins.audit.helpers.DateTimeHelper.formatDateISO;

@Extension
public class BuildListener extends RunListener<Run<?, ?>> {
    /**
     * Fired when a build is started, event logged via Log4j-audit.
     *
     * @param run      of type Run having the build information
     * @param listener of type TaskListener that the onStarted method expects
     */
    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        BuildStart buildStart = LogEventFactory.getEvent(BuildStart.class);

        buildStart.setBuildNumber(run.getNumber());
        buildStart.setCause(run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.toList()));
        buildStart.setProjectName(run.getParent().getFullName());
        buildStart.setTimestamp(formatDateISO(run.getStartTimeInMillis()));

        buildStart.logEvent();
    }

    /**
     * Fired when a build is completed, event logged via Log4j-audit.
     *
     * @param run      of type Run having the build information
     * @param listener of type TaskListener that the onCompleted method expects
     */
    @Override
    public void onCompleted(Run<?, ?> run, @NonNull TaskListener listener) {
        BuildFinish buildFinish = LogEventFactory.getEvent(BuildFinish.class);

        // Run.getStartTime() + Run.getDuration() might not be fully accurate
        buildFinish.setTimestamp(currentDateTimeISO());
        buildFinish.setBuildNumber(run.getNumber());
        buildFinish.setCause(run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.toList()));
        buildFinish.setProjectName(run.getParent().getFullName());

        buildFinish.logEvent();
    }

    /**
     * Returns a registered {@link BuildListener} instance.
     */
    public static ExtensionList<BuildListener> getInstance() {
        return ExtensionList.lookup(BuildListener.class);
    }
}
