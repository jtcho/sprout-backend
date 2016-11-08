package edu.upenn.sprout.api.models;

import com.sksamuel.diffpatch.DiffMatchPatch.Operation;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.Pattern;

/**
 * Serialized model for a diff.
 *
 * @author jtcho
 * @version 2016.11.06
 */
public class Diff {

  @Required
  @Pattern("[+-=]")
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

  @Override
  public String toString() {
    return operation + ":" + text;
  }

}
