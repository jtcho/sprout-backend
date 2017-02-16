package edu.upenn.sprout.controllers;

import edu.upenn.sprout.api.models.RegisterUserEvent;
import edu.upenn.sprout.doc.Document;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import static play.libs.Json.toJson;

/**
 * Created by igorpogorelskiy on 2/15/17.
 */
public class RegisterUserController extends Controller {
    private final Logger LOG = LoggerFactory.getLogger(RegisterUserController.class);
    private FormFactory formFactory;
    private DocumentDiffPatchService service;

    @Inject
    public RegisterUserController(FormFactory formFactory, DocumentDiffPatchService service) {
        this.formFactory = formFactory;
        this.service = service;
    }

    public Result registerNewUser() {
        Form<RegisterUserEvent> registerUserEventForm = formFactory.form(RegisterUserEvent.class);
        try {
            RegisterUserEvent rue = registerUserEventForm.bindFromRequest(request()).get();
            service.registerNewUser(rue.getUserID(), rue.getDocumentID());
            Document masterDoc = service.masterCopyForDocumentWithID(rue.getUserID());
            return ok(toJson(masterDoc));
        } catch (Exception e) {
            LOG.info("Caught an exception while registering user to a document:" + e.toString());
            return badRequest(e.getMessage());
        }
    }
}
