package edu.upenn.sprout.models.data;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * @author jtcho
 */
public class StringField extends Field<String> {

  public StringField() {
    super(FieldType.STRING);
  }

  public StringField(String value) {
    super(FieldType.STRING, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue asAttributeValue() {
    return new AttributeValue(getValue());
  }
}
