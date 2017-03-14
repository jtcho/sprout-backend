package edu.upenn.sprout.api.models;

import play.data.validation.Constraints.Required;

/**
 * Created by igorpogorelskiy on 2/15/17.
 */
public class RegisterUserEvent {

    @Required
    private String documentID;

    @Required
    private String userID;

    public RegisterUserEvent() {}

    public RegisterUserEvent(String documentID, String userID) {
        this.documentID = documentID;
        this.userID = userID;
    }

    public void setDocumentID(String docID) {
        this.documentID = docID;
    }

    public String getDocumentID() {
        return this.documentID;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---Register User Event---\n");
        sb.append("Author: " + getUserID() + "\n");
        sb.append("Document ID: " + getDocumentID());
        return sb.toString();
    }
}
