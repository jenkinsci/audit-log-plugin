package io.jenkins.plugins.audit.listeners;

import java.util.List;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.xml.sax.SAXException;
import org.acegisecurity.Authentication;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.rules.ExpectedException;
import org.apache.logging.log4j.core.LogEvent;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.jenkinsci.plugins.mocksecurityrealm.MockSecurityRealm;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.containsString;

import jenkins.model.Jenkins;
import jenkins.model.IdStrategy;
import junitparams.Parameters;
import junitparams.JUnitParamsRunner;
import hudson.security.SecurityRealm;

@RunWith(JUnitParamsRunner.class)
public class UserLoginListenerTest {

    private ListAppender app;
    private WebClient client;

    private final String USERS = "alice admin\nbob dev\ncharlie qa\ndebbie admin qa";
    private final SecurityRealm realm = new MockSecurityRealm(USERS, null, false,
            IdStrategy.CASE_INSENSITIVE, IdStrategy.CASE_INSENSITIVE);

    private static void assertEventCount(final List<LogEvent> events, final int expected) {
        assertEquals("Incorrect number of events.", expected, events.size());
    }

    private static WebClient logout(final WebClient wc) throws IOException, SAXException {
        wc.goTo("logout");
        return wc;
    }

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws IOException, SAXException {
        // setup a mock security realm with dummy usernames
        j.jenkins.setSecurityRealm(realm);
        client = j.createWebClient();
        logout(client);

        app = ListAppender.getListAppender("AuditList").clear();
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-54087")
    @Test
    @Parameters({
            "1, alice, alice, alice",
            "1, bob, bob, bob",
            "1, charlie, charlie, charlie",
            "1, debbie, debbie, debbie"
    })
    public void testValidUserLoginEventsLogged(int expectedCount, String expected, String username, String password) throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        client.login(username, password);

        assertEventCount(events, expectedCount);

        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals(expected, a.getName());

            return null;
        });
    }

    @Issue("JENKINS-54087")
    @Test
    public void testValidUsernameInMessageLogged() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        client.login("debbie", "debbie");
        StructuredDataMessage logMessage = (StructuredDataMessage) events.get(0).getMessage();

        assertEventCount(events, 1);
        assertTrue(logMessage.toString().contains("login"));
        assertEquals("debbie", logMessage.get("userId"));
    }

    @Issue("JENKINS-54087")
    @Test
    public void testInvalidUserLoginFailsWithError() throws Exception {
        List<LogEvent> events = app.getEvents();

        expectedException.expect(FailingHttpStatusCodeException.class);
        expectedException.expectMessage(containsString("Unauthorized"));
        client.login("john", "john");

        assertEventCount(events, 0);
    }


}
