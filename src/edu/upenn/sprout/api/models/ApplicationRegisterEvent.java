package edu.upenn.sprout.api.models;

import play.data.validation.Constraints;

import java.util.UUID;

/**
 * @author jtcho
 * @version 2016.11.11
 */
public class ApplicationRegisterEvent {

  @Constraints.Required
  protected String applicationName;
  protected String appId;

  public ApplicationRegisterEvent() {
    /**
     * Generate an application ID for the application
     */
    setAppId(UUID.randomUUID().toString());
  }

  public void setAppId(String id) {
    this.appId = id;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public String getApplicationID() { return appId; }

}
