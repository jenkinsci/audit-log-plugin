package io.jenkins.plugins.audit.listeners;


import hudson.Extension;

import hudson.ExtensionList;
import hudson.Extension;
import io.jenkins.plugins.audit.RequestContext;
import jenkins.security.SecurityListener;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Instant;

import io.jenkins.plugins.audit.event.CreateUser;
import org.acegisecurity.userdetails.User;
import org.apache.logging.log4j.audit.LogEventFactory;
import org.acegisecurity.userdetails.UserDetails;

/**
 * Listener notified of user creation events.
 */
@Extension
public class UserCreationListener extends SecurityListener {

    /**
     * Fired when a user was successfully authenticated using credentials. It could be password or any other credentials.
     * This might be via the web UI, or via REST (using API token or Basic), or CLI (remoting, auth, ssh)
     * or any other way plugins can propose.
     *
     * @param details details of the newly authenticated user, such as name and groups.
     */
    protected void authenticated(@Nonnull UserDetails details) {}

    /**
     * Fired when a user has failed to log in.
     * Would be called after {@link #failedToAuthenticate}.
     *
     * @param username the user
     */
    protected void failedToLogIn(@Nonnull String username) {}

    /**
     * Fired when a user tried to authenticate but failed.
     * UserLogListener does not use this abstract class method, however, rather than leaving
     * its implementation empty it is made to throw an exception if the method is ever called.
     *
     * @param username the user
     * @see #authenticated
     */
    protected void failedToAuthenticate(@Nonnull String username) {}

    /**
     * Fired when a user has logged in, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged in.
     */
    protected void loggedIn(@Nonnull String username) {}

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
     * Fired when a user has logged out, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged out.
     */
    protected void loggedOut(@Nonnull String username) {}

    /**
     * Returns all the registered {@link UserLogListener}s.
     */
    public static ExtensionList<UserCreationListener> all() {
        return ExtensionList.lookup(UserCreationListener.class);
    }
}
