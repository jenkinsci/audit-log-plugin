package io.jenkins.plugins.audit.config;


import org.junit.*;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class AuditLogConfigurationTest {
    private ListAppender appender;
    private AuditLogConfiguration configuration;

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() {
        configuration = new AuditLogConfiguration();
    }

    @After
    public void teardown() {
        configuration.setLogDestination(null);
        appender.clear();
    }

    @Issue("JENKINS-54089")
    @Test
    public void testAuditLogDestinationName() {
        String destinationName = "TestAuditList";
        configuration.setLogDestination(destinationName);

        appender = ListAppender.getListAppender(destinationName);
        assertEquals(destinationName, appender.getName());
    }
}