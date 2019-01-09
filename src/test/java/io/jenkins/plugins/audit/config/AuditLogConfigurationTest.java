package io.jenkins.plugins.audit.config;


import org.junit.*;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class AuditLogConfigurationTest {
    private ListAppender appender;
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @After
    public void teardown() {
        appender.clear();
    }

    @Test
    @Parameters({
            "TestAuditList"
    })
    public void testAuditLogDestinationName(String destinationName) throws Exception {
        AuditLogConfiguration configuration = new AuditLogConfiguration();
        configuration.setLogDestination(destinationName);

        appender = ListAppender.getListAppender(destinationName);
        assertEquals(destinationName, appender.getName());
    }
}