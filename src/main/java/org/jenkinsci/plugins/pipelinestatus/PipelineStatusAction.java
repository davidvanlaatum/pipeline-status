package org.jenkinsci.plugins.pipelinestatus;

import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Api;
import hudson.model.Run;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExportedBean
public class PipelineStatusAction extends Actionable implements Action {
  private static final Logger LOG = Logger.getLogger(PipelineStatusAction.class.getName());

  private final Run build;
  private Map<String, DataValue> data = new TreeMap<>();

  public PipelineStatusAction(Run build) {
    this.build = build;
  }

  public Run getBuild() {
    return build;
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "Workflow Status";
  }

  @Override
  public String getUrlName() {
    return "pipeline-status";
  }

  @Exported(inline = true)
  public String getUrl() {
    return build.getUrl() + "/" + getUrlName();
  }

  @Override
  public String getSearchUrl() {
    return null;
  }

  public void set(String name, Object value, DataType type) {
    LOG.log(Level.INFO, "Setting {0} to {1} type {2} class {3}",
        new Object[]{name, value, type, value == null ? null : value.getClass()});
    if (value == null) {
      data.remove(name);
    } else {
      data.put(name, new DataValue().setType(type).setValue(value).covertAsNeeded());
    }
  }

  @Exported
  public Map<String, DataValue> getData() {
    return data;
  }

  @DataBoundSetter
  public void setData(Map<String, DataValue> data) {
    this.data = data;
  }

  public boolean hasData() {
    return !data.isEmpty();
  }

  public DataValue get(String name) {
    return data.get(name);
  }
}
