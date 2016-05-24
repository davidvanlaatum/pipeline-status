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
import java.util.ArrayList;
import java.util.Collections;

public class AppendStatusVariableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;
  private Object value;
  private String table;
  private Integer column;

  @DataBoundConstructor
  public AppendStatusVariableStep(@NotNull String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
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

  @DataBoundSetter
  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    @StepContextParameter
    private transient Run build;
    @Inject
    private AppendStatusVariableStep step;

    @Override
    protected Void run() throws Exception {
      PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, true);
      DataValue dataValue = status.get(step.name, step.getTable(), step.getColumn());
      if (dataValue != null) {
        dataValue.append(step.value);
      } else {
        status.set(step.name, new ArrayList<>(Collections.singletonList(step.value)), DataType.LIST, step.getTable(), step.getColumn());
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
      return "appendStatusVar";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Append Status Variable";
    }
  }
}
