package org.jenkinsci.plugins.pipelinestatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Row implements Serializable {
  private String key;
  private List<DataValue> data;

  Row(String key, int size) {
    this.key = key;
    data = new ArrayList<>(size);
    while(data.size() < size) {
      data.add(null);
    }
  }

  public String getKey() {
    return key;
  }

  public List<DataValue> getData() {
    return Collections.unmodifiableList(data);
  }

  public void setColumn(int index, DataValue value) {
    data.set(index, value);
  }

  public DataValue getColumn(Integer column) {
    return data.get(column);
  }
}
