package io.jenkins.plugins.audit.listeners;

import hudson.security.SecurityRealm;
import jenkins.model.IdStrategy;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.jenkinsci.plugins.mocksecurityrealm.MockSecurityRealm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        assertEventCount(app.getEvents(), 0);

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
        assertEventCount(app.getEvents(), 2);
    }

    @Issue("JENKINS-54087")
    @Test
    public void testValidUsernameInMessageLogged() throws Exception {
        assertEventCount(app.getEvents(), 0);

        client.login("debbie", "debbie");
        logout(client);

        StructuredDataMessage logMessage = (StructuredDataMessage) app.getEvents().get(1).getMessage();

        assertEventCount(app.getEvents(), 2);
        assertTrue(logMessage.toString().contains("logout"));
        assertEquals("debbie", logMessage.get("userId"));
    }


}
