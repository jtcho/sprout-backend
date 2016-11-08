package edu.upenn.sprout.api.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sksamuel.diffpatch.DiffMatchPatch.Operation;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.Pattern;
import play.libs.Json;

/**
 * Serialized model for a diff.
 *
 * @author jtcho
 * @version 2016.11.06
 */
public class Diff {

  @Required
  @Pattern("^-?[0-1]")
  protected Operation operation;
  @Required
  protected String text;

  public Diff() {}

  public Diff(Operation operation, String text) {
    this.operation = operation;
    this.text = text;
  }

  public void setOp(int operation) {
    switch (operation) {
      case 1:
        this.operation = Operation.INSERT;
        break;
      case -1:
        this.operation = Operation.DELETE;
        break;
      case 0:
        this.operation = Operation.EQUAL;
        break;
      default:
        throw new IllegalArgumentException("Invalid operation " + operation + " received.");
    }
  }

  public Operation getOp() {
    return operation;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  private static int encodeOperation(Operation operation) {
    switch (operation) {
      case DELETE:
        return -1;
      case INSERT:
        return 1;
      case EQUAL:
        return 0;
      default:
        throw new IllegalStateException("Invalid operation type encountered.");
    }
  }

  public JsonNode encode() {
    ObjectNode value = Json.newObject();
    value.put("op", encodeOperation(operation));
    value.put("text", text);
    return value;
  }

  @Override
  public String toString() {
    return operation + ":" + text;
  }

}
