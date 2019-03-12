package io.jenkins.plugins.audit.listeners;

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
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
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

public class UserPasswordLogListenerTest {

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
    public void updateUserPassword() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);

        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        // new user account creation
        SignupPage signup = new SignupPage(client.goTo("signup"));
        signup.enterUsername("alice");
        signup.enterPassword("alice");
        signup.enterFullName(StringUtils.capitalize("alice user"));
        HtmlPage p = signup.submit(j);

        // verify a login event occurred after account creation
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("alice", a.getName());

            return null;
        });

        // execute an http request to change a user's password from their config page
        User alice = User.getById("alice", false);
        URL configPage = client.createCrumbedUrl(alice.getUrl() + "/" + "configSubmit");
        String formData = "{\"fullName\": \"alice user\", \"description\": \"\", \"userProperty2\": {\"primaryViewName\": \"\"}, \"userProperty4\": {\"user.password\": \"admin\", \"$redact\": [\"user.password\", \"user.password2\"], \"user.password2\": \"admin\"}, \"userProperty5\": {\"authorizedKeys\": \"\"}, \"userProperty7\": {\"insensitiveSearch\": true}, \"core:apply\": \"true\"}";

        WebRequest request = new WebRequest(configPage, HttpMethod.POST);
        request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestBody("json=" + URLEncoder.encode(formData, StandardCharsets.UTF_8.name()));
        Page page = client.getPage(request);

        // ensure user whose password was changed was in fact logged
        StructuredDataMessage logMessage = (StructuredDataMessage) events.get(2).getMessage();
        assertTrue(logMessage.toString().contains("updatePassword"));
        assertEventCount(events, 3);
    }

}
