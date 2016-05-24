package org.jenkinsci.plugins.pipelinestatus;

import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Run;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExportedBean
public class PipelineStatusAction extends Actionable implements Action {
  private static final Logger LOG = Logger.getLogger(PipelineStatusAction.class.getName());

  private final Run build;
  private Map<String, DataValue> data = new TreeMap<>();
  private Map<String, DataTable> tables = new TreeMap<>();

  public PipelineStatusAction(Run build) {
    this.build = build;
  }

  public static PipelineStatusAction getPipelineStatusAction(Run<?,?> build, boolean create) {
    PipelineStatusAction status = null;
    if (build != null) {
      status = build.getAction(PipelineStatusAction.class);
      if (status == null && create) {
        status = new PipelineStatusAction(build);
        build.addAction(status);
      }
    }
    return status;
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

  public void set(String name, Object value, DataType type, String tableName, Integer column) {
    LOG.log(Level.INFO, "Setting {0} to {1} type {2} class {3}",
        new Object[]{name, value, type, value == null ? null : value.getClass()});
    if (tableName == null) {
      if (value == null) {
        data.remove(name);
      } else {
        data.put(name, new DataValue().setType(type).setValue(value).covertAsNeeded());
      }
    } else {
      DataTable table = getTable(tableName);
      if (table == null) {
        throw new IllegalArgumentException("table " + tableName + " does not exit");
      }
      Row row = table.getRow(name, true);
      if (type == null) {
        type = table.getColumn(column).getDefaultType();
      }
      if (column != null) {
        if (value == null) {
          row.setColumn(column, null);
        } else {
          row.setColumn(column, new DataValue().setValue(value).setType(type).covertAsNeeded());
        }
      } else if (value instanceof Collection) {
        int c = 0;
        for (Object o : ((Collection) value)) {
          if (o == null) {
            row.setColumn(c, null);
          } else {
            row.setColumn(c, new DataValue().setValue(o).setType(table.getColumn(c).getDefaultType()).covertAsNeeded());
          }
          c++;
        }
      } else {
        throw new IllegalArgumentException("Must provide a column or value must be a collection");
      }
    }
  }

  @Exported
  public Map<String, DataValue> getData() {
    return data;
  }

  public Collection<DataTable> getTables() {
    return Collections.unmodifiableCollection(tables.values());
  }

  @DataBoundSetter
  public void setData(Map<String, DataValue> data) {
    this.data = data;
  }

  public boolean hasData() {
    return !data.isEmpty();
  }

  public DataValue get(String name, String table, Integer column) {
    if (table == null) {
      return data.get(name);
    } else {
      DataTable dataTable = getTable(table);
      if (dataTable == null) {
        throw new IllegalArgumentException("Table " + table + " does not exit");
      }
      Row row = dataTable.getRow(name, true);
      DataValue rt = row.getColumn(column);
      if (rt == null) {
        rt = new DataValue();
        row.setColumn(column, rt);
      }
      return rt;
    }
  }

  public synchronized void createTable(String name, List<Map<String, String>> columns) {
    if (tables.get(name) != null) {
      throw new IllegalArgumentException("Table " + name + " already exists");
    }

    DataTable table = new DataTable(name);
    tables.put(name, table);

    for (Map<String, String> column : columns) {
      if (!column.containsKey("name") || column.get("name") == null || column.get("name").isEmpty()) {
        throw new IllegalArgumentException("Columns must have a name");
      }
      Column addColumn = table.addColumn(column.get("name"));
      if (column.containsKey("type")) {
        addColumn.setDefaultType(DataType.valueOf(column.get("type")));
      }
    }
  }

  public DataTable getTable(String name) {
    return tables.get(name);
  }
}
