package edu.upenn.sprout.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import edu.upenn.sprout.models.data.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An abstract data model type. All data models used by this application
 * should extend this class.
 *
 * The data stored in the model may be easily retrieved for database updates.
 *
 * @author jtcho
 */
public abstract class Model {

  private Map<String, Field> dataMap;

  /**
   * Initializes the Model type from an existing data map.
   *
   * @param dataMap the data map
   */
  protected Model(Map<String, Field> dataMap) {
    this.dataMap = new HashMap<>(dataMap);
  }

  public Model() {
    this.dataMap = new HashMap<>();
  }

  /**
   * Gets the Model's data map.
   *
   * @return the data map
   */
  public Map<String, Field> getDataMap() {
    return this.dataMap;
  }

  /**
   * Gets the Model's data map as an attribute value map.
   *
   * @return the attribute value map
   */
  public Map<String, AttributeValue> getDataAsAttributes() {
    return dataMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().asAttributeValue()));
  }

  protected void setValue(String key, Field value) {
    this.dataMap.put(key, value);
  }

  protected Field getValue(String key) {
    return this.dataMap.get(key);
  }

}
