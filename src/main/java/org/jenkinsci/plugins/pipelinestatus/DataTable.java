package org.jenkinsci.plugins.pipelinestatus;


import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTable implements Serializable {
  private static final Logger LOG = Logger.getLogger(DataTable.class.getName());
  private String name;
  private List<Column> columns = new ArrayList<>();
  private Map<String, Row> rows = new TreeMap<>();

  public DataTable(String name) {
    this.name = name;
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
}
