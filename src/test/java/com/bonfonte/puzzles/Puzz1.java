package com.bonfonte.puzzles;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Puzz1 {

  public static void log(String s, Object... objects) {
    System.out.println(String.format(s, objects));
  }

  /*
    Given set of n integers - find all triplets { a, b, c } where a + b + c = 0 and a <= b <= c
   */

  private static class Pair implements Comparable<Pair>{
    final int a;
    final int b;
    public Pair(int x, int y) {
      a = x; b = y;
    }
    public String toString() {
      return a + " " + b;
    }

    @Override
    public int compareTo(Pair o) {
      int res = Integer.compare(a, o.a);
      return res == 0 ? Integer.compare(b, o.b) : res;
    }

    @Override
    public int hashCode() {
      return Integer.hashCode(a) * 17 + Integer.hashCode(b);
    }
    @Override
    public boolean equals(Object o) {
      if (o != null && o.getClass().equals(this.getClass())) {
        return compareTo((Pair)o) == 0;
      }
      return false;
    }
  }

  private static String pad(int i) {
    String res = "";
    String sign = "p";
    if (i < 0) {
      sign += "b";
      i = 1000 + i;
    }
    res = "" + i;
    while (res.length() < 3) {
      res = "z" + res;
    }
    return sign + res;
  }
  private static String makeTripleKey(int a, int b, int c) {
    return String.format("%s:%s:%s", pad(a), pad(b), pad(c));
  }

  private static List<List<Integer>> findTriples(int... ints) {
    List<Integer> list = new ArrayList<>(ints.length);
    for (int i : ints) {
      list.add(i);
    }
    list.sort(Integer::compareTo);
    int size = list.size();
    if (size > 50) {
      String s = list.subList(0, 10) + "..." + list.subList(size/2 - 5, size/2 + 5) + "..." + list.subList(size - 10, size);
      log( "Initial integer list (sorted) = %s", s);
    } else {
      log("Initial integer list (sorted) = %s ...", list);
    }
    Map<String, List<Integer>> map = new TreeMap<>();
    for (int i = 0; i < list.size(); i++) {
      int a = list.get(i);
      for (int j = i+1; j < list.size(); j++) {
        int b = list.get(j);
        for (int k = j+1; k < list.size(); k++) {
          int c = list.get(k);
          if (a + b + c == 0) {
            map.put(makeTripleKey(a, b, c), Arrays.asList(a, b, c));
          }
        }
      }
    }
    return map.values().stream().collect(Collectors.toList());
  }

  private static List<List<Integer>> findTriples1(int... ints) {
    Map<Integer, Set<Pair>> map = new TreeMap<>();
    int totalZeroes = 0;
    for (int k : ints) {
      Set<Pair> set = map.computeIfAbsent(k, key -> new TreeSet<>());
      map.put(k, set);
      if (k == 0) totalZeroes++;
    }

    for (int i = 0; i < ints.length; i++) {
      int a = ints[i];
      for (int j = 0; j < ints.length && j != i; j++) {
        int b = ints[j];
        if (a <= b) {
          int k = 0 - (a + b); // This ensures k + a + b == 0
          if (k >= b) {
            if (a == k && totalZeroes < 3) continue; // a == b == k == 0 we can only allow this if there are 3 0's
            Set<Pair> pairs = map.get(k);
            if (pairs != null) {
              Pair pair = new Pair(a, b);
              pairs.add(pair);
            }
          }
        }
      }
    }
    List<List<Integer>> res = new ArrayList<>();
    for (Integer k : map.keySet()) {
      Set<Pair> pairs = map.get(k);
      for (Pair pair : pairs) {
        res.add(Arrays.asList(pair.a, pair.b, k));
      }
    }
    return res;
  }

  private void testTriples(int... ints) {
    long t1 = System.currentTimeMillis();
    List<List<Integer>> res = findTriples(ints);
    long t2 = System.currentTimeMillis();
    for (List<Integer> list : res) {
      log("Found triple %s", list);
    }
    log("Total time for %s items was %sms", ints.length, t2 - t1);
  }

  @Test
  public void testTriples() {
    testTriples(-1, 0, 1, 2, -1, -4);
  }

  private void testTimer(int size) {
    Random r = new Random(size);
    int offset = size/2;
    int[] ints = new int[size];
    for (int i = 0; i < size; i++) {
      int k = r.nextInt(size) - offset;
      ints[i] = k;
    }
    testTriples(ints);
  }

  @Test
  public void testTimer10() {
    testTimer(10);
  }

  @Test
  public void testTimer100() {
    testTimer(100);
  }
  @Test
  public void testTimer1000() {
    testTimer(1000);
  }

}
