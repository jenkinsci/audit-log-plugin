package io.jenkins.plugins.audit.listeners;

import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import junitparams.JUnitParamsRunner;
import org.apache.logging.log4j.audit.AuditMessage;
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
import static org.assertj.core.api.Assertions.*;

@RunWith(JUnitParamsRunner.class)

public class ItemChangeListenerTest {
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

    @Issue("JENKINS-56640")
    @Test
    public void testAuditOnItemCreate() throws Exception {
        List<LogEvent> events = app.getEvents();

        FreeStyleProject project = j.createFreeStyleProject("Test build");
//        project.getBuildersList().add(new Shell("echo Test audit-log-plugin"));

        assertThat(events).hasSize(1);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).contains("createItem");
    }

    @Issue("JENKINS-56641")
    @Test
    public void testAuditOnItemUpdate() throws Exception {
        List<LogEvent> events = app.getEvents();

        FreeStyleProject project = j.createFreeStyleProject("Test build");
        AbstractItem item = (AbstractItem) project;
        item.setDescription("Item created for testing the updates");
        item.save();

        assertThat(events).hasSize(2);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).containsSequence("createItem", "updateItem");
    }

    @Issue("JENKINS-56642")
    @Test
    public void testAuditOnItemCopy() throws Exception {
        List<LogEvent> events = app.getEvents();

        FreeStyleProject project = j.createFreeStyleProject("Test build");
        AbstractProject copiedProject = j.jenkins.copy((AbstractProject)project , "Copied Project");

        assertThat(events).hasSize(2);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).containsSequence("createItem", "copyItem");
    }

    @Issue("JENKINS-56644")
    @Test
    public void testAuditOnItemDelete() throws Exception {
        List<LogEvent> events = app.getEvents();

        FreeStyleProject project = j.createFreeStyleProject("Test build");
        project.delete();
        // This also triggers the fireOnUpdate method.

        assertThat(events).hasSize(3);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).containsSequence("createItem", "updateItem", "deleteItem");
    }
}
