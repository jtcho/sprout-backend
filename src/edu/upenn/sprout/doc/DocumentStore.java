package edu.upenn.sprout.doc;

import com.sksamuel.diffpatch.DiffMatchPatch.Diff;
import com.sksamuel.diffpatch.DiffMatchPatch.Patch;
import edu.upenn.sprout.api.models.EditEvent;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import edu.upenn.sprout.utils.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates all of the shadow copies for a particular shared document.
 *
 * @author jtcho
 * @version 2016.11.06
 */
public class DocumentStore {

  private Map<String, Document> shadowCopies;
  private Map<String, EditEvent> eventsToPush;

  public DocumentStore() {
    shadowCopies = new HashMap<>();
    eventsToPush = new HashMap<>();
  }

  public boolean isRegistered(String author) {
    return shadowCopies.containsKey(author);
  }

  /**
   * Registers a user with this particular document.
   *
   * @param author
   * @param copy
   */
  public void registerUser(String author, String applicationId, String documentId, Document copy) {
    if (shadowCopies.containsKey(author)) {
      throw new IllegalStateException("Author " + author + " is already registered.");
    }
    shadowCopies.put(author, copy);
//    eventsToPush.put(author, new EditEvent(author, applicationId, documentId, new LinkedList<>()));
  }

  /**
   * Called when the given author's changes are merged into the master copy.
   * Creates diffs for every other author's shadow copy, and enqueues them.
   *
   * @param author
   * @param masterCopy
   */
  public void enqueueChangesForClient(String author, Document masterCopy) {
//    shadowCopies.keySet().stream()
//        .filter(user -> ! user.equals(author))
//        .map(user -> {
//          Document shadowCopy = shadowCopies.get(user);
//          List<Diff> diffs = DocumentDiffPatchService.makeDiffsFromText(shadowCopy.getContent(), masterCopy.getContent());
//          EditEvent nextEvent
//          return new Pair<>(user, diffs);
//        })
//        .forEach(pair -> eventsToPush.get(pair.getFirst()).addAll(pair.getSecond()));
  }

  public EditEvent getQueuedDiffs(String author) {
    return eventsToPush.get(author);
  }

  public void flushQueuedDiffs(String author) {
    eventsToPush.remove(author);
  }

  public Set<String> getRegisteredUsers() {
    return shadowCopies.keySet();
  }

  /**
   * Given a list of diffs, computes the requisite patches to update the shadow copy for a particular user
   * and updates the document.
   * It is expected after this that the updated document will be enqueued as an edit event to be propagated to
   * the master copy.
   *
   * @param author the author
   * @param diffs the diffs list
   * @return the updated document
   */
  public Document applyEdit(String author, List<Diff> diffs) {
    Document workingCopy = shadowCopies.get(author);
    Pair<String, List<Boolean>> results = DocumentDiffPatchService.applyDiffs(diffs, workingCopy.getContent());
    Document updatedCopy = new Document(workingCopy.getId(), workingCopy.getRevisionNumber() + 1, workingCopy.getTitle(), results.getFirst());
    shadowCopies.put(author, updatedCopy);
    return updatedCopy;
  }
}
