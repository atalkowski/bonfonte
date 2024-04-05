package com.bonfonte.graph;

import java.util.Iterator;
import java.util.List;

public interface GraphInterface<T> {
  int getNVertices();

  int getNEdges();

  void addEdge(int v1, int v2);

  Iterator<Integer> getAdjs(int v);

  List<Integer> getAdjList(int v);

  default void associate(int v, T item) { // Do nothing;
  }
}
