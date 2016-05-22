package org.jenkinsci.plugins.pipelinestatus;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class GetStatusVariableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;

  @DataBoundConstructor
  public GetStatusVariableStep(@NotNull String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Object> {

    @StepContextParameter
    private transient Run build;
    @Inject
    private GetStatusVariableStep step;

    @Override
    protected Object run() throws Exception {
      PipelineStatusAction status = build.getAction(PipelineStatusAction.class);
      if (status == null) {
        status = new PipelineStatusAction(build);
        build.addAction(status);
      }
      DataValue dataValue = status.get(step.name);
      return dataValue == null ? null : dataValue.getValue();
    }
  }

  public static class DescriptorImpl extends AbstractStepDescriptorImpl {
    public DescriptorImpl() {
      super(Execution.class);
    }

    @Override
    public String getFunctionName() {
      return "getStatusVar";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Get Status Variable";
    }
  }
}
