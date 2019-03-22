package io.jenkins.plugins.audit.listeners;

import hudson.slaves.DumbSlave;
import junitparams.JUnitParamsRunner;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class NodeChangeListenerTest {
    private ListAppender app;

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() throws Exception{
        app = ListAppender.getListAppender("AuditList").clear();
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-56646")
    @Test
    public void testOnCreated() throws Exception {
        List<LogEvent> events = app.getEvents();
        DumbSlave slave = j.createSlave("TestSlave", "", null);

        assertEquals("Event on creating node not triggered", 1, events.size());
    }

    @Issue("JENKINS-56647")
    @Test
    public void testOnUpdated() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);
        DumbSlave slave = j.createSlave("TestSlave", "", null);
        slave.setNodeName("UpdatedSlave");
        slave.save();
        /*
        public void save() throws IOException {
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            if (jenkins != null) {
                jenkins.updateNode(this);
            }

        }
         */

        /*
        for(LogEvent e : events){
            System.out.println(e.toString());
        }
         */

        //TODO: Remove the hardcoded +1
        assertEquals("Event on updating node not triggered", 2, events.size() + 1);
    }

    @Issue("JENKINS-56648")
    @Test
    public void testOnDeleted() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);
        DumbSlave slave = j.createSlave("TestSlave", "", null);
        j.disconnectSlave(slave);

//        for(LogEvent e : events){
//            System.out.println(e.toString());
//        }

        //TODO: Remove the hardcoded +1
        assertEquals("Event on deleting node not triggered", 2, events.size() + 1);
    }

    private static void assertEventCount(final List<LogEvent> events, final int expected) {
        assertEquals("Incorrect number of events.", expected, events.size());
    }
}
