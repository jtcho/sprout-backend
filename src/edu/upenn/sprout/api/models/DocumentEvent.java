package edu.upenn.sprout.api.models;

import play.data.validation.Constraints.Required;

/**
 * Created by igorpogorelskiy on 2/15/17.
 */
public class DocumentEvent {

    @Required
    String userID;

    String documentTitle;

    public DocumentEvent() {}

    public DocumentEvent(String userID) {
        this.userID = userID;
        setTitle("Untitled");
    }

    public String getUserID() {
        return this.userID;
    }

    public String getTitle() {
        return this.documentTitle;
    }

    public void setTitle(String title) {
        this.documentTitle = title;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Author: ");
        sb.append(getUserID());
        sb.append("\n");
        sb.append("Document Title: " + getTitle());
        return sb.toString();
    }

}
