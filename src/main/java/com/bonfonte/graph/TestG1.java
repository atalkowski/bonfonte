package com.bonfonte.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
public class TestG1 {
    /*
     * Complete the 'getMaxTime' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts UNWEIGHTED_INTEGER_GRAPH g as parameter.
     */

  /*
   * For the unweighted graph, <name>:
   *
   * 1. The number of nodes is <name>Nodes.
   * 2. The number of edges is <name>Edges.
   * 3. An edge exists between <name>From[i] and <name>To[i].
   *
   */

  public static class MyGraph {
    int nEdges;
    Map<Integer, Set<Integer>> adjs = new TreeMap<>();

    public void addLink(int from, int to) {
      Set<Integer> nodes = adjs.computeIfAbsent(from, key -> new HashSet<>());
      nEdges++;
      nodes.add(to);
    }

    public Set<Integer> getAdjs(int j) {
      return adjs.computeIfAbsent(j, key -> new HashSet<>());
    }

    public int getNV() {
      return adjs.size();
    }
  }
  public static MyGraph buildGraph(List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = new MyGraph();
    for (int f = 0; f < gFrom.size(); f++) {
      g.addLink(gFrom.get(f), gTo.get(f));
    }
    return g;
  }

  public static int getMax(MyGraph g, int from) {
    List<Integer> list = new ArrayList<>();
    list.add(from);
    int depth = 0;
    Set<Integer> visited = new HashSet<>();
    while (!list.isEmpty()) {
      List<Integer> next = new ArrayList<>();
      boolean hasnext = false;
      for (int n : list) {
        Set<Integer> adjs = g.getAdjs(n);
        for (int v : adjs) {
          hasnext = true;
          if (visited.add(v)) {
            next.add(v);
          }
        }
        if (hasnext) depth++;
        list = next;
      }
    }
    return depth;
  }

  public static int getMaxTime(int gNodes, List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = buildGraph(gFrom, gTo);
    int max = 0;
    int maxposs = g.adjs.size();
    for (int from : gFrom) {
      int fmax = getMax(g, from);
      if (fmax > max) max = fmax;
      if (max == maxposs) break;
    }
    return max;
  }

  @Test
  public void testExample1() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(5, res);
  }

  @Test
  public void testExample2() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7, 5);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 8);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(6, res);
  }

  @Test
  public void testExample3() {
    List<Integer> from = Arrays.asList(1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 1);
    List<Integer> to   = Arrays.asList(2, 1, 3, 2, 4, 3, 5, 4, 6, 5, 1, 6);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(5, res);
  }

  @Test
  public void testExample4() {
    List<Integer> from = Arrays.asList(1, 2, 3, 1, 3, 2);
    List<Integer> to   = Arrays.asList(2, 3, 1, 3, 2, 1);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(2, res);
  }

}
