package org.jenkinsci.plugins.pipelinestatus;

import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.Run;

import java.util.Map;

public class PipelineStatusProjectAction extends Actionable implements Action {
  private final Run<?, ?> run;
  private PipelineStatusAction action = null;

  public PipelineStatusProjectAction(Job<?, ?> job) {
    this.run = job.getLastBuild();
    if (this.run != null) {
      this.action = this.run.getAction(PipelineStatusAction.class);
    }
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return "pipeline-status";
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getSearchUrl() {
    return null;
  }

  public boolean hasData() {
    return action != null && !action.getData().isEmpty();
  }

  public Map<String, DataValue> getData() {
    Map<String, DataValue> rt = null;
    if (action != null) {
      rt = action.getData();
    }
    return rt;
  }

  public PipelineStatusAction getBuildAction() {
    return action;
  }
}
