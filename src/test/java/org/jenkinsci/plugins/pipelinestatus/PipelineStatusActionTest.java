package org.jenkinsci.plugins.pipelinestatus;

import hudson.model.Run;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.Serializable;
import java.util.Objects;

import static org.junit.Assert.*;

public class PipelineStatusActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void Test() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("statusvars.get(\"Remaining\");statusvars.append(\"Remaining\",123);", false));
        Run<?, ?> run = Objects.requireNonNull(job.scheduleBuild2(0)).get();
        j.assertBuildStatusSuccess(run);
    }
}
