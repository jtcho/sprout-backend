package edu.upenn.sprout.models.data;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * @author jtcho
 * @version 16.09.17
 */
public class LongField extends Field<Long> {

  public LongField() {
    super(FieldType.LONG);
  }

  public LongField(Long value) {
    super(FieldType.LONG, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue asAttributeValue() {
    return new AttributeValue(String.valueOf(getValue()));
  }
}
