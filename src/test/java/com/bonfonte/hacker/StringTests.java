package com.bonfonte.hacker;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.TreeSet;

public class StringTests {

  private static final String IPD = "[0-9]|[0-9][0-9]|[0-1][0-9][0-9]|2[0-4][0-9]|25[0-5]";
  private static void log(String s, Object... args) {
    System.out.println(String.format(s, args));
  }
  public static String getSmallestAndLargest(String s, int k) {
    String smallest = "";
    String largest = "";
    Comparator<String> COMP = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    };
    TreeSet<String> set = new TreeSet<>(COMP);
    int i = 0;
    while (i + k <= s.length()) {
      String sub = s.substring(i, i+k);
      set.add(sub);
      i++;
    }
    // Complete the function
    // 'smallest' must be the lexicographically smallest substring of length 'k'
    // 'largest' must be the lexicographically largest substring of length 'k'
    if (!set.isEmpty()) {
      smallest = set.pollFirst();
      set.add(smallest);
      largest = set.pollLast();
    }
    return smallest + "\n" + largest;
  }

  @Test
  public void test1() {
    String res1 = getSmallestAndLargest("AbcABCabcAbC", 3);
    Assert.assertEquals("ABC\ncAb", res1);
  }

  private static String getIpdRegx() {
    return String.format("(%s)[.](%s)[.](%s)[.](%s)", IPD, IPD, IPD, IPD);
  }
  @Test
  public void test2() {
    String regx ="([0-9]|[0-9][0-9]|[0-1][0-9][0-9]|2[0-4][0-9]|25[0-5])";
    for (int i = 0; i <= 255; i++) {
      String ip = "" + i;
      Assert.assertTrue(ip.matches(regx));
      if (i < 100) {
        Assert.assertTrue(("0" + ip).matches(regx));
        if (i < 10) {
          Assert.assertTrue(("00" + ip).matches(regx));
          log("Testing %s", ip);
        }
      }
    }
    log("Testing %s", "256");
    Assert.assertFalse("256".matches(regx));
  }

  @Test
  public void test3() {
    String regx = getIpdRegx();
    Assert.assertTrue("0.0.0.0".matches(regx));
    String[] tests = new String[] { "000.12.12.034",
        "121.234.12.12",
        "255.255.255.255",
        "23.45.12.56",
        "00.12.123.123123.123",
        "122.23",
        "Hello.IP"};
    for (int i = 0; i < tests.length; i++) {
      log("Processing case %s %s gives %s", i, tests[i], tests[i].matches(regx) );
      switch (i) {
        case 0: case 1: case 2: case 3:
          Assert.assertTrue(tests[i].matches(regx)); break;
        default:
          Assert.assertFalse(tests[i].matches(regx)); break;
      }
    }

  }
}
