package edu.upenn.sprout.services;

import edu.upenn.sprout.api.models.Diff;
import edu.upenn.sprout.api.models.EditEvent;
import edu.upenn.sprout.api.models.InternalEditEvent;
import edu.upenn.sprout.doc.Document;
import org.junit.Before;
import org.junit.Test;
import play.inject.ApplicationLifecycle;

import java.util.Collections;
import java.util.List;

import static com.sksamuel.diffpatch.DiffMatchPatch.Operation.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author jtcho
 * @version 2017.02.01
 */
public class DocumentDiffPatchServiceShould {

  private DocumentDiffPatchService service;

  @Before
  public void setup() {
    ApplicationLifecycle lifecycleMock = mock(ApplicationLifecycle.class);
    service = new DocumentDiffPatchService(lifecycleMock);
  }

  @Test
  public void createNewDocument() {
    String documentId = service.createNewDocumentFor("jtcho");
    assertTrue(service.masterCopies.containsKey(documentId));
    assertTrue(service.shadowStores.containsKey(documentId));
    assertTrue(service.shadowStores.get(documentId).isRegistered("jtcho"));
  }

  @Test
  public void handleSingleEdit() {
    String documentId = service.createNewDocumentFor("jtcho");
    EditEvent editEvent = new EditEvent("jtcho", "", documentId);
    editEvent.setDiffs(Collections.singletonList(new Diff(INSERT, "foo")));
    service.applyShadowEdit(documentId, new InternalEditEvent(editEvent));
    assertEquals(1, service.masterEditQueue.size());
//    service.applyMasterEdit(documentId, new InternalEditEvent(editEvent));
//    Document masterCopy = service.masterCopies.get(documentId);
//    assertEquals("foo", masterCopy.getContent());
  }

}
