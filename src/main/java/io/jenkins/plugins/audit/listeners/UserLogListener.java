/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jenkins.plugins.audit.listeners;

import hudson.ExtensionList;
import hudson.Extension;
import jenkins.security.SecurityListener;
import io.jenkins.plugins.audit.RequestContext;

import java.time.Instant;
import javax.annotation.Nonnull;

import io.jenkins.plugins.audit.event.Login;
import io.jenkins.plugins.audit.event.Logout;
import org.apache.logging.log4j.audit.LogEventFactory;
import org.acegisecurity.userdetails.UserDetails;
import org.kohsuke.stapler.Stapler;

/**
 * Listener notified of user login and logout events.
 */
@Extension
public class UserLogListener extends SecurityListener {

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
    @Override
    protected void loggedIn(@Nonnull String username) {
        String currentTime = Instant.now().toString();
        Login login = LogEventFactory.getEvent(Login.class);

        RequestContext.setIpAddress(Stapler.getCurrentRequest().getRemoteAddr());
        login.setUserId(username);
        login.setTimestamp(currentTime);
        login.logEvent();
        RequestContext.clear();
    }

    /**
     * Fired when a user has logged out, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged out.
     */
     @Override
     protected void loggedOut(@Nonnull String username) {
         String currentTime = Instant.now().toString();
         Logout logout = LogEventFactory.getEvent(Logout.class);

         RequestContext.setIpAddress(Stapler.getCurrentRequest().getRemoteAddr());
         logout.setUserId(username);
         logout.setTimestamp(currentTime);
         logout.logEvent();
         RequestContext.clear();
     }

     /**
      * Returns all the registered {@link UserLogListener}s.
      */
      public static ExtensionList<UserLogListener> all() {
          return ExtensionList.lookup(UserLogListener.class);
      }
}
