package io.jenkins.plugins.audit.listeners;

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

import hudson.security.PasswordPropertyListener;
import io.jenkins.plugins.audit.event.UpdatePassword;
import org.apache.logging.log4j.audit.LogEventFactory;

import hudson.Extension;
import hudson.ExtensionList;

import javax.annotation.Nonnull;

/**
 * Listener which logs password-update audit events.
 */
@Extension
public class UserPasswordLogListener extends PasswordPropertyListener {

    /**
     * Fired when a user password property has been updated and will log the event.
     *
     * @param username the user
     * @param oldValue old property of the user
     * @param newValue new property of the user
     *
     */
    @Override
    public void onChanged(@Nonnull String username, @Nonnull Object oldValue, @Nonnull Object newValue) {
        UpdatePassword user = LogEventFactory.getEvent(UpdatePassword.class);

        user.setUserId(username);
        user.logEvent();
    }

    /**
     * Returns a registered {@link UserPasswordLogListener} instance.
     */
    public static ExtensionList<UserPasswordLogListener> getInstance() { return ExtensionList.lookup(UserPasswordLogListener.class); }

}
