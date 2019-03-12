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

import hudson.security.HudsonPrivateSecurityRealm;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import jenkins.security.ApiTokenProperty;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.jvnet.hudson.test.Issue;
import org.xml.sax.SAXException;
import org.acegisecurity.Authentication;
import org.jvnet.hudson.test.JenkinsRule;
import org.apache.logging.log4j.core.LogEvent;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.apache.logging.log4j.message.StructuredDataMessage;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import static org.junit.Assert.*;

import hudson.model.User;
import jenkins.model.Jenkins;
import hudson.security.pages.SignupPage;
import hudson.security.HudsonPrivateSecurityRealm;

public class ApiKeyCreationListenerTest {

    private ListAppender app;
    private WebClient client;
    private final HashMap<String, String> USERS = new HashMap<String, String>();

    private static void assertEventCount(final List<LogEvent> events, final int expected) {
        assertEquals("Incorrect number of events.", expected, events.size());
    }

    private static WebClient logout(final WebClient wc) throws IOException, SAXException {
        wc.goTo("logout");
        return wc;
    }

    @Rule
    public JenkinsRule j = new JenkinsRule();
    //{j.timeout = 0;} // disable timeouts

    @Before
    public void setup() throws Exception {
        // user ID conformance check
        Field field = HudsonPrivateSecurityRealm.class.getDeclaredField("ID_REGEX");
        field.setAccessible(true);
        field.set(null, null);

        client = j.createWebClient();
        logout(client);

        app = ListAppender.getListAppender("AuditList").clear();
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-55694")
    @Test
    public void createUserToken() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);

        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        // new user account creation
        SignupPage signup = new SignupPage(client.goTo("signup"));
        signup.enterUsername("charlie");
        signup.enterPassword("charlie");
        signup.enterFullName(StringUtils.capitalize("charlie user"));
        HtmlPage page = signup.submit(j);

        // execute an http request to create a new a user api token from their config page
        User charlie = User.getById("charlie", false);
        URL configPage = client.createCrumbedUrl(charlie.getUrl() + "/" + "/descriptorByName/" + ApiTokenProperty.class.getName() + "/generateNewToken/?newTokenName=" + "charlie-token");
        Page p = client.getPage(new WebRequest(configPage, HttpMethod.POST));

        // ensure user whose api token was created was in fact logged
        StructuredDataMessage logMessage = (StructuredDataMessage) events.get(2).getMessage();
        assertTrue(logMessage.toString().contains("createKey"));
        assertEventCount(events, 3);
    }
}
