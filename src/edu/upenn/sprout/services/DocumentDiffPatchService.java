package edu.upenn.sprout.services;

import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;
import com.sksamuel.diffpatch.DiffMatchPatch.Patch;
import edu.upenn.sprout.api.models.EditEvent;
import edu.upenn.sprout.doc.Document;
import edu.upenn.sprout.doc.DocumentStore;
import edu.upenn.sprout.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author jtcho
 * @version 2016.11.06
 */
@Singleton
public class DocumentDiffPatchService {

  /**
   * Maps document IDs to the master copy of the document.
   */
  private Map<String, Document> masterCopies;
  /**
   * Maps document IDs to document stores containing shadow copies.
   */
  private Map<String, DocumentStore> shadowStores;
  /**
   * Queue of edit events to process.
   */
  private ConcurrentLinkedQueue<EditEvent> editQueue;

  private static DiffMatchPatch dmp = new DiffMatchPatch();

  private static Logger LOG = LoggerFactory.getLogger(DocumentDiffPatchService.class);

  @Inject
  protected DocumentDiffPatchService(ApplicationLifecycle lifecycle) {
    masterCopies = new HashMap<>();
    shadowStores = new HashMap<>();
    editQueue = new ConcurrentLinkedQueue<>();

    ShadowCopyHandler shadowDaemon = new ShadowCopyHandler(this, editQueue);
    Thread shadowThread = new Thread(shadowDaemon);
    shadowThread.start();
    lifecycle.addStopHook(() -> CompletableFuture.runAsync(shadowDaemon::shutdown));

    // Please do delete these... just for sample testing.
    DocumentStore exampleStore = new DocumentStore();
    Document masterCopy = new Document("pineapple", 0, "TITLE", "barbaz");
    exampleStore.registerUser("jtcho", masterCopy);
    shadowStores.put("pineapple", exampleStore);
  }

  /**
   * Given a list of diffs and the base text, computes patches to be applied to the base text.
   *
   * @param diffs
   * @param baseText
   * @return a list of patches
   */
  public static LinkedList<Patch> makePatchesFromDiffs(List<Diff> diffs, String baseText) {
    return dmp.patch_make(baseText, new LinkedList<>(diffs));
  }

  /**
   * Applies a list of patches to input base text and returns the results.
   *
   * @param patches the patches
   * @param baseText the text to apply patches to
   * @return a pair consisting of the result of the patch and a list of booleans corresponding to which
   * patches were successfully applied
   */
  public static Pair<String, List<Boolean>> applyPatches(LinkedList<Patch> patches, String baseText) {
    Object results[] = dmp.patch_apply(patches, baseText);
    boolean[] patchResults = (boolean [])results[1];
    List<Boolean> patchResultsWrapper = new ArrayList<>();
    for (boolean b : patchResults) {
      patchResultsWrapper.add(b);
    }
    return new Pair<>((String) results[0], patchResultsWrapper);
  }

  /**
   * Applies an edit to a stored document.
   *
   * @param documentID
   * @param editEvent
   */
  protected void applyShadowEdit(String documentID, EditEvent editEvent) {
    DocumentStore store = shadowStores.get(documentID);
    String author = editEvent.getAuthor();
    List<Diff> diffs = editEvent.getDiffs().stream().map(DocumentDiffPatchService::convertDiff).collect(Collectors.toList());
    Document updatedResult = store.applyEdit(author, diffs);
    LOG.info("Applied edit to document [" + documentID + "] and got updated result: " + updatedResult.getContent());
  }

  /**
   * Enqueues an edit event to be processed by the shadow copy handler.
   *
   * @param editEvent
   */
  public void enqueueShadowEdit(EditEvent editEvent) {
    editQueue.add(editEvent);
  }

  public static Diff convertDiff(edu.upenn.sprout.api.models.Diff requestDiff) {
    return new Diff(requestDiff.getOp(), requestDiff.getText());
  }

}
