package org.jenkinsci.plugins.pipelinestatus;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class SetStatusVariableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;
  private Object value;
  private DataType type = DataType.OBJECT;

  @DataBoundConstructor
  public SetStatusVariableStep(@NotNull String name) {
    super();
    this.name = name;
  }

  public DataType getType() {
    return type;
  }

  @DataBoundSetter
  public void setType(DataType type) {
    this.type = type;
  }

  @DataBoundSetter
  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public Object getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    @StepContextParameter
    private transient Run build;
    @StepContextParameter
    private transient TaskListener taskListener;
    @Inject
    private SetStatusVariableStep step;

    @Override
    protected Void run() throws Exception {
      PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, true);
      status.set(step.getName(), step.getValue(), step.getType());
      return null;
    }
  }

  public static class DescriptorImpl extends AbstractStepDescriptorImpl {
    public DescriptorImpl() {
      super(Execution.class);
    }

    @Override
    public String getFunctionName() {
      return "setStatusVar";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Set Status Variable";
    }

    public ListBoxModel doFillTypeItems() {
      ListBoxModel items = new ListBoxModel();
      for (DataType dataType : DataType.values()) {
        items.add(dataType.name());
      }
      return items;
    }
  }
}
