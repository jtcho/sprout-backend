package edu.upenn.sprout.controllers;

import edu.upenn.sprout.api.models.ApplicationRegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * @author jtcho
 * @version 2016.11.08
 */
public class ApplicationController extends Controller {

  private final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);
  private FormFactory formFactory;

  @Inject
  public ApplicationController(FormFactory formFactory) {
    this.formFactory = formFactory;
  }

  public Result registerApplication() {
    Form<ApplicationRegisterEvent> registerEventForm = formFactory.form(ApplicationRegisterEvent.class);
    ApplicationRegisterEvent event = registerEventForm.bindFromRequest(request()).get();
    LOG.info("Registering application: " + event.getApplicationName());
    return ok();
  }

}
