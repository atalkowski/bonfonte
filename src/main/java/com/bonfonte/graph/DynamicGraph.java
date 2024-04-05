package com.bonfonte.graph;

import java.io.InputStream;
import java.util.*;

public class DynamicGraph implements GraphInterface<Integer> {
  private int nVertices;
  private int nEdges;
  private Map<Integer, IntBag> adjs; // adj.get(v) is adjacent vertices to vertex v
  private static final IntBag EMPTY_BAG = new IntBag();

  //Constructor 1: Ignores attempts to set the number of vertices as this is dynamically set.
  public DynamicGraph(int... nVertices) {
    init();
  }

  // Constructor 2
  public DynamicGraph(InputStream input) {
    Scanner scanner = new Scanner(input);
    int vertexCount = scanner.nextInt(); // And ignore!!
    init();
    int edgeCount = scanner.nextInt();
    for (int edge = 0; edge < edgeCount; edge++) {
      int vertex1 = scanner.nextInt();
      int vertex2 = scanner.nextInt();
      addEdge(scanner.nextInt(), scanner.nextInt());
    }
  }
  private void init() {
    this.nEdges = 0;
    this.adjs = new TreeMap<>(); // Is the tree any use?
  }

  public int getNVertices() {
    return adjs.size();
  }

  public int getNEdges() {
    return nEdges;
  }


  private void putLink(int v1, int v2) {
    getAdjArray(v1).add(v2);
  }

  public void addEdge(int v1, int v2) {
    putLink(v1, v2);
    if (v1 != v2) { // Why add a loop twice .. if needed then add it twice.
      putLink(v2, v1);
    }
    nEdges++;
  }

  public Iterator<Integer> getAdjs(int v) {
    IntBag bag = adjs.get(v);
    if (bag != null) return bag.iterator();
    return EMPTY_BAG.iterator();
  }

  public IntBag getAdjArray(int v) { // Avoid boxing problems
    return adjs.computeIfAbsent(v, notFound -> new IntBag());
  }

  public List<Integer> getAdjList(int v) { // Avoid boxing problems
    IntBag bag = getAdjArray(v);
    List<Integer> result = new ArrayList<>(bag.size());
    Iterator<Integer> vAdj = bag.iterator();
    int index = 0;
    while (vAdj.hasNext()) {
      result.add(index++, vAdj.next());
    }
    return result;
  }
}
