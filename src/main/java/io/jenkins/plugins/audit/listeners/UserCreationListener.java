package io.jenkins.plugins.audit.listeners;

import org.apache.logging.log4j.audit.LogEventFactory;

import hudson.Extension;
import hudson.ExtensionList;
import io.jenkins.plugins.audit.event.CreateUser;
import javax.annotation.Nonnull;
import jenkins.security.SecurityListener;

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
    protected void userCreated(@Nonnull String username) {
        CreateUser user = LogEventFactory.getEvent(CreateUser.class);

        user.setUserId(username);
        user.logEvent();
    }

    /**
     * Returns all the registered {@link UserCreationListener}s.
     */
    public static ExtensionList<UserCreationListener> getInstance() {
        return ExtensionList.lookup(UserCreationListener.class);
    }


}
