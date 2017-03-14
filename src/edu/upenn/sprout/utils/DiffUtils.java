package edu.upenn.sprout.utils;

import com.sksamuel.diffpatch.DiffMatchPatch;
import edu.upenn.sprout.api.models.Diff;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jtcho
 * @version 2016.11.07
 */
public class DiffUtils {

  public static List<DiffMatchPatch.Diff> convertDiffsToInternal(Collection<Diff> diffs) {
    return diffs.stream().map(DiffUtils::convertDiffToInternal).collect(Collectors.toList());
  }

  public static List<Diff> convertInternalDiffs(Collection<DiffMatchPatch.Diff> diffs) {
    return diffs.stream().map(DiffUtils::convertInternalDiff).collect(Collectors.toList());
  }

  public static Diff convertInternalDiff(DiffMatchPatch.Diff diff) {
    return new Diff(diff.operation, diff.text);
  }

  /**
   * Converts the serialized Diff from the request binding to the DiffPatch format.
   *
   * @return the converted object
   */
  public static DiffMatchPatch.Diff convertDiffToInternal(Diff requestDiff) {
    return new DiffMatchPatch.Diff(requestDiff.getOp(), requestDiff.getText());
  }

}
