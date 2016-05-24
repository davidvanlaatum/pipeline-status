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

public class DecStatusVariableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;
  private Integer amount = 1;
  private String table;
  private Integer column;

  @DataBoundConstructor
  public DecStatusVariableStep(@NotNull String name) {
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

  public String getTable() {
    return table;
  }

  @DataBoundSetter
  public void setTable(String table) {
    this.table = table;
  }

  public Integer getColumn() {
    return column;
  }

  @DataBoundSetter
  public void setColumn(Integer column) {
    this.column = column;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    @StepContextParameter
    private transient Run build;
    @Inject
    private DecStatusVariableStep step;

    @Override
    protected Void run() throws Exception {
      PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, true);
      DataValue dataValue = status.get(step.name, step.getTable(), step.getColumn());
      if (dataValue != null) {
        dataValue.decValue(step.amount);
      } else {
        status.set(step.name, step.amount * -1, DataType.OBJECT, step.getTable(), step.getColumn());
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
      return "decStatusVar";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Decrement Status Variable";
    }
  }
}
