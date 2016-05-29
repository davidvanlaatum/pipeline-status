package org.jenkinsci.plugins.pipelinestatus;

import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetStatusVariableAveStep extends AbstractStepImpl implements Serializable {
  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  private static final Logger LOG = Logger.getLogger(GetStatusVariableAveStep.class.getName());
  private String name;
  private String table;
  private Integer column;
  private Integer count;
  private List<String> keys;

  @DataBoundConstructor
  public GetStatusVariableAveStep() {
    super();
  }

  public List<String> getKeys() {
    return keys;
  }

  @DataBoundSetter
  public void setKeys(List<String> keys) {
    this.keys = keys;
  }

  public String getName() {
    return name;
  }

  @DataBoundSetter
  public void setName(@CheckForNull String name) {
    this.name = Util.fixEmptyAndTrim(name);
  }

  public String getTable() {
    return table;
  }

  @DataBoundSetter
  public void setTable(@CheckForNull String table) {
    this.table = Util.fixEmptyAndTrim(table);
  }

  public Integer getColumn() {
    return column;
  }

  @DataBoundSetter
  public void setColumn(Integer column) {
    this.column = column;
  }

  public Integer getCount() {
    return count;
  }

  @DataBoundSetter
  public void setCount(Integer count) {
    this.count = count;
  }

  @Override
  public StepDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  public static class Execution extends AbstractSynchronousNonBlockingStepExecution<Object> {

    @StepContextParameter
    private transient Run build;
    @Inject
    private GetStatusVariableAveStep step;

    @Override
    protected Object run() throws Exception {
      if (step.name != null) {
        return getAverageFor(step.name);
      } else {
        Map<String, Double> values = new TreeMap<>();
        for (String key : step.keys) {
          values.put(key, getAverageFor(key));
        }
        return values;
      }
    }

    private Double getAverageFor(String name) {
      Run b = build;
      int count = 0;
      List<Number> values = new ArrayList<>(step.count);
      while (b != null && count < step.count) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(b, false);
        if (status != null) {
          try {
            DataValue dataValue = status.get(name, step.getTable(), step.getColumn());
            if (dataValue != null && dataValue.getValue() instanceof Number) {
              values.add((Number) dataValue.getValue());
            }
          } catch (Exception e) {
            // ignore
          }
        }
        b = b.getPreviousBuild();
        count++;
      }
      return calcAverage(values);
    }

    private Double calcAverage(List<Number> values) {
      Iterator<Number> iterator = values.iterator();
      Double average = 0.0;
      while (iterator.hasNext()) {
        average = average + iterator.next().doubleValue();
      }
      if (!values.isEmpty()) {
        average = average / values.size();
      }
      return average;
    }
  }

  public static class DescriptorImpl extends AbstractStepDescriptorImpl {
    public DescriptorImpl() {
      super(Execution.class);
    }

    @Override
    public String getFunctionName() {
      return "getStatusVarAve";
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Get Status Variable Average";
    }
  }
}
