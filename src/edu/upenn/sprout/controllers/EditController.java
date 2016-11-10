package edu.upenn.sprout.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.upenn.sprout.api.models.Diff;
import edu.upenn.sprout.api.models.EditEvent;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

/**
 * @author jtcho
 * @version 2016.11.06
 */
public class EditController extends Controller {

  private final Logger LOG = LoggerFactory.getLogger(EditController.class);
  private FormFactory formFactory;
  private DocumentDiffPatchService service;

  @Inject
  public EditController(FormFactory formFactory, DocumentDiffPatchService service) {
    this.formFactory = formFactory;
    this.service = service;
  }

  public Result getEdits(String docId, String authorId) {
    if (service.isValid(docId)) {
      LOG.info("Fetching queued edits for document [" + docId + "] for user " + authorId + ".");
      try {
        List<Diff> diffs = service.getQueuedDiffs(docId, authorId);
        ArrayNode encodedDiffs = Json.newArray();
        diffs.stream().map(Diff::encode).forEach(encodedDiffs::add);
        return ok(encodedDiffs);
      } catch (IllegalArgumentException e) {
        return badRequest(e.getMessage());
      }
    } else {
      return notFound("Document with id " + docId + " not found.");
    }
  }

  public Result postEdit() {
    Form<EditEvent> editEventForm = formFactory.form(EditEvent.class);
    try {
      EditEvent event = editEventForm.bindFromRequest(request()).get();
      service.enqueueShadowEdit(event);
      return ok();
    } catch (IllegalStateException e) {
      return badRequest();
    }
  }

}
