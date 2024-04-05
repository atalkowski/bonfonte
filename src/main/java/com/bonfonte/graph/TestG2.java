package com.bonfonte.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TestG2 {
  /*
   * Complete the 'getMaxTime' function below.
   *
   * The function is expected to return an INTEGER.
   * The function accepts UNWEIGHTED_INTEGER_GRAPH g as parameter.
   */
  private static boolean debug = false;

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
    Map<Integer, Set<Integer>> adjs = new TreeMap<>();

    public void addLink(int from, int to) {
      Set<Integer> nodes = adjs.computeIfAbsent(from, key -> new HashSet<>());
      nEdges++;
      nodes.add(to);
    }

    public void add2WayEdge(int a, int b) {
      addLink(a, b);
      if (a != b) addLink(b, a);
    }

    public List<Integer> getAdjs(int j) {
      Set<Integer> set = adjs.get(j);
      if (set == null) return new ArrayList<>();
      return set.stream().collect(Collectors.toList());
    }

    public int getNV() {
      return adjs.size();
    }
  }
  public static MyGraph buildGraph(List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = new MyGraph();
    for (int f = 0; f < gFrom.size(); f++) {
      g.add2WayEdge(gFrom.get(f), gTo.get(f));
    }
    return g;
  }

  static class Link<P> {
    Link prev;
    Link next;
    P p;
    public Link(P p) {
      this.p = p;
    }
  }


  static class ChainFast<P> implements Chain<P> {
    Link head;
    Link tail;
    Map<P, Link> map = new HashMap<>();

    public void append(P p) {
      if (p == null) return;
      Link q = map.get(p);
      if (q != null) {
        dbLog("Adding %s but already in chain", p);
      }
      Link<P> link = new Link(p);
      map.put(p, link);
      if (tail == null) {
        tail = link;
        head = link;
      } else {
        tail.next = link;
        link.prev = tail;
        tail = link;
      }
      dbLog("Adding %s gave %s", p, this);
    }

    public List<P> asList() {
      List<P> res = new ArrayList<>(map.size());
      Link<P> h = head;
      while (h != null) {
        res.add(h.p);
        h = h.next;
      }
      return res;
    }

    public Iterator<P> iterator() {
      List<P> list = asList();
      return list.iterator();
    }

    public void remove(P p) {
      dbLog("Removing %s from %s", p, this);
      Link<P> link = map.remove(p);
      if (link == null) {
        dbLog(p + " is not present");
        return;
      }
      Link prev = link.prev;
      Link next = link.next;
      if (prev == null) {
        head = next;
      } else {
        prev.next = next;
      }
      if (next == null) {
        tail = prev;
      } else {
        next.prev = prev;
      }
      dbLog("... gave %s", this);
    }
    public Chain<P> copy() {
      return addAll(new ChainFast<P>());
    }

    @Override
    public String toString() {
      StringBuilder res = new StringBuilder();
      if (map.isEmpty()) {
        return res.append("empty").toString();
      } else {
        Link<P> h = head;
        while (h != null) {
          if (res.length() > 0) res.append("-");
          res.append(h.p);
          h = h.next;
        }
      }
      return "Chain:" + res.toString();
    }
  }

  public interface Chain<P> {
    void append(P p);
    void remove(P p);
    Iterator<P> iterator();
    List<P> asList();
    Chain<P> copy();
    default Chain<P> addAll(Chain<P> to) {
      List<P> items = asList();
      for (P item : items) {
        to.append(item);
      }
      return to;
    }
  }

  static class ChainLinkList<P> implements Chain<P> {
    LinkedList<P> links = new LinkedList<>();

    Map<P, P> map = new HashMap<>();
    public void append(P p) {
      if (p == null) return;
      P q = map.get(p);
      if (q != null) {
        dbLog("Adding " + p.toString() + " but already in chain" );
      }
      map.put(p, p);
      links.add(p);
      dbLog("Adding %s gave %s", p, this);
    }

    public List<P> asList() {
      List<P> res = new ArrayList<>(map.size());
      res.addAll(links);
      return res;
    }

    public Iterator<P> iterator() {
      List<P> list = asList();
      return list.iterator();
    }

    public void remove(P p) {
      dbLog("Removing %s from %s", p, this);
      P item = map.remove(p);
      if (item == null) {
        dbLog(p + " is not present");
        return;
      }
      links.remove(p);
      dbLog("... gave %s", this);
    }

    @Override
    public String toString() {
      String res = "Chain:";
      if (links.isEmpty()) {
        return res + "empty";
      }
      String list = links.stream().map(Objects::toString).collect(Collectors.joining("-"));
      res += " " + list;
      return res;
    }

    public Chain<P> copy() {
      return addAll(new ChainLinkList<P>());
    }
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
        1-2-3-4-5-2-6-5-7  ?? We should allow this based on edges walked.
   */

  public static String getEdgeKey(Integer from, Integer to) {
    if (from < to) return from + "|" + to;
    return to + "|" + from;
  }

  public static class Walk {
    MyGraph g;
    Integer vertex;
    List<Integer> path = new ArrayList<>();
    Set<String> edgesWalked = new HashSet<>();
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
      return String.format("Walk vertexes=%s leng=%s: %s (edges = %s)",
          path.size(), edgesWalked.size(), path, edgesWalked);
    }
  }

  public static List<Walk> findAllTerminalPaths(MyGraph g, int from) {
    List<Walk> result = new ArrayList<>();
    Stack<Walk> stack = new Stack<>();
    Walk origin = new Walk(g, from);
    stack.add(origin);
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
        // ... is counted as a walk of length 3. We may like to think of this as length 2 by excluding when
        // we land on origin by --- but it is a bit sucky. Depends on the problem definition.
        result.add(sofar);
      }
    }
    return result;
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
        List<Integer> adjs = g.getAdjs(n);
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

  private static List<Integer> getDistinct(List<Integer>... lists) {
    return Arrays.asList(lists).stream()
        .flatMap(List::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  public static int getMaxTimeNotWorking(int gNodes, List<Integer> gFrom, List<Integer> gTo) {
    MyGraph g = buildGraph(gFrom, gTo);
    int max = 0;
    int maxposs = g.adjs.size();
    List<Integer> nodes = getDistinct(gFrom);
    for (int from : nodes) {
      int fmax = getMax(g, from);
      if (fmax > max) max = fmax;
      if (max == maxposs) break;
    }
    return max;
  }

  public static int getMaxTime(int gNodes, List<Integer> gFrom, List<Integer> gTo) {
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
          max = walk.edgesWalked.size();
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

  private static long now() {
    return System.currentTimeMillis();
  }

  private static Chain<Integer> testChain(Chain<Integer> c) {
    long t1 = now();
    for (int i = 1; i <= 100000; i++) {
      c.append(i);
    }
    long t2 = now();
    for (int i = 30; i < 8000; i += 2) {
      c.remove(i);
    }
    for (int i = 20; i < 99997; i++) {
      c.remove(i);
    }
    long t3 = now();
    log("Timings for %s are build %s and removes %s", c.getClass().getSimpleName(), t2 - t1, t3 - t2);
    log("Here is the chain: %s", c);
    return c;
  }

  
  @Test
  public void testChainTimings() {
    testChain(new ChainFast<Integer>());
    testChain(new ChainLinkList<Integer>());
    testChain(new ChainFast<Integer>());
    testChain(new ChainLinkList<Integer>());
  }

  @Test
  public void testExample1() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8);
    int res = getMaxTime(8, from, to);
    Assert.assertEquals(5, res);
  }


  @Test
  public void testExample2() {
    List<Integer> from = Arrays.asList(1, 2, 3, 3, 3, 6, 7, 5);
    List<Integer> to   = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 8);
    int res = getMaxTime(8, from, to);
    Assert.assertEquals(8, res);
  }

  @Test
  public void testExample3() {
    List<Integer> from = Arrays.asList(1, 2, 3, 4);
    List<Integer> to   = Arrays.asList(2, 3, 4, 1);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(4, res);
  }

  @Test
  public void testExample4() {
    List<Integer> from = Arrays.asList(1, 2, 2, 2, 3, 6, 4, 5);
    List<Integer> to   = Arrays.asList(2, 3, 5, 6, 4, 5, 5, 7);
    int res = getMaxTime(from.size(), from, to);
    Assert.assertEquals(8, res);
  }

}
