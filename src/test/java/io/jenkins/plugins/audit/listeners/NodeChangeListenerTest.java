package io.jenkins.plugins.audit.listeners;

import hudson.model.Node;
import hudson.slaves.DumbSlave;
import jenkins.model.NodeListener;
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

        // Creating the node
        DumbSlave slave = j.createSlave("TestSlave", "", null);

        Node node = j.jenkins.getNode("TestSlave");
        node.setNodeName("Updated the node");
        node.save();
        j.jenkins.updateNode(node);

        NodeListener.fireOnUpdated(slave, node);

        assertEquals("Event on updating node not triggered", 2, events.size());
    }

    @Issue("JENKINS-56648")
    @Test
    public void testOnDeleted() throws Exception {
        List<LogEvent> events = app.getEvents();
        assertEventCount(events, 0);
        DumbSlave slave = j.createOnlineSlave();
        j.jenkins.removeNode(slave);

        assertEquals("Event on deleting node not triggered", 2, events.size());
    }

    private static void assertEventCount(final List<LogEvent> events, final int expected) {
        assertEquals("Incorrect number of events.", expected, events.size());
    }
}
