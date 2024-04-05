package com.bonfonte.experiment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MyGen<T extends Comparable<T>> {
  private List<T> list = new ArrayList<>(3);
  private Comparator<T> COMP = new Comparator<T>() {
    @Override
    public int compare(T o1, T o2) {
      return o1.compareTo(o2);
    }
  };

  public MyGen<T> add(T item) {
    list.add(item);
    return this;
  }

  public T get(int pos) {
    if (pos >= 0 && pos < list.size()) return list.get(pos);
    return null;
  }

  public MyGen<T> clear() {
    list.clear();
    return this;
  }

  public MyGen<T> sort() {
    list.sort(COMP);
    return this;
  }

  public Iterator<T> iterator() {
    return list.iterator();
  }

  @Override
  public String toString() {
    return list.toString();
  }
}
