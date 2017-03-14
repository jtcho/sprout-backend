package edu.upenn.sprout.controllers;

import edu.upenn.sprout.api.models.CreateUserEvent;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by igorpogorelskiy on 3/13/17.
 */
public class NewUserController extends Controller {
    private final Logger LOG = LoggerFactory.getLogger(NewUserController.class);
    private FormFactory formFactory;
    private DocumentDiffPatchService service;

    @Inject
    public NewUserController(FormFactory formFactory, DocumentDiffPatchService service) {
        this.formFactory = formFactory;
        this.service = service;
    }

    public Result createNewUser() {
        Form<CreateUserEvent> createUserEventForm = formFactory.form(CreateUserEvent.class);
        try {
            CreateUserEvent cue = createUserEventForm.bindFromRequest(request()).get();
            String userID = cue.getUserID();
            service.addUserToApp(userID, "");
            return ok(userID);
        } catch (Exception e) {
            LOG.info("Caught an exception while registering user to a document:" + e.toString());
            return badRequest(e.getMessage());
        }
    }
}
