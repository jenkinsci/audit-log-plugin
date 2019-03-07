package io.jenkins.plugins.audit.listeners;

import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
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
public class BuildStartListenerTest {
    private ListAppender app;
    private FreeStyleProject project;

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() throws Exception{
        app = ListAppender.getListAppender("AuditList").clear();
        project = j.createFreeStyleProject("Test build");
    }

    @After
    public void teardown() {
        app.clear();
    }

    @Issue("JENKINS-55608")
    @Test
    public void testAuditOnBuildStart() throws Exception{
        List<LogEvent> events = app.getEvents();

        project.getBuildersList().add(new Shell("echo Test audit-log-plugin"));
        project.scheduleBuild2(0).get();

        assertEquals("Event on build start not triggered", 1, events.size());
    }
}
