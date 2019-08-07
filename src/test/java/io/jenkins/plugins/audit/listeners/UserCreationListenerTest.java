package io.jenkins.plugins.audit.listeners;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.User;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.security.pages.SignupPage;
import jenkins.model.Jenkins;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.acegisecurity.Authentication;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


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
        assertEventCount(app.getEvents(), 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(realm);

        User user = realm.createAccount(username, password);
        user.save();

        assertEventCount(app.getEvents(), expectedCount);
    }

    @Issue("JENKINS-54088")
    @Test
    public void testUserCreationFromSignUp() throws Exception {
        assertEventCount(app.getEvents(), 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(realm);

        SignupPage signup = new SignupPage(client.goTo("signup"));
        signup.enterUsername("debbie");
        signup.enterPassword(USERS.get("debbie"));
        signup.enterFullName("Debbie User");
        HtmlPage success = signup.submit(j);

        // user creation via a jenkins signup also automatically logs the user in
        assertEventCount(app.getEvents(), 2);

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
        assertEventCount(app.getEvents(), 0);

        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(realm);

        User u1 = realm.createAccount("charlie", USERS.get("charlie"));
        u1.save();
        client.login("charlie", USERS.get("charlie"));

        // verify the audit event log messages as user creation and user login events
        StructuredDataMessage logMessageOne = (StructuredDataMessage) app.getEvents().get(0).getMessage();
        StructuredDataMessage logMessageTwo = (StructuredDataMessage) app.getEvents().get(1).getMessage();

        assertTrue(logMessageOne.toString().contains("createUser"));
        assertTrue(logMessageTwo.toString().contains("login"));

        // verify a login event occurred
        client.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("charlie", a.getName());

            return null;
        });

        assertEventCount(app.getEvents(), 2);
    }


}
