package org.jenkinsci.plugins.pipelinestatus;


import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Api;
import hudson.model.ModelObject;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExportedBean
public class DataTable extends Actionable implements Serializable, Action {
  private static final Logger LOG = Logger.getLogger(DataTable.class.getName());
  private String name;
  private List<Column> columns = new ArrayList<>();
  private Map<String, Row> rows = new TreeMap<>();

  public DataTable(String name) {
    this.name = name;
  }

  public Api getApi() {
    return new Api(this);
  }

  public String getName() {
    return name;
  }

  public Column addColumn(String name) {
    Column rt = new Column(name);
    columns.add(rt);
    return rt;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public Collection<Row> getRows() {
    return Collections.unmodifiableCollection(rows.values());
  }

  @Exported(inline = true)
  public Map<String, Map<String, Object>> getData() {
    Map<String, Map<String, Object>> rt = new TreeMap<>();
    for (Row row : rows.values()) {
      Map<String, Object> values = new TreeMap<>();
      for (int i = 0; i < columns.size(); i++) {
        DataValue value = row.getColumn(i);
        if (value != null) {
          values.put(getColumn(i).getName(), value.getValue());
        }
      }
      rt.put(row.getKey(), values);
    }
    return rt;
  }

  public synchronized Row getRow(String name, boolean create) {
    Row rt = rows.get(name);
    if (rt == null && create) {
      rt = new Row(name,columns.size());
      rows.put(name, rt);
    }
    return rt;
  }

  public Column getColumn(int column) {
    return columns.get(column);
  }

  @CheckForNull
  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return name;
  }

  @CheckForNull
  @Override
  public String getUrlName() {
    return name;
  }

  @Override
  public String getSearchUrl() {
    return name;
  }
}
