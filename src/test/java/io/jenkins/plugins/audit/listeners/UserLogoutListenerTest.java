package io.jenkins.plugins.audit.listeners;

import java.util.List;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
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

import static org.junit.Assert.*;

import jenkins.model.Jenkins;
import jenkins.model.IdStrategy;
import hudson.security.SecurityRealm;

public class UserLogoutListenerTest {

    private ListAppender app;
    private WebClient client;

    private static final String USERS = "alice admin\nbob dev\ncharlie qa\ndebbie admin qa";
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
    public void testValidLogoutEventLogged() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        client.login("alice", "alice");

        // ensure we are logged in
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("alice", a.getName());

            return null;
        });

        // do logout
        logout(client);

        // ensure we logged out
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertNotEquals("alice", a.getName());

            return null;
        });

        // verify login - logout events
        assertEventCount(events, 2);
    }

    @Issue("JENKINS-54087")
    @Test
    public void testValidUsernameInMessageLogged() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        client.login("debbie", "debbie");
        logout(client);

        StructuredDataMessage logMessage = (StructuredDataMessage) events.get(1).getMessage();

        assertEventCount(events, 2);
        assertTrue(logMessage.toString().contains("logout"));
        assertTrue(logMessage.toString().contains("userId"));
        assertTrue(logMessage.toString().contains("debbie"));
    }


}
