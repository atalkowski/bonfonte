package com.bonfonte.geek;

import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.junit.Assert;
import org.junit.Test;

public class TestPermuteString {

  @Test
  public void testPermuteAbc() {
    PermuteString perm = new PermuteString("abc");
    int inx = 0;
    String[] expected = { "abc", "acb", "bac", "bca", "cab", "cba" };
    while (perm.hasNext()) {
      inx ++;
      if (inx > expected.length) {
        Assert.fail("Too many permutations!");
      }
      String pinx = perm.next();
      System.out.println(inx + " = " +  pinx + " expecting " + expected[inx-1]);
    }
    Assert.assertEquals(expected.length, inx);
  }

  @Test
  public void testPermute10() {
    PermuteString perm = new PermuteString("abcdefgh");
    int max = 8 * 7 * 6 * 5 * 4 * 3 * 2;
    int inx = 0;
    while (perm.hasNext()) {
      inx ++;
      if (inx > max) {
        Assert.fail("Too many permutations!");
      }
      String pinx = perm.next();
      if (inx == max || inx % 4000 == 1) System.out.println(inx + " = " +  pinx);
    }
    Assert.assertEquals(max, inx);
  }

}
