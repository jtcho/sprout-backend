package edu.upenn.sprout.doc;

import com.sksamuel.diffpatch.DiffMatchPatch.Diff;
import com.sksamuel.diffpatch.DiffMatchPatch.Patch;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import edu.upenn.sprout.utils.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates all of the shadow copies for a particular shared document.
 *
 * @author jtcho
 * @version 2016.11.06
 */
public class DocumentStore {

  private Map<String, Document> shadowCopies;

  public DocumentStore() {
    shadowCopies = new HashMap<>();
  }

  public boolean isRegisterd(String author) {
    return shadowCopies.containsKey(author);
  }

  /**
   * Registers a user with this particular document.
   *
   * @param author
   * @param copy
   */
  public void registerUser(String author, Document copy) {
    if (shadowCopies.containsKey(author)) {
      throw new IllegalStateException("Author " + author + " is already registered.");
    }
    shadowCopies.put(author, copy);
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
    LinkedList<Patch> patches = DocumentDiffPatchService.makePatchesFromDiffs(diffs, workingCopy.getContent());
    Pair<String, List<Boolean>> results = DocumentDiffPatchService.applyPatches(patches, workingCopy.getContent());
    Document updatedCopy = new Document(workingCopy.getId(), workingCopy.getRevisionNumber() + 1, workingCopy.getTitle(), results.getFirst());
    shadowCopies.put(author, updatedCopy);
    return updatedCopy;
  }
}
