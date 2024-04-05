package com.bonfonte.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class GraphTest101 {

  public static class Graph {
     Map<Integer, List<Integer>> adjs = new HashMap<>();
     int nv = 0;
     public void add1Way(int from, int to) {
       List<Integer> list = adjs.computeIfAbsent(from, k -> new ArrayList<>());
       list.add(to);
       nv++;
     }
     public void add2Way(int from, int to) {
       add1Way(from, to);
       if (from != to) {
         add1Way(to, from);
       }
     }

     public List<Integer> getAdjs(int v) {
       List<Integer> res = this.adjs.get(v);
       if (res == null) return new ArrayList<>();
       return res;
     }

     public List<Integer> getAllVertices() {
       return adjs.keySet().stream().collect(Collectors.toList());
     }

    public static Graph build1way(int... pairs) {
      int index = 0;
      Graph g = new Graph();
      while (index < pairs.length - 1) {
        g.add1Way(pairs[index++], pairs[index++]);
      }
      return g;
    }

     public static Graph build2way(int... pairs) {
       int index = 0;
       Graph g = new Graph();
       while (index < pairs.length - 1) {
         g.add2Way(pairs[index++], pairs[index++]);
       }
       return g;
     }
  }

  public static int findShortest(Graph g, int from, int to) {
    // Depth first search
    if (!g.adjs.containsKey(to)) return -1;
    if (from == to) return 0;
    int depth = 1;
    List<Integer> todo = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();
    todo.add(from);
    while (!todo.isEmpty()) {
      List<Integer> next = new ArrayList<>();
      for (Integer v : todo) {
        if (v == to) return depth;
        visited.add(v);
        List<Integer> adjs = g.getAdjs(v);
        for (Integer a : adjs) {
          if (visited.contains(a)) continue;
          visited.add(a);
          if (a == to) return depth;
          next.add(a);
        }
      }
      depth++;
      todo = next;
    }
    return -1; // No path from - to
  }

  /*
         1 -- 2 -- 3   10--11
         !    | `-----.
         5    `- 4 ---8
         `- 6 -- 7
            \
             9
   */
  @Test
  public void testShortest() {
    Graph g = Graph.build2way(1,2,2,3,2,4,2,8,4,8,1,5,5,6,6,7,6,9,10,11);
    Assert.assertEquals(2, findShortest(g, 1, 4));
    Assert.assertEquals(3, findShortest(g, 1, 9));
    Assert.assertEquals(5, findShortest(g, 9, 8));
    Assert.assertEquals(-1, findShortest(g, 10, 8));
    Assert.assertEquals(0, findShortest(g, 10, 10));
    Assert.assertEquals(1, findShortest(g, 10, 11));
    Assert.assertEquals(-1, findShortest(g, 12, 12));


  }
}
