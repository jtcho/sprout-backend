package edu.upenn.sprout.models.data;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * An abstract type for a Model attribute.
 *
 * @param <T> the type of the attribute value
 */
public abstract class Field<T> {

  private String name;
  private T value;

  @SuppressWarnings("unchecked")
  protected Field(FieldType fieldType) {
    this(fieldType, (T) fieldType.getDefaultValue());
  }

  protected Field(FieldType fieldType, T value) {
    this.name = fieldType.getName();
    this.value = value;
  }

  /**
   * Gets the name of the field.
   *
   * @return the name of the field
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the value of the field.
   *
   * @param value the new value
   */
  public void setValue(T value) {
    this.value = value;
  }

  /**
   * Gets the value of the field.
   *
   * @return the value of the field
   */
  public T getValue() {
    return this.value;
  }

  /**
   * Converts this Field type to an AttributeValue that can be stored
   * easily in an AWS DynamoDB database table.
   *
   * @return the corresponding attribute value type
   */
  public abstract AttributeValue asAttributeValue();

}
