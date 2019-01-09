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

import org.apache.logging.log4j.audit.LogEventFactory;

import hudson.ExtensionList;
import hudson.Extension;
import io.jenkins.plugins.audit.event.Login;
import io.jenkins.plugins.audit.event.Logout;
import javax.annotation.Nonnull;
import jenkins.security.SecurityListener;

/**
 * Listener notified of user login and logout events.
 */
@Extension
public class UserLogListener extends SecurityListener {

    /**
     * Fired when a user has logged in, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged in.
     */
    @Override
    protected void loggedIn(@Nonnull String username) {
        Login login = LogEventFactory.getEvent(Login.class);

        login.setUserId(username);
        login.logEvent();
    }

    /**
     * Fired when a user has logged out, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged out.
     */
    @Override
    protected void loggedOut(@Nonnull String username) {
        Logout logout = LogEventFactory.getEvent(Logout.class);

        logout.setUserId(username);
        logout.logEvent();
    }

     /**
      * Returns a registered {@link UserLogListener} instance.
      */
      public static ExtensionList<UserLogListener> getInstance() {
          return ExtensionList.lookup(UserLogListener.class);
      }


}
