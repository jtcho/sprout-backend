package edu.upenn.sprout.models.data;

import java.util.ArrayList;

/**
 * An enum and source of truth for all allowed Field types for a Model attribute.
 *
 * @author jtcho
 */
public enum FieldType {

  STRING("STRING_FIELD", ""),
  INT("INT_FIELD", 0),
  LONG("LONG_FIELD", 0L),
  DOUBLE("DOUBLE_FIELD", 0D),
  BOOL("BOOL_FIELD", false),
  STRING_LIST("STRING_LIST_FIELD", new ArrayList<String>()),
  ;

  private String name;
  private Object defaultValue;

  FieldType(String name, Object defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return this.name;
  }

  public Object getDefaultValue() {
    return this.defaultValue;
  }

}
