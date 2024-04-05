package com.bonfonte.testing;

import org.junit.Assert;
import org.junit.Test;

import java.util.PriorityQueue;
import java.util.TreeMap;

public class GuideWTest {
  // Given an array of integers .. find the index of the second highest element
  // Generalize to make this use the nth
  private static int findIndex(int[] a, int nth) {
    TreeMap<Integer,Integer> map = new TreeMap<>();

    for (int i = 0; i < a.length; i++) {
      int value = a[i];
      Integer inx = map.get(value);
      if (inx == null) {
        map.put(value, i);
        if (map.size() > nth) {
          map.remove(map.firstKey());
        }
      }
    }
    if (map.size() == 0) return -1;
    return map.get(map.firstKey());
  }

  @Test
  public void test1() {
    int[] a = new int[] { 1, 4, 3 };
    int inx = findIndex(a, 2);
    Assert.assertEquals(2, inx);
  }

  @Test
  public void test2() {
    int[] a = new int[] { 2, 3, -1, 10, 11, 11, 1, 4, 10 };
    int inx = findIndex(a, 2);
    Assert.assertEquals(3, inx);
  }

  @Test
  public void test3() {
    int[] a = new int[] { 2, 3, 10, 1, 9, 7, 4, 10, 5, 8, 9, 6, 9, 1, 3, 4, 5, 6, 7, 8, 9 };
    int inx = findIndex(a, 4);
    Assert.assertEquals(5, inx);
  }

  private static class Rank implements Comparable<Rank>{
    int index;
    int value;

    public Rank(int index, int value) {
      this.index = index;
      this.value = value;
    }

    @Override
    public int compareTo(Rank o) {
      int res = Integer.compare(o.value, value);
      return res == 0 ? Integer.compare(index, o.index) : res;
    }
  }
  // Using priority queue
  private static int findIndexQ(int[] a, int nth) {
    PriorityQueue<Rank> q = new PriorityQueue<>(nth);
    for (int i = 0; i < a.length; i++) {
      int value = a[i];
      Rank r = new Rank(i, value);
      if (q.size() < nth) {
        q.add(r);
      } else {

      }
    }
    return 0;
  }
}
