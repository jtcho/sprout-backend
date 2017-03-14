package edu.upenn.sprout.api.models;

import com.sksamuel.diffpatch.DiffMatchPatch.Diff;
import edu.upenn.sprout.utils.DiffUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jtcho
 * @version 2016.11.07
 */
public class InternalEditEvent {

  protected String author;
  protected String applicationId;
  protected String documentId;
  protected List<Diff> diffs = new LinkedList<>();

  public InternalEditEvent(String author, String applicationId, String documentId, List<Diff> diffs) {
    this.author = author;
    this.applicationId = applicationId;
    this.documentId = documentId;
    this.diffs = diffs;
  }

  public InternalEditEvent(EditEvent editEvent) {
    author = editEvent.getAuthor();
    applicationId = editEvent.getApplicationId();
    documentId = editEvent.getDocumentId();
    diffs = DiffUtils.convertDiffsToInternal(editEvent.getDiffs());
  }

  public String getAuthor() {
    return author;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public String getDocumentId() {
    return documentId;
  }

  public List<Diff> getDiffs() {
    return diffs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(author + ", " + applicationId + ", " + " [");
    sb.append(diffs.stream().map(Diff::toString).collect(Collectors.joining(", ")));
    sb.append("] ");
    return sb.toString();
  }
}
