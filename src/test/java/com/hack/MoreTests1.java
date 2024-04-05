package com.hack;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MoreTests1 {

  public void log(String s) {
    System.out.println(s);
  }

  public static class MyGraph {

    private Map<Integer, List<Integer>> adjs = new TreeMap<>();
    public int nVertices;
    public int nEdges;

    public int root;

    public MyGraph(int nVertices) {
      this.nVertices = nVertices;
      for (int edge = 1; edge <= nVertices; edge++) {
        adjs.put(edge, new ArrayList<>());
      }
    }

    public void putOneWayEdge(int v1, int v2) {
      if (v1 < 1 || v2 < 1 || v1 > nVertices || v2 > nVertices) throw new RuntimeException("Illegal edge " + v1 + " -> " +v2);
      List<Integer> v1list = adjs.computeIfAbsent(v1, x -> new ArrayList<>());
      v1list.add(v2);
    }
    public void addEdge(int v1, int v2) {
      putOneWayEdge(v1, v2);
      if (v1 != v2) putOneWayEdge(v2, v1);
    }

    public List<Integer> getAdjacents(int v1) {
      List<Integer> res = adjs.get(v1);
      return res == null ? new ArrayList<>() : res;
    }
  }

  static int[] test1 = { 6, 2,   1, 2,   2, 3,   3, 4,   1, 5 };

  private MyGraph buildGraph( int[] def ) {
    int nVerts = def[0];
    int root = def[1];
    int index = 2;
    MyGraph g = new MyGraph(nVerts);
    while ( index + 1 < def.length ) {
      g.addEdge(def[index], def[index + 1]);
      index += 2;
    }
    g.root = root;
    return g;
  }

  private Map<Integer, Integer> getDistances(MyGraph g1, int root) {
    List<Integer> verts = new ArrayList<>();
    Map<Integer, Integer> dists = new TreeMap<>();
    int steps = 0;
    verts.add(root);
    dists.put(root, 0);
    while (!verts.isEmpty()) {
      steps++;
      List<Integer> nextVerts = new ArrayList<>();
      for (int vx : verts) {
        // Marked
        List<Integer> adjs = g1.getAdjacents(vx);
        for(Integer vadj : adjs) {
          if (dists.containsKey(vadj)) continue;
          nextVerts.add(vadj);
          dists.put(vadj, steps);
        }
      }
      verts = nextVerts;
    }
    // Fill in the missing
    for (int vx = 1; vx <= g1.nVertices; vx++) {
      if (dists.get(vx) == null) dists.put(vx, -1);
    }
    return dists;
  }

  @Test
  public void testShortestDist() {
    MyGraph g1 = buildGraph(test1);
    Map<Integer, Integer> distances = getDistances(g1, g1.root);
    log("Distance from " + g1.root + ":" + distances);
    log("Distance from " + 4 + ":" + getDistances(g1, 4));
  }


}
