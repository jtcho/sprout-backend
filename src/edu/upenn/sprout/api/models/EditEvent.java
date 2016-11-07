package edu.upenn.sprout.api.models;

import play.data.validation.Constraints.Required;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serialized model for an edit event.
 *
 * @author jtcho
 * @version 2016.11.06
 */
public class EditEvent {

  @Required
  protected String author;
  @Required
  protected String applicationId;
  @Required
  protected String documentId;
  @Required
  protected List<Diff> diffs;

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getAuthor() {
    return author;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public String getDocumentId() {
    return documentId;
  }

  public void setDiffs(List<Diff> diffs) {
    this.diffs = new LinkedList<>(diffs);
  }

  public List<Diff> getDiffs() {
    return diffs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(author + ", " + applicationId + ", " + " [");
    sb.append(diffs.stream().map(Diff::toString).collect(Collectors.joining(", ")));
    sb.append("]");
    return sb.toString();
  }

}
