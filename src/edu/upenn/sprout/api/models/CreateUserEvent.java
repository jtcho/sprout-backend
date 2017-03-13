package edu.upenn.sprout.api.models;

import play.data.validation.Constraints.Required;

/**
 * Created by igorpogorelskiy on 3/13/17.
 */
public class CreateUserEvent {

    @Required
    private String userID;

    public CreateUserEvent() {}

    public CreateUserEvent(String userID) {
        setUserID(userID);
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return this.userID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---Create User Event---\n");
        sb.append("User ID: " + getUserID() + "\n");
        return sb.toString();
    }
}
