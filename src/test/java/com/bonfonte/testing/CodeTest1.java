package com.bonfonte.testing;

import org.junit.Assert;
import org.junit.Test;

public class CodeTest1 {

  private static void log(String s, Object... obs) {
    System.out.println(String.format(s, obs));
  }

  public String solution(String S, int[] A) {
    // Implement your solution here
    char[] source =  S.toCharArray();
    StringBuilder sb = new StringBuilder();
    int pos = 0;
    for(;;) {
      char c = source[pos];
      sb.append(c);
      pos = A[pos];
      if (pos == 0) return sb.toString();
    }
  }

  @Test
  public void test0() {
    String s = "a";
    int[] A = new int[] {0};
    String res = solution(s, A);
    log("Test0 gave res = %s", res);
    Assert.assertEquals("a", res);
  }

  @Test
  public void test1() {
    String s = "cdeo";
    int[] A = new int[] {3, 2, 0, 1};
    String res = solution(s, A);
    log("Test1 gave res = %s", res);
    Assert.assertEquals("code", res);
  }

  @Test
  public void test2() {
    String s = "cdeenetpi";
    int[] A = new int[] {5, 2, 0, 1, 6, 4, 8, 3, 7};
    String res = solution(s, A);
    log("Test2 gave res = %s", res);
    Assert.assertEquals("centipede", res);
  }

  @Test
  public void test3() {
    String s = "bytdag";
    int[] A = new int[] {4, 3, 0, 1, 2, 5};
    String res = solution(s, A);
    log("Test3 gave res = %s", res);
    Assert.assertEquals("bat", res);
  }


}
