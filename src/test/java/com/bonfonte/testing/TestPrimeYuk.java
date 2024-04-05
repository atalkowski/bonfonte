package com.bonfonte.testing;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;
public class TestPrimeYuk {

  // This class was difficult because of a wierd specification .. to output n-1 lines for input of n lines.
  // They say : for input 2 1 3 4 5 you must output:
  // 2
  // 2
  // 2 3
  // 2 3 5
  // In other words the first non prime (=1) print existing found so far but NOT for non prime 4. As we want to
  // show maximum number of primes if those are available.
  // So the issue was to decide which lines to include. The only interpretation I could come up with is this:
  // If there are no primes print a single line containing " " (as explicitly asked).
  // Otherwise print the current list of primes found so far with a space at the end .. but
  // If no primes are found so far then we should only print " " if we know that there are not enough primes to make
  // up the required 4 lines. Yeuch - what a crappy spec.
  // Actually it still isn't solved but hacker rank passed all tests.
  private static String log(String s, Object... obs) {
    String res = String.format(s, obs);
    System.out.println(res);
    return res;
  }
  private static class Prime {

    private static TreeSet<Integer> primes = init(2, 3, 5, 7, 11, 13, 17, 19);
    private static TreeSet<Integer> others = init(23);

    private static TreeSet<Integer> addThese(TreeSet<Integer> set, int... list) {
      for (int p : list) {
        set.add(p);
      }
      return set;
    }

    private static TreeSet<Integer> init(int... values) {
      return addThese(new TreeSet<>(), values);
    }

    private static boolean isPrime(int p) {
      if (primes.contains(p) || others.contains(p)) return true;
      Integer last = primes.last();
      if (p < last) return false;
      for (int prime : primes) {
        int res = p % prime;
        if (res == 0) return false;
      }

      int next = last;
      while (next * next < p) {
        next = next + 2;
        if (p % next == 0) return false;
      }
      others.add(p);
      return true;
    }

    private static Scanner getScanner(InputStream in) {
      try {
//        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return new Scanner(in);
      } catch (Exception e) {
        return new Scanner(new ByteArrayInputStream("".getBytes()));
      }
    }

    public static String checkPrime(InputStream in) {
      int found = 0;
      int total = 0;
      List<Integer> results = new ArrayList<>();

      if (in != null) {
        Scanner sc = getScanner(in);
        while (sc.hasNextInt()) {
          int value = sc.nextInt();
          total ++;
          if (isPrime(value)) {
            found++;
            results.add(value);
          } else {
            results.add(-1);
          }
        }
      }
      StringBuilder sb = new StringBuilder();
      if (found == 0) {
        return log(" ");
      }
      int totalBad = 0;
      for (int i=0; i < results.size(); i++) {
        int value = results.get(i);
        if (value > 1) {
          sb.append(value).append(" ");
        } else {
          totalBad++;
          if (totalBad + found >= total) continue;
        }
        log(sb.toString());
      }
      return sb.toString();
    }
  }
//  public static void main(String[] args) {
//    /* Enter your code here. Print output to STDOUT. Your class should be named Solution. */
//  }

  @Test
  public void test1() {
    InputStream is = new ByteArrayInputStream("2 1 3 4 5".getBytes());
    String s = Prime.checkPrime(is);
    Assert.assertEquals("2 3 5 ", s);
  }

  @Test
  public void test2() {
    InputStream is = new ByteArrayInputStream("4 5 49 29 30".getBytes());
    String s = Prime.checkPrime(is);
    Assert.assertEquals("5 29 ", s);
  }


  @Test
  public void test3() {
    InputStream is = new ByteArrayInputStream("4 49 30 56 180 1000 1045".getBytes());
    String s = Prime.checkPrime(is);
    Assert.assertEquals(" ", s);
  }

  @Test
  public void test4() {
    InputStream is = new ByteArrayInputStream("9000 103 109 201 203 205 207 101 107 209 1023 31".getBytes());
    String s = Prime.checkPrime(is);
    Assert.assertEquals("103 109 101 107 31 ", s);
  }

  @Test
  public void test5() {
    InputStream is = null;
    String s = Prime.checkPrime(is);
    Assert.assertEquals(" ", s);
  }
  @Test
  public void test6() {
    InputStream is = new ByteArrayInputStream("103 109 12002 107 31 37".getBytes());;
    String s = Prime.checkPrime(is);
    Assert.assertEquals("103 109 107 31 37 ", s);
  }

}
