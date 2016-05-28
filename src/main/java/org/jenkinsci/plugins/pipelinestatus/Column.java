package org.jenkinsci.plugins.pipelinestatus;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Column implements Serializable {
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
    return name;
  }
}
