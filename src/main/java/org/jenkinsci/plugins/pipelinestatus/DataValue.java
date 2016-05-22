package org.jenkinsci.plugins.pipelinestatus;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.List;

@ExportedBean
public class DataValue implements Serializable {
  private DataType type;
  private Object value;

  @Exported
  public DataType getType() {
    return type;
  }

  public DataValue setType(DataType type) {
    this.type = type;
    return this;
  }

  @Exported
  public Object getValue() {
    return value;
  }

  public DataValue setValue(Object value) {
    this.value = value;
    return this;
  }

  @Override
  public String toString() {
    String rt = null;

    switch (type) {
      case OBJECT:
        rt = value != null ? value.toString() : null;
        break;
      case INTERVAL:
        if (value instanceof Long) {
          rt = new Period(((Long) value).longValue()).toString(new PeriodFormatterBuilder()
              .printZeroRarelyLast()
              .appendHours()
              .appendSuffix("h")
              .appendMinutes()
              .appendSuffix("m")
              .appendSeconds()
              .appendSuffix("s")
              .appendMillis()
              .appendSuffix("ms")
              .toFormatter());
        }
      case LIST:
        rt = value.toString();
        break;
    }

    return rt == null ? "{null}" : rt;
  }

  public DataValue covertAsNeeded() {
    switch (type) {
      case INTERVAL:
        if (value instanceof Integer) {
          value = new Long((Integer) value);
        }
        break;
    }
    return this;
  }

  public Object getSortValue() {
    Object rt = null;
    switch (type) {
      case INTERVAL:
        rt = value;
        break;
    }
    return rt;
  }

  public void incValue(Integer amount) {
    if (value instanceof Integer) {
      value = (Integer) value + amount;
    } else if (value instanceof Long) {
      value = (Long) value + amount;
    } else {
      throw new IllegalStateException("Can't increment value of type " + (value != null ? value.getClass() : null));
    }
  }

  public void decValue(Integer amount) {
    if (value instanceof Integer) {
      value = (Integer) value - amount;
    } else if (value instanceof Long) {
      value = (Long) value - amount;
    } else {
      throw new IllegalStateException("Can't decrement value of type " + (value != null ? value.getClass() : null));
    }
  }

  @SuppressWarnings("unchecked")
  public void append(Object value) {
    if (this.value instanceof List) {
      ((List) this.value).add(value);
    } else {
      throw new IllegalStateException("Can't append to value of type " + (this.value != null ? this.value.getClass() : null));
    }
  }
}
