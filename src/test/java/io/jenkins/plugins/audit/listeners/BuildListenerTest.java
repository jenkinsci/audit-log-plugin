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
import static org.junit.Assert.fail;

@RunWith(JUnitParamsRunner.class)
public class BuildListenerTest {
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

    @Issue("JENKINS-55608, JENKINS-56645")
    @Test
    public void testAuditOnBuildStartAndFinish() throws Exception{
        List<LogEvent> events = app.getEvents();

        project.getBuildersList().add(new Shell("echo Test audit-log-plugin"));
        project.scheduleBuild2(0).get();

        /*
        Order of expected events
        1) Start of the Build
        2) Finish of the Build
         */

        assertEquals("Events on build start and complete not triggered", 3, events.size());
    }
}
