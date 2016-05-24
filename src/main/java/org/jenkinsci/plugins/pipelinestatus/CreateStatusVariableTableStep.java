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
import java.util.List;
import java.util.Map;

public class CreateStatusVariableTableStep extends AbstractStepImpl implements Serializable {

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private final String name;
  private List<Map<String, String>> columns;

  @DataBoundConstructor
  public CreateStatusVariableTableStep(@NotNull String name) {
    super();
    this.name = name;
  }

  public List<Map<String, String>> getColumns() {
    return columns;
  }

  @DataBoundSetter
  public void setColumns(List<Map<String, String>> columns) {
    this.columns = columns;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
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
    private CreateStatusVariableTableStep step;

    @Override
    protected Void run() throws Exception {
      PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, true);
      status.createTable(step.name, step.columns);
//      status.set(step.getName(), step.getValue(), step.getType());
      return null;
    }
  }

  public static class DescriptorImpl extends AbstractStepDescriptorImpl {
    public DescriptorImpl() {
      super(Execution.class);
    }

    @Override
    public String getFunctionName() {
      return "createStatusVarTable";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Create Status Variable Table";
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
