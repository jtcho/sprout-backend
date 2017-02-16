package edu.upenn.sprout.controllers;

import edu.upenn.sprout.api.models.DocumentEvent;
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
public class DocumentCreationController extends Controller {
    private final Logger LOG = LoggerFactory.getLogger(DocumentCreationController.class);

    private FormFactory formFactory;
    private DocumentDiffPatchService service;

    @Inject
    public DocumentCreationController(FormFactory formFactory, DocumentDiffPatchService service) {
        this.formFactory = formFactory;
        this.service = service;
    }

    public Result createDocument() {
        Form<DocumentEvent> docEventForm = formFactory.form(DocumentEvent.class);
        try {
            DocumentEvent dEvent = docEventForm.bindFromRequest(request()).get();
            service.createNewDocumentFor(dEvent.getUserID());
            return ok(toJson(dEvent.getUserID()));
        } catch (Exception e) {
            LOG.info("Caught an exception while creating document: " + e.toString());
            return badRequest(e.getMessage());
        }
    }
}
