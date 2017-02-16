package edu.upenn.sprout.services;

import com.google.common.annotations.VisibleForTesting;
import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Patch;
import edu.upenn.sprout.api.models.Diff;
import edu.upenn.sprout.api.models.EditEvent;
import edu.upenn.sprout.api.models.InternalEditEvent;
import edu.upenn.sprout.doc.Document;
import edu.upenn.sprout.doc.DocumentStore;
import edu.upenn.sprout.utils.DiffUtils;
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

/**
 * @author jtcho
 * @version 2016.11.06
 */
@Singleton
public class DocumentDiffPatchService {

  /**
   * Maps document IDs to the master copy of the document.
   */
  @VisibleForTesting
  protected Map<String, Document> masterCopies;
  /**
   * Maps document IDs to document stores containing shadow copies.
   */
  @VisibleForTesting
  protected Map<String, DocumentStore> shadowStores;
  /**
   * Queue of shadow copy edit events to process.
   */
  @VisibleForTesting
  protected ConcurrentLinkedQueue<InternalEditEvent> shadowEditQueue;
  /**
   * Queue of master copy edit events to process.
   */
  @VisibleForTesting
  protected ConcurrentLinkedQueue<InternalEditEvent> masterEditQueue;

  private static DiffMatchPatch dmp = new DiffMatchPatch();

  private static Logger LOG = LoggerFactory.getLogger(DocumentDiffPatchService.class);

  @VisibleForTesting
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

    // TODO Please do delete these... just for sample testing.
//    String documentId = createNewDocumentFor("jtcho");
//    registerNewUser("igorpo", documentId);
  }

  /**
   * Creates a new document for a particular user.
   *
   * @param author the user creating the document
   * @return the id of the created document
   */
  public String createNewDocumentFor(String author) {
    DocumentStore newStore = new DocumentStore();
    Document masterCopy = new Document(newStore.getId(), 0, "Untitled Copy", "");
    newStore.registerUser(author, masterCopy);
    shadowStores.put(newStore.getId(), newStore);
    masterCopies.put(newStore.getId(), masterCopy);

    return newStore.getId();
  }

  /**
   * Registers a new user for an existing document.
   *
   * @param author the user to register
   * @param documentId the id of the document
   */
  public void registerNewUser(String author, String documentId) {
    shadowStores.get(documentId).registerUser(author, masterCopies.get(documentId));
  }

  /**
   * TODO
   * @param documentId
   * @return
   */
  public boolean isValid(String documentId) {
    return masterCopies.containsKey(documentId);
  }

  /**
   * Given a list of diffs and the base text, computes patches to be applied to the base text.
   *
   * @return a list of patches
   */
  private static LinkedList<Patch> makePatchesFromDiffs(List<DiffMatchPatch.Diff> diffs, String baseText) {
    return dmp.patch_make(baseText, new LinkedList<>(diffs));
  }

  /**
   * Computes a sequential list of diffs between the input strings.
   */
  public static LinkedList<DiffMatchPatch.Diff> makeDiffsFromText(String text1, String text2) {
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

  /**
   * Applies a list of diffs to the given base text, employing a best-effort approach to
   * patching the edits.
   *
   * @return a pair consisting of the result of the patch and a list of booleans corresponding to which
   * patches were successfully applied
   */
  public static Pair<String, List<Boolean>> applyDiffs(List<DiffMatchPatch.Diff> diffs, String baseText) {
    return applyPatches(makePatchesFromDiffs(diffs, baseText), baseText);
  }

  /**
   * Applies an edit to a stored shadow document copy,
   * and then computes the diff with respect to the master copy
   * and enqueues the changes to be merged in to master.
   */
  protected void applyShadowEdit(String documentId, InternalEditEvent editEvent) {
    DocumentStore store = shadowStores.get(documentId);
    String author = editEvent.getAuthor();
    List<DiffMatchPatch.Diff> diffs = editEvent.getDiffs();
    Document updatedResult = store.applyEdit(author, diffs);
    LOG.info("Applied edit to " + author + "'s shadow copy of document [" + documentId +
        "] and got updated result: " + updatedResult.getContent());
    // Compute diff against master document.
    Document masterCopy = masterCopies.get(documentId);
    List<DiffMatchPatch.Diff> nextDiffs = makeDiffsFromText(masterCopy.getContent(), updatedResult.getContent());
    InternalEditEvent nextEvent = new InternalEditEvent(author, editEvent.getApplicationId(), documentId, nextDiffs);
    masterEditQueue.add(nextEvent);
  }

  /**
   * Applies an edit to the master document copy,
   * and then computes diffs with respect to all other editors of the document
   * and enqueues the changes to be pushed to clients.
   */
  protected void applyMasterEdit(String documentId, InternalEditEvent editEvent) {
    Document masterCopy = masterCopies.get(documentId);
    String author = editEvent.getAuthor();
    List<DiffMatchPatch.Diff> diffs = editEvent.getDiffs();
    Pair<String, List<Boolean>> results = applyDiffs(diffs, masterCopy.getContent());
    Document updatedCopy = new Document(masterCopy.getId(), masterCopy.getRevisionNumber() + 1, masterCopy.getTitle(), results.getFirst());
    LOG.info("Applied edit to " + author + "'s master copy of document [" + documentId +
        "] and got updated result: " + updatedCopy.getContent());
    masterCopies.put(documentId, updatedCopy);
    DocumentStore documentStore = shadowStores.get(documentId);
    documentStore.enqueueChangesForClient(author, updatedCopy);
  }

  /**
   * TODO
   * @param documentId
   * @param authorId
   * @return
   */
  public List<Diff> getQueuedDiffs(String documentId, String authorId) {
    List<DiffMatchPatch.Diff> diffs = shadowStores.get(documentId).getQueuedDiffs(authorId);
    List<Diff> queuedDiffs = DiffUtils.convertInternalDiffs(diffs);
    shadowStores.get(documentId).flushQueuedDiffs(authorId);
    return queuedDiffs;
  }

  public Document masterCopyForDocumentWithID(String ID) {
    return masterCopies.get(ID);
  }

  /**
   * Enqueues an edit event to be processed by the shadow copy handler.
   */
  public void enqueueShadowEdit(EditEvent editEvent) {
    shadowEditQueue.add(new InternalEditEvent(editEvent));
  }

}
