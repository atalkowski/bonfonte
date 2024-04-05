package com.bonfonte.hacker;

import org.junit.Test;

import java.util.Iterator;
import java.util.TreeSet;

public class TreeSetTests {

  private static void log(String s, Object... args) {
    System.out.println(String.format(s, args));
  }

  @Test
  public void test1() {
    int[] arr = new int[]{10, 6, 5, 12, 1, 2, 9, 22, 7};
    TreeSet<Integer> t1 = new TreeSet<>();
    for (int v :arr) {
      t1.add(v);
    }

    Iterator<Integer> it = t1.iterator();
    int index = 0;
    while (it.hasNext()) {
      log("Element %s = %s", index++, it.next());
    }
    log("Lower of 5 is %s", t1.lower(5));
    log("Floor of 5 is %s", t1.floor(5));
    log("Ceiling of 5 is %s", t1.ceiling(5));
    log("Higher of 5 is %s", t1.higher(5));
    log("Lower of 3 is %s", t1.lower(3));
    log("Floor of 3 is %s", t1.floor(3));
    log("Ceiling of 3 is %s", t1.ceiling(3));
    log("Higher of 3 is %s", t1.higher(3));
    log("Higher of 22 is %s", t1.higher(22));
  }



}
