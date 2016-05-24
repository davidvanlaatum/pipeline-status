package org.jenkinsci.plugins.pipelinestatus;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Column implements Serializable {
  static Logger LOG = Logger.getLogger(Column.class.getName());
  private String name;
  private DataType defaultType = DataType.OBJECT;

  Column(String name) {
    this.name = name;
  }

  public DataType getDefaultType() {
    return defaultType;
  }

  public Column setDefaultType(DataType defaultType) {
    this.defaultType = defaultType;
    return this;
  }

  public String getName() {
    LOG.log(Level.INFO, "returning name {0}", name);
    return name;
  }
}
