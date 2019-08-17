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

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.UserProperty;

import io.jenkins.plugins.audit.event.CreateKey;
import jenkins.security.ApiTokenProperty;
import jenkins.security.ApiTokenPropertyListener;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;

/**
 * Listener notified of api token key creation events.
 */
@Extension
public class ApiKeyCreationListener extends ApiTokenPropertyListener {

    /**
     * Fired when a new user property has been created.
     *
     * @param username the user
     * @param value property that was newly created.
     *
     */
    @Override
    public void onCreated(@Nonnull String username, @Nonnull UserProperty value) {
        if (value instanceof ApiTokenProperty) {
            CreateKey user = LogEventFactory.getEvent(CreateKey.class);

            user.setUserId(username);
            user.logEvent();
        }
    }

    /**
     * Returns a registered {@link ApiKeyCreationListener} instance.
     */
    public static ExtensionList<ApiKeyCreationListener> getInstance() { return ExtensionList.lookup(ApiKeyCreationListener.class); }

}
