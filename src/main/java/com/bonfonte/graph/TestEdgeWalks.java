package com.bonfonte.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TestEdgeWalks {
  /*
   * Complete the 'getMaxTime' function below.
   *
   * The function is expected to return an INTEGER.
   * The function accepts UNWEIGHTED_INTEGER_GRAPH g as parameter.
   */
  private static final boolean debug = false;

  public static void log(String s, Object... objects) {
    System.out.println(String.format(s, objects));
  }

  public static void dbLog(String s, Object... objects) {
    if (debug) {
      System.out.println(String.format(s, objects));
    }
  }

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
    Map<Integer, List<Integer>> adjs = new TreeMap<>();

    public void addLink(int from, int to) {
      List<Integer> nodes = adjs.computeIfAbsent(from, key -> new ArrayList<>());
      nEdges++;
      nodes.add(to);
    }

    public void add2WayEdge(int a, int b) {
      addLink(a, b);
      if (a != b) addLink(b, a);
    }

    public List<Integer> getAdjs(int j) {
      List<Integer> list = adjs.get(j);
      if (list == null) return new ArrayList<>();
      return new ArrayList<>(list);
    }

    public int getNV() {
      return adjs.size();
    }
  }

  public static MyGraph buildGraph(int... pairs) {
    MyGraph g = new MyGraph();
    for (int f = 0; f < pairs.length - 1; f += 2) {
      g.add2WayEdge(pairs[f], pairs[f+1]);
    }
    return g;
  }

  public static MyGraph buildGraph(List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = new MyGraph();
    for (int f = 0; f < gFrom.size(); f++) {
      g.add2WayEdge(gFrom.get(f), gTo.get(f));
    }
    return g;
  }

  /*
      Consider
                  3 --- 4
                /        \
       1 ---- 2 --------- 5 ------7
               \         /
                \__ 6 __/

      To get from 1 to 7 ... we could go
        1-2-5-7
        1-2-3-4-5-7
        1-2-6-5-7
        1-2-3-4-5-2-6-5-7  We should allow this based on edges walked. (See testExample4)
   */

  public static String getEdgeKey(Integer from, Integer to) {
    if (from < to) return from + "-" + to;
    return to + "-" + from;
  }

  public static class Walk {
    MyGraph g;
    Integer vertex;
    List<Integer> path = new ArrayList<>();
    Set<String> edgesWalked = new TreeSet<>();

    // This class is critical to the algorithm here for computing longest paths within a graph.
    // The precise rules for determining a "path" requires careful definition.
    // Most GRAPH problems require us to solve the shortest path from A to B - which is not too
    // difficult because we do a depth first search marking visited vertices as we go.
    // However - finding the longest path (in this solution) requires a more subtle approach:
    // * Keep a record of edges walked (not using vertices visited here);
    // * We don't want to allow an edge to be walked in both directions in this solution (but could).
    // * Therefore, we keep a record of each edge in a direction-independent way (M, N) where M <= N.
    // * We allow a walk (M, M) providing such an edge exists. For each M only one M=M can exist (a graph property).
    // So finally we use a Walk class which is initialized from (graph, vertex) or (walk-parent, vertex):
    // * A base graph=g that it belongs to.
    // * A vertex in graph g that identifies the starting point of this walk instance (not the origin necessarily)
    // * A path (list of integers) that show us how we got here (=parent.path + this.vertex)
    // * The edgesWalked - a set of strings "M-N" where M<=N  giving us a quick way to check where we cannot go.
    // Then when doing our depth search - at each vertex we are allowed to go to any other vertex providing
    // it is not one of existing edgesWalked set.
    // This allows us to keep walking and for a given origin and graph search for the longest path ...
    // discarding any path that is terminal when other paths can continue to be explored.
    public Walk(MyGraph g, Integer from) {
      this.g = g;
      this.vertex = from;
      path.add(from);
    }

    public Walk(Walk parent, Integer vertex) {
      this.g = parent.g;
      this.vertex = vertex;
      this.path.addAll(parent.path);
      this.path.add(vertex);
      this.edgesWalked.addAll(parent.edgesWalked);
      this.edgesWalked.add(getEdgeKey(parent.vertex, vertex));
    }

    public boolean isWalkedAlready(Integer adjacent) {
      return this.edgesWalked.contains(getEdgeKey(vertex, adjacent));
    }

    @Override
    public String toString() {
      return String.format("Walk vertexes=%s leng=%s: %s",
          path.size(), edgesWalked.size(), path);
    }
  }


  private static Comparator<Integer> DSC = new Comparator<>() {
    public int compare(Integer a, Integer b) {
      return Integer.compare(b, a);
    }
  };

  public static List<Walk> findAllTerminalPaths(MyGraph g, int from) {
    List<Walk> result = new ArrayList<>();
    Stack<Walk> stack = new Stack<>();
    Walk origin = new Walk(g, from);
    stack.push(origin);
    while (!stack.isEmpty()) {
      Walk sofar = stack.pop();
      List<Integer> adjs = g.getAdjs(sofar.vertex);
      boolean isTerminal = true;
      for (Integer adj : adjs) {
        if (sofar.isWalkedAlready(adj)) continue;
        Walk next = new Walk(sofar, adj);
        stack.push(next);
        isTerminal = false;
      }
      if (isTerminal) {
        // TODO or not TODO ... this means a circular walk 1 - 2 - 3 - 1  (start == end == 1)
        // ... is counted as a walk of length 3.
        // It depends on the problem definition:
        // 1. Find all paths from an origin that cannot go any further without tracing a previously walked edge -
        // regardless of direction it was walked **.
        // 2. As above but restricting that the start and end points are different.
        // 3. Note (**) if we allow walking the other way along an edge than this means that edges direction must
        // be taken in to consideration. An easy fix in getEdgeKey(from, to) so that 1-2 different from 2-1.
        result.add(sofar);
      }
    }
    return result;
  }

  public static <V> List<V> getDistinct(List<V> input) {
    return input.stream().distinct().collect(Collectors.toList());
  }

  public static int getMaxWalkDistance(int gNodes, List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = buildGraph(gFrom, gTo);
    int max = 0;
    List<Walk> biggest = new ArrayList<>();
    List<Integer> nodes = getDistinct(gFrom);
    for (Integer node : nodes) {
      List<Walk> walks = findAllTerminalPaths(g, node);
      for (Walk walk : walks) {
        dbLog("For %s found terminal walk %s", node, walk);
        int pathleng = walk.edgesWalked.size();
        if (pathleng > max) {
          max = pathleng;
          biggest.clear();
        }
        if (pathleng == max) biggest.add(walk);
      }
    }
    for (Walk walk : biggest) {
      log("This walk is maximum leng: %s", walk);
    }
    return max;
  }

  public static List<Walk> findAllPaths(MyGraph g, int from, int to) {
    List<Walk> result = new ArrayList<>();
    Stack<Walk> stack = new Stack<>();
    Walk origin = new Walk(g, from);
    stack.add(origin);
    if (from == to) result.add(origin);
    while (!stack.isEmpty()) {
      Walk sofar = stack.pop();
      List<Integer> adjs = g.getAdjs(sofar.vertex);
      boolean isTerminal = true;
      for (Integer adj : adjs) {
        if (sofar.isWalkedAlready(adj)) continue;
        Walk next = new Walk(sofar, adj);
        if (adj == to) {
          result.add(next);
        }
        stack.push(next);
        isTerminal = false;
      }
    }
    return result;
  }

  private static long now() {
    return System.currentTimeMillis();
  }

  @Test
  public void testExample1() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8);
    int res = getMaxWalkDistance(8, from, to);
    Assert.assertEquals(5, res);
  }


  @Test
  public void testExample2() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7, 5);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 8);
    int res = getMaxWalkDistance(8, from, to);
    Assert.assertEquals(8, res);
  }

  @Test
  public void testExample3() {
    List<Integer> from = Arrays.asList(1, 2, 3, 4);
    List<Integer> to   = Arrays.asList(2, 3, 4, 1);
    int res = getMaxWalkDistance(from.size(), from, to);
    Assert.assertEquals(4, res);
  }

  @Test
  public void testExample4() {
    List<Integer> from = Arrays.asList(1, 2, 2, 2, 3, 6, 4, 5);
    List<Integer> to   = Arrays.asList(2, 3, 5, 6, 4, 5, 5, 7);
    int res = getMaxWalkDistance(from.size(), from, to);
    Assert.assertEquals(8, res);
  }

  @Test
  public void testExample5() {
    List<Integer> from = Arrays.asList(1, 2, 2, 2, 2, 3, 6, 4, 5);
    List<Integer> to   = Arrays.asList(2, 3, 5, 6, 6, 4, 5, 5, 7);
    int res = getMaxWalkDistance(from.size(), from, to);
    Assert.assertEquals(8, res);
  }


  private static void runToFromTests(int from, int to, int... pairs) {
    MyGraph g = buildGraph(pairs);
    List<Walk> walks = findAllPaths(g, from, to);
    log("Here are the paths from %s to %s in ascending size:", from, to);
    walks.sort(new Comparator<Walk>() {
      @Override
      public int compare(Walk o1, Walk o2) {
        int res = Integer.compare(o1.edgesWalked.size(), o2.edgesWalked.size());
        return res;
      }
    });
    for (Walk w : walks) {
      log(" %s", w);
    }
  }

  @Test
  public void testFindPathsFromTo() {
    runToFromTests( 1, 8, 1, 2, 2, 3, 3, 4, 3, 5, 3, 6, 6, 7, 7, 8, 5, 8);
    runToFromTests( 1, 4, 1, 2, 2, 3, 3, 4, 3, 5, 3, 6, 6, 7, 7, 8, 5, 8);
  }
}
