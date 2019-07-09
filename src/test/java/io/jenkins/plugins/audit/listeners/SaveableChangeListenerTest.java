package io.jenkins.plugins.audit.listeners;

import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import junitparams.JUnitParamsRunner;
import org.apache.logging.log4j.audit.AuditMessage;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import com.cloudbees.plugins.credentials.CredentialsProvider;

import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class SaveableChangeListenerTest {
    private ListAppender app;
    private CredentialsStore store = null;

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

    @Issue("ISSUE-35")
    @Test
    public void testOnCredentialsUsage() throws Exception {
        List<LogEvent> events = app.getEvents();

        UsernamePasswordCredentialsImpl credentials = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "secret-id", "test credentials", "bob","secret");
        CredentialsProvider.lookupStores(j.jenkins).iterator().next().addCredentials(Domain.global(), credentials);
        JenkinsRule.WebClient wc = j.createWebClient();
        FreeStyleProject job = j.createFreeStyleProject();
        job.addProperty(new ParametersDefinitionProperty(
                new CredentialsParameterDefinition(
                    "SECRET",
                    "The secret",
                    "secret-id",
                    Credentials.class.getName(),
                    false
                )));
        job.getBuildersList().add(new CaptureEnvironmentBuilder());
        job.scheduleBuild2(0, new ParametersAction(new CredentialsParameterValue("SECRET", "secret-id", "The secret", true))).get();

        assertThat(events).hasSize(4);
        assertThat(events).extracting(event -> ((AuditMessage) event.getMessage()).getId().toString()).containsSequence("createItem", "buildStart", "useCredentials", "buildFinish");
    }
}
