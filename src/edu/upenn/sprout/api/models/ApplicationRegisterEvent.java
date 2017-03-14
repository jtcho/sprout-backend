package edu.upenn.sprout.api.models;

import play.data.validation.Constraints;

/**
 * @author jtcho
 * @version 2016.11.11
 */
public class ApplicationRegisterEvent {

  @Constraints.Required
  protected String applicationName;

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getApplicationName() {
    return applicationName;
  }

}
