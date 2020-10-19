package io.jenkins.plugins.audit.listeners;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import hudson.model.Slave;
import hudson.slaves.DumbSlave;
import org.apache.logging.log4j.audit.AuditMessage;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeChangeListenerTest {
    private ListAppender app;

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() {
        app = ListAppender.getListAppender("AuditList").clear();
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-56646")
    @Test
    public void testOnCreated() throws Exception {
        j.createSlave();

        List<LogEvent> events = app.getEvents();

        assertThat(events).hasSize(1);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).contains("createNode");
    }

    @Issue("JENKINS-56647")
    @Test
    public void testOnUpdated() throws Exception {
        Slave agent = j.createOnlineSlave();
        HtmlForm form = j.createWebClient().getPage(agent, "configure").getFormByName("config");
        HtmlInput element = form.getInputByName("_.name");
        element.setValueAttribute("newSlaveName");
        j.submit(form);

        List<LogEvent> events = app.getEvents();

        assertThat(events).hasSize(2);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).contains("createNode", "updateNode");
    }

    @Issue("JENKINS-56648")
    @Test
    public void testOnDeleted() throws Exception {
        DumbSlave agent = j.createOnlineSlave();
        j.jenkins.removeNode(agent);

        List<LogEvent> events = app.getEvents();

        assertThat(events).hasSize(2);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).contains("createNode", "deleteNode");

    }
}
