package edu.upenn.sprout.models;

import com.fasterxml.uuid.Generators;
import edu.upenn.sprout.models.data.StringField;

import java.util.UUID;

/**
 * @author jtcho
 * @version 2016.09.17
 */
public class Account extends Model {

  private final UUID uuid;

  public Account(String email) {
    uuid = Generators.timeBasedGenerator().generate();
    this.setValue("email", new StringField(email));
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getEmail() {
    return ((StringField) this.getValue("email")).getValue();
  }

  public Account setName(String name) {
    this.setValue("name", new StringField(name));
    return this;
  }

  public String getName() {
    return ((StringField) this.getValue("name")).getValue();
  }
}
