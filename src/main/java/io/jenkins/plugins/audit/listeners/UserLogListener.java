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

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import io.jenkins.plugins.audit.event.Login;
import io.jenkins.plugins.audit.event.Logout;
import jenkins.security.SecurityListener;
import org.apache.logging.log4j.audit.LogEventFactory;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.currentDateTimeISO;

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
    protected void loggedIn(@NonNull String username) {
        Login login = LogEventFactory.getEvent(Login.class);

        login.setUserId(username);
        login.setTimestamp(currentDateTimeISO());
        login.logEvent();
    }

    /**
     * Fired when a user has logged out, event logged via Log4j-audit.
     *
     * @param username name or ID of the user who logged out.
     */
    @Override
    protected void loggedOut(@NonNull String username) {
        Logout logout = LogEventFactory.getEvent(Logout.class);

        logout.setUserId(username);
        logout.setTimestamp(currentDateTimeISO());
        logout.logEvent();
    }

    /**
     * Returns a registered {@link UserLogListener} instance.
     */
    public static ExtensionList<UserLogListener> getInstance() {
        return ExtensionList.lookup(UserLogListener.class);
    }


}
