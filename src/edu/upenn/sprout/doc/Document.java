package edu.upenn.sprout.doc;

/**
 * @author jtcho
 * @version 2016.11.06
 */
public class Document {

  private final String id;
  private final long revisionNumber;
  private String title;
  private String content;

  public Document(String id, long revisionNumber, String title, String content) {
    this.id = id;
    this.revisionNumber = revisionNumber;
    this.title = title;
    this.content = content;
  }

  public String getId() {
    return id;
  }

  public long getRevisionNumber() {
    return revisionNumber;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

}
