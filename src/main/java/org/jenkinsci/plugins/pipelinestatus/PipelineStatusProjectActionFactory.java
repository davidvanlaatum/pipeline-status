package org.jenkinsci.plugins.pipelinestatus;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.TransientActionFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

@Extension
public class PipelineStatusProjectActionFactory extends TransientActionFactory<Job> {
  @Override
  public Class<Job> type() {
    return Job.class;
  }

  @Nonnull
  @Override
  public Collection<? extends Action> createFor(@Nonnull Job job) {
    return Collections.singleton(new PipelineStatusProjectAction(job));
  }
}
