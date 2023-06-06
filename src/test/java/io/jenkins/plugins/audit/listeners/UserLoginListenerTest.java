package io.jenkins.plugins.audit.listeners;

import org.htmlunit.FailingHttpStatusCodeException;
import hudson.security.SecurityRealm;
import jenkins.model.IdStrategy;
import jenkins.model.Jenkins;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
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
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEventCount(app.getEvents(), 0);

        client.login(username, password);

        assertEventCount(app.getEvents(), expectedCount);

        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals(expected, a.getName());

            return null;
        });
    }

    @Issue("JENKINS-54087")
    @Test
    public void testValidUsernameInMessageLogged() throws Exception {
        assertEventCount(app.getEvents(), 0);

        client.login("debbie", "debbie");
        StructuredDataMessage logMessage = (StructuredDataMessage) app.getEvents().get(0).getMessage();

        assertEventCount(app.getEvents(), 1);
        assertTrue(logMessage.toString().contains("login"));
        assertEquals("debbie", logMessage.get("userId"));
    }

    @Issue("JENKINS-54087")
    @Test
    public void testInvalidUserLoginFailsWithError() throws Exception {

        expectedException.expect(FailingHttpStatusCodeException.class);
        expectedException.expectMessage(containsString("Unauthorized"));
        client.login("john", "john");

        assertEventCount(app.getEvents(), 0);
    }


}
