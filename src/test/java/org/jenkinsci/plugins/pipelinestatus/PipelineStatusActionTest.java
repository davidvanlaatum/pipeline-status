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
        job.setDefinition(new CpsFlowDefinition("statusvars.set(\"Running\", 0)\n" +
                "statusvars.createTable \"Suites\", [\n" +
                "        [name: 'Time', type: \"INTERVAL\"],\n" +
                "        [name: 'Ave', type: \"INTERVAL\"],\n" +
                "        [name: 'Status', type: \"OBJECT\"],\n" +
                "        [name: 'Node', type: \"OBJECT\"],\n" +
                "        [name: 'Order', type: \"NUMBER\"]]\n" +
                "\n" +
                "statusvars.getTable(\"Suites\").time(\"abc\", 0, {\n" +
                "    sleep(1)\n" +
                "})\n" +
                "statusvars.getTable(\"Suites\").time(\"xyz\", 0, {\n" +
                "    sleep(1)\n" +
                "})\n" +
                "\n" +
                "println statusvars.getTable(\"Suites\").get(\"abc\",0)\n" +
                "println statusvars.getTable(\"Suites\").get(\"xyz\",0)\n" +
                "    \n" +
                "if(!statusvars.getTable(\"Suites\").get(\"abc\",0).toString().startsWith(\"1s\")) {\n" +
                "    println statusvars.getTable(\"Suites\").get(\"abc\",0)\n" +
                "    throw new Exception(\"Bad abc value\")\n" +
                "}\n" +
                "\n" +
                "def table = statusvars.getTable(\"Suites\")\n" +
                "Map<String, Double> order = table.calcAverages(0, 1)\n" +
                "println order\n" +
                "println statusvars.getTable(\"Suites\").get(\"abc\",1)\n" +
                "println statusvars.getTable(\"Suites\").get(\"xyz\",1)\n" +
                "\n" +
                "if (!(order.get(\"abc\") > 0)) {\n" +
                "    throw new Exception(\"bad abc value\")\n" +
                "}", false));
        Run<?, ?> run = Objects.requireNonNull(job.scheduleBuild2(0)).get();
        j.assertBuildStatusSuccess(run);
    }
}
