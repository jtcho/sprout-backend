package edu.upenn.sprout.models.data;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * @author jtcho
 */
public class IntField extends Field<Integer> {

  public IntField() {
    super(FieldType.INT);
  }

  public IntField(Integer value) {
    super(FieldType.INT, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue asAttributeValue() {
    return new AttributeValue(String.valueOf(getValue()));
  }
}
