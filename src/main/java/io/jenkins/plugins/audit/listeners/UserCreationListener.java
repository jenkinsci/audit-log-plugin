package io.jenkins.plugins.audit.listeners;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import io.jenkins.plugins.audit.event.CreateUser;
import jenkins.security.SecurityListener;
import org.apache.logging.log4j.audit.LogEventFactory;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.currentDateTimeISO;

/**
 * Listener notified of user creation events.
 */
@Extension
public class UserCreationListener extends SecurityListener {

    /**
     * Fired when a new user account has been created.
     *
     * @param username the user
     */
    @Override
    protected void userCreated(@NonNull String username) {
        CreateUser user = LogEventFactory.getEvent(CreateUser.class);

        user.setUserId(username);
        user.setTimestamp(currentDateTimeISO());
        user.logEvent();
    }

    /**
     * Returns a registered {@link UserCreationListener} instance.
     */
    public static ExtensionList<UserCreationListener> getInstance() {
        return ExtensionList.lookup(UserCreationListener.class);
    }


}
