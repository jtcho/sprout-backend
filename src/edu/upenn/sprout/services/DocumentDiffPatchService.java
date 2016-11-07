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
import java.util.Collection;
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
   * Queue of shadow copy edit events to process.
   */
  private ConcurrentLinkedQueue<EditEvent> shadowEditQueue;
  /**
   * Queue of master copy edit events to process.
   */
  private ConcurrentLinkedQueue<EditEvent> masterEditQueue;

  private static DiffMatchPatch dmp = new DiffMatchPatch();

  private static Logger LOG = LoggerFactory.getLogger(DocumentDiffPatchService.class);

  @Inject
  protected DocumentDiffPatchService(ApplicationLifecycle lifecycle) {
    masterCopies = new HashMap<>();
    shadowStores = new HashMap<>();
    shadowEditQueue = new ConcurrentLinkedQueue<>();
    masterEditQueue = new ConcurrentLinkedQueue<>();

    DocumentEditHandler editDaemon = new DocumentEditHandler(this, shadowEditQueue, masterEditQueue);
    Thread shadowThread = new Thread(editDaemon);
    shadowThread.start();
    lifecycle.addStopHook(() -> CompletableFuture.runAsync(editDaemon::shutdown));

    // Please do delete these... just for sample testing.
    DocumentStore exampleStore = new DocumentStore();
    Document masterCopy = new Document("pineapple", 0, "TITLE", "barbaz");
    exampleStore.registerUser("jtcho", "Plank", "pineapple", masterCopy);
    shadowStores.put("pineapple", exampleStore);
    masterCopies.put("pineapple", masterCopy);
  }

  /**
   * Given a list of diffs and the base text, computes patches to be applied to the base text.
   *
   * @return a list of patches
   */
  private static LinkedList<Patch> makePatchesFromDiffs(List<Diff> diffs, String baseText) {
    return dmp.patch_make(baseText, new LinkedList<>(diffs));
  }

  public static LinkedList<Diff> makeDiffsFromText(String text1, String text2) {
    return dmp.diff_main(text1, text2);
  }

  /**
   * Applies a list of patches to input base text and returns the results.
   *
   * @param patches the patches
   * @param baseText the text to apply patches to
   * @return a pair consisting of the result of the patch and a list of booleans corresponding to which
   * patches were successfully applied
   */
  private static Pair<String, List<Boolean>> applyPatches(LinkedList<Patch> patches, String baseText) {
    Object results[] = dmp.patch_apply(patches, baseText);
    boolean[] patchResults = (boolean [])results[1];
    List<Boolean> patchResultsWrapper = new ArrayList<>();
    for (boolean b : patchResults) {
      patchResultsWrapper.add(b);
    }
    return new Pair<>((String) results[0], patchResultsWrapper);
  }

  public static Pair<String, List<Boolean>> applyDiffs(List<Diff> diffs, String baseText) {
    return applyPatches(makePatchesFromDiffs(diffs, baseText), baseText);
  }

  /**
   * Applies an edit to a stored shadow document copy.
   */
  protected void applyShadowEdit(String documentID, EditEvent editEvent) {
    DocumentStore store = shadowStores.get(documentID);
    String author = editEvent.getAuthor();
    List<Diff> diffs = convertDiffs(editEvent.getDiffs());
    Document updatedResult = store.applyEdit(author, diffs);
    LOG.info("Applied edit to " + author + "'s shadow copy of document [" + documentID +
        "] and got updated result: " + updatedResult.getContent());
    // Compute diff against master document.
    Document masterCopy = masterCopies.get(documentID);
    List<Diff> nextDiffs = makeDiffsFromText(masterCopy.getContent(), updatedResult.getContent());
    EditEvent nextEvent = new EditEvent(author, editEvent.getApplicationId(), documentID, nextDiffs);
    masterEditQueue.add(nextEvent);
  }

  protected void applyMasterEdit(String documentID, EditEvent editEvent) {
    Document masterCopy = masterCopies.get(documentID);
    String author = editEvent.getAuthor();
    List<Diff> diffs = editEvent.getConvertedDiffs();
    Pair<String, List<Boolean>> results = applyDiffs(diffs, masterCopy.getContent());
    Document updatedCopy = new Document(masterCopy.getId(), masterCopy.getRevisionNumber() + 1, masterCopy.getTitle(), results.getFirst());
    LOG.info("Applied edit to " + author + "'s master copy of document [" + documentID +
        "] and got updated result: " + updatedCopy.getContent());
    // Compute all diffs between server copy and all other copies.
    DocumentStore documentStore = shadowStores.get(documentID);
  }

  /**
   * Enqueues an edit event to be processed by the shadow copy handler.
   */
  public void enqueueShadowEdit(EditEvent editEvent) {
    shadowEditQueue.add(editEvent);
  }

  public static List<Diff> convertDiffs(Collection<edu.upenn.sprout.api.models.Diff> diffs) {
    return diffs.stream().map(DocumentDiffPatchService::convertDiff).collect(Collectors.toList());
  }

  /**
   * Converts the serialized Diff from the request binding to the DiffPatch format.
   *
   * @return the converted object
   */
  public static Diff convertDiff(edu.upenn.sprout.api.models.Diff requestDiff) {
    return new Diff(requestDiff.getOp(), requestDiff.getText());
  }

}
