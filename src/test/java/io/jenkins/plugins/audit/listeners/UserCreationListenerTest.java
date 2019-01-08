package io.jenkins.plugins.audit.listeners;

import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
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
import static org.hamcrest.Matchers.containsString;

import hudson.model.User;
import jenkins.model.Jenkins;
import junitparams.Parameters;
import junitparams.JUnitParamsRunner;
import hudson.security.pages.SignupPage;
import hudson.security.HudsonPrivateSecurityRealm;


@RunWith(JUnitParamsRunner.class)
public class UserCreationListenerTest {

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

        // credentials of four Jenkins accounts
        USERS.put("alice", "alicePassword");
        USERS.put("bob", "bobPassword");
        USERS.put("charlie", "charliePassword");
        USERS.put("debbie", "debbiePassword");

        client = j.createWebClient();
        logout(client);

        app = ListAppender.getListAppender("AuditList").clear();
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-54088")
    @Test
    @Parameters({
            "1, alice, alicePassword",
            "1, bob, bobPassword",
            "1, charlie, charliePassword",
            "1, debbie, debbiePassword"
    })
    public void testUserCreationFromRealm(int expectedCount, String username, String password) throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(realm);

        User user = realm.createAccount(username, password);
        user.save();

        assertEventCount(events, expectedCount);
    }

    @Issue("JENKINS-54088")
    @Test
    public void testUserCreationFromSignUp() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(realm);

        SignupPage signup = new SignupPage(client.goTo("signup"));
        signup.enterUsername("debbie");
        signup.enterPassword(USERS.get("debbie"));
        signup.enterFullName("Debbie User");
        HtmlPage success = signup.submit(j);

        // user creation via a jenkins signup also automatically logs the user in
        assertEventCount(events, 2);

        // verify a login event occurred
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("debbie", a.getName());

            return null;
        });

        assertThat(success.getElementById("main-panel").getTextContent(), containsString("Success"));
        assertEquals("Debbie User", realm.getUser("debbie").getDisplayName());
    }

    @Issue("JENKINS-54088")
    @Test
    public void testUserCreationAndLoginFromRealm() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(realm);

        User u1 = realm.createAccount("charlie", USERS.get("charlie"));
        u1.save();
        client.login("charlie", USERS.get("charlie"));

        // verify the audit event log messages as user creation and user login events
        StructuredDataMessage logMessageOne = (StructuredDataMessage) events.get(0).getMessage();
        StructuredDataMessage logMessageTwo = (StructuredDataMessage) events.get(1).getMessage();

        assertTrue(logMessageOne.toString().contains("createUser"));
        assertTrue(logMessageTwo.toString().contains("login"));

        // verify a login event occurred
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("charlie", a.getName());

            return null;
        });

        assertEventCount(events, 2);
    }


}
