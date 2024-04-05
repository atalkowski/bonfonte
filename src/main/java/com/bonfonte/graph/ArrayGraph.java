package com.bonfonte.graph;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ArrayGraph implements GraphInterface<Integer> {
  private int nVertices;
  private int nEdges;
  private IntBag [] adjs; // adj[v] is adjacent vertices to vertex v
  private static final IntBag EMPTY_BAG = new IntBag();
  //Constructor 1
  public ArrayGraph(int nVertices) {
    init(nVertices);
  }

  // Constructor 2
  public ArrayGraph(InputStream input) {
    Scanner scanner = new Scanner(input);
    init(scanner.nextInt());
    int edgeCount = scanner.nextInt();
    for (int edge = 0; edge < edgeCount; edge++) {
      int vertex1 = scanner.nextInt();
      int vertex2 = scanner.nextInt();
      addEdge(scanner.nextInt(), scanner.nextInt());
    }
  }
  private void init(int nVertices) {
    this.nVertices = nVertices;
    this.nEdges = 0;
    this.adjs = new IntBag[nVertices];
    for (int index = 0; index < nVertices; index++) {
      this.adjs[index] = new IntBag();
    }
  }

  public int getNVertices() {
    return nVertices;
  }

  public int getNEdges() {
    return nEdges;
  }

  public void addEdge(int v1, int v2) {
    adjs[v1].add(v2);
    if (v1 != v2) { // Why add a loop twice .. if needed then add it twice.
      adjs[v2].add(v1);
    }
    nEdges++;
  }

  public Iterator<Integer> getAdjs(int v) {
    if (v >= 0 && v < nVertices) {
      return adjs[v].iterator();
    }
    return EMPTY_BAG.iterator();
  }

  public Integer[] getAdjArray(int v) { // Avoid boxing problems
    if (v >= 0 && v < nVertices) {
      Integer[] result = new Integer[adjs[v].size()];
      Iterator<Integer> vAdj = getAdjs(v);
      int index = 0;
      while (vAdj.hasNext()) {
        result[index++] = vAdj.next();
      }
      return result;
    }
    return new Integer[0]; // Empty array.
  }

  public List<Integer> getAdjList(int v) { // Avoid boxing problems
    if (v >= 0 && v < nVertices) {
      List<Integer> result = new ArrayList<>(adjs[v].size());
      Iterator<Integer> vAdj = getAdjs(v);
      int index = 0;
      while (vAdj.hasNext()) {
        result.add(index++, vAdj.next());
      }
      return result;
    }
    return new ArrayList<>(0);
  }


}
