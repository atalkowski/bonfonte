package com.bonfonte.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bag<T> {
  private List<T> contents = new ArrayList<>(2);

  public void add(T item) {
    contents.add(item);
  }

  public int size() {
    return contents.size();
  }

  public Iterator<T> iterator() {
    return contents.iterator();
  }
}
