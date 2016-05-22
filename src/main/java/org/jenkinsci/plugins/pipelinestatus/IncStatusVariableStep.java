package org.jenkinsci.plugins.pipelinestatus;

import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class IncStatusVariableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;
  private Integer amount = 1;

  @DataBoundConstructor
  public IncStatusVariableStep(@NotNull String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Integer getAmount() {
    return amount;
  }

  @DataBoundSetter
  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    @StepContextParameter
    private transient Run build;
    @Inject
    private IncStatusVariableStep step;

    @Override
    protected Void run() throws Exception {
      PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, true);
      DataValue dataValue = status.get(step.name);
      if (dataValue != null) {
        dataValue.incValue(step.amount);
      } else {
        status.set(step.name, step.amount, DataType.OBJECT);
      }
      return null;
    }

  }

  public static class DescriptorImpl extends AbstractStepDescriptorImpl {
    public DescriptorImpl() {
      super(Execution.class);
    }

    @Override
    public String getFunctionName() {
      return "incStatusVar";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Increment Status Variable";
    }
  }
}