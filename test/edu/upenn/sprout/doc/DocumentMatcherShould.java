package edu.upenn.sprout.doc;

import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;
import com.sksamuel.diffpatch.DiffMatchPatch.Patch;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * @author jtcho
 * @version 2016.11.06
 */
public class DocumentMatcherShould {

  @Test
  public void generateDiffs() {
    String text1 = "This is a bad test!";
    String text2 = "this is also a test!";

    DiffMatchPatch dmp = new DiffMatchPatch();
    LinkedList<Diff> diffs = dmp.diff_main(text1, text2);
//    diffs.stream().forEach(System.out::println);
    LinkedList<Patch> patches = dmp.patch_make(text1, diffs);
//    patches.stream().forEach(System.out::println);
    Object[] result = dmp.patch_apply(patches, text1);
    assertEquals(text2, result[0]);
    System.out.println("Result: " + result[0]);
  }

}
