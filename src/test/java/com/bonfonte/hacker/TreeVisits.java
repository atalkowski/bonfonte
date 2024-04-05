package com.bonfonte.hacker;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TreeVisits {

  // ALL HERE IS TEST - EXCLUDE FROM SOLUTION
  private static void log(String s, Object... objects) {
    System.out.println(String.format(s, objects));
  }

  private static String pad(int n) {
    String max = "----------------------------->>>";
    return max.substring(0, Math.min(n, max.length()));
  }

  private static void showTree(Tree t, int indent, StringBuilder sb) {
    sb.append("\n").append(pad(indent)).append(t.toString());
    for (Tree child : t.edges) {
      showTree(child, indent + 2, sb);
    }
  }

  private static String showTree(Tree root) {
    StringBuilder sb = new StringBuilder();
    showTree(root, 0, sb);
    return sb.toString();
  }
  // -------------- End of TEST code
  private static final int COLOR_RED = 0;
  private static final int COLOR_GREEN = 1;
  private static final int MOD = 1000_000_007;
  private static final int ROOT_ID = 1;

  private enum Aggregator {

     SUM((Integer a, Integer b) -> a + b),
     PROD107(( Integer a, Integer b) -> {
       int res = a * b;
       if (res == 0) res = 1;
       if (res >= MOD || res < 0) res = res % MOD;
       return res;
     });

     private Aggregator(BiFunction<Integer,Integer,Integer> bifunc) {
       this.bifunc = bifunc;
     }
     BiFunction<Integer,Integer,Integer> bifunc;

  }
  private enum TFilter {
    RED((Tree t) -> t.color == COLOR_RED),
    GREEN((Tree t) -> t.color == COLOR_GREEN),
    EVEN((Tree t) -> t.depth % 2 == 0),
    ODD((Tree t) -> t.depth % 2 == 1),
    LEAF((Tree t) -> t.isLeaf()),
    NONLEAF((Tree t) -> !t.isLeaf());
    TFilter(Function<Tree, Boolean> func) {
      this.func = func;
    }
    Function<Tree, Boolean> func;
    public Function<Tree, Boolean> get() {
      return func;
    }
  }

  private static class TreeIterator implements Iterator<Tree> {
    private Tree root;
    private Stack<Tree> todo;

    public TreeIterator(Tree t) {
      this.root = t;
      this.todo = new Stack<>();
      if (t != null) todo.add(t);
    }

    @Override
    public boolean hasNext() {
      return !todo.empty();
    }

    @Override
    public Tree next() {
      if (todo.empty()) throw new RuntimeException("Next called when hasNext is false");
      Tree t = todo.pop();
      if (t.edges != null) {
        for (Tree child : t.edges) {
          todo.push(child);
        }
      }
      return t;
    }
  }

  // -------  CODE STARTS HERE --------------->
  private static class Tree {
    public int id;
    public int value;
    public int color;
    public int depth;

    private List<Tree> edges = new ArrayList<>();
    private Tree(int id, int value, int color) {
      this.id = id;
      this.value = value;
      this.color = color;
    }
    public boolean isLeaf() {
      return edges.isEmpty();
    }

    public String toString() {
      return "D" + depth + "(" + id + "|" + value + "|" +color + ")" + (isLeaf() ? "LEAF" : "NODE")
          + " " + (depth%2 == 0 ? "EVEN" : "ODD")
          + " " + (color == 0 ? "RED" : color == 1 ? "GREEN" : ("" + color + "?"));
    }

    public Iterator<Tree> getIterator() {
      return new TreeIterator(this);
    }
  }

  public interface Visitor {
    int getResult();
    void visitNode(Tree node);
    void visitLeaf(Tree node);
  }

  private static abstract class GenVisitor implements Visitor {
    public abstract int getNextValue(int currentValue, int value);
    public abstract int getInitialValue();

    private List<TFilter> filters = new ArrayList();
    public GenVisitor filter(TFilter filt) {
      filters.add(filt);
      return this;
    }

    Set<Tree> nodeset = new HashSet<>(); // This contains all visitied nodes (to date).
    @Override
    public int getResult() {
      int result = getInitialValue();
      for (Tree node : nodeset) {
        result = getNextValue(result, node.value);
      }
      return result;
    }

    private boolean accept(Tree node) {
      for (TFilter filt : this.filters) {
        if (!filt.get().apply(node)) return false;
      }
      return true;

    }
    private void addContributor(Tree node) {
      Iterator<Tree> it = new TreeIterator(node);
      while (it.hasNext()) {
        Tree member = it.next();
        if (accept(member)) {
          this.nodeset.add(member);
        }
      }
    }

    @Override
    public void visitNode(Tree node) {
      addContributor(node);
    }

    @Override
    public void visitLeaf(Tree node) {
      addContributor(node);
    }
  }

  public static class SumVisitor extends GenVisitor {
    public int getInitialValue() { return 0; }
    public int getNextValue(int current, int value) {return current + value;}
  }

  public static class ProductVisitor extends GenVisitor {
    public int getInitialValue() { return 1; }
    public int getNextValue(int current, int value) {
      return Aggregator.PROD107.bifunc.apply(current, value);}
  }

  public static class SumInLeavesVisitor extends SumVisitor {
    public SumInLeavesVisitor() {
      filter(TFilter.LEAF);
    }
  }

  public static class ProductRedNodesVisitor extends ProductVisitor {
    public ProductRedNodesVisitor() {
      filter(TFilter.RED);
      // Note this is nodes ... including leaves!!!
    }
  }

  public static final class FancyVisitor implements Visitor {
 ;
    private SumVisitor greenLeaf = new SumVisitor();
    private SumVisitor evenNodes = new SumVisitor();

    public FancyVisitor()  {
      greenLeaf.filter(TFilter.GREEN).filter(TFilter.LEAF);
      evenNodes.filter(TFilter.EVEN).filter(TFilter.NONLEAF);
    }

    @Override
    public int getResult() {
      return Math.abs(greenLeaf.getResult() - evenNodes.getResult());
    }

    @Override
    public void visitNode(Tree node) {
      greenLeaf.visitNode(node);
      evenNodes.visitNode(node);
    }

    @Override
    public void visitLeaf(Tree node) {
      visitNode(node);
    }
  }

  private static void setDepths(Tree root) {
    // We could easily use recursion but ... we could blow the stack (paranoif Hacker tests in mind)
    int depth = 0;
    Set<Integer> done = new HashSet<>();
    List<Tree> todo = new ArrayList<>();
    todo.add(root);
    while (!todo.isEmpty()) {
      List<Tree> nextLayer = new ArrayList<>();
      for (Tree t : todo) {
        if (done.contains(t.id)) { // Reject bogus trees.
          continue;
        }
        done.add(t.id);
        t.depth = depth;
        nextLayer.addAll(t.edges);
      }
      todo = nextLayer;
      depth++;
    }
  }

  private static Tree buildTree(List<Integer>values, List<Integer> colors,
                         List<Integer> edgePairs) {
   TreeMap<Integer, Tree> nodes = new TreeMap<>();
   for (int i = 0; i < values.size(); i++) {
     int id = i + 1;
     Tree node = new Tree(id, values.get(i), colors.get(i) & 1);
     nodes.put(id, node);
   }
   Tree root = nodes.get(ROOT_ID);
   for (int j = 0; j < edgePairs.size() - 1; j += 2) {
     int from = edgePairs.get(j);
     int to = edgePairs.get(j+1);
     Tree parent = nodes.get(from);
     Tree child = nodes.get(to);
     parent.edges.add(child);
   }
   setDepths(root);
   return root;
  }

  private static List<Integer> scan( Scanner sc, int count) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      if (sc.hasNext()) result.add(sc.nextInt());
      else break;
    }
    return result;
  }
  private static Tree buildTree(InputStream in) {
    Scanner sc = new Scanner(in);
    int n = sc.nextInt();
    List<Integer> vals = scan(sc, n);
    List<Integer> cols = scan(sc, n);
    List<Integer> edgePairs = new ArrayList<>();
    while (sc.hasNext()) {
      edgePairs.addAll(scan(sc, 2));
    }
    return buildTree(vals, cols, edgePairs);
  }

  public static void main(String[] args) {
    /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
    Tree root = buildTree(System.in);
    SumInLeavesVisitor v1 = new SumInLeavesVisitor();
    log("%s", v1.getResult());
    ProductRedNodesVisitor v2 = new ProductRedNodesVisitor();
    v2.visitNode(root);
    log("%s", v2.getResult());
    FancyVisitor v3 = new FancyVisitor();
    v3.visitNode(root);
    log("%s", v3.getResult());
  }

  // -------------- TEST BELOW -------

  @Test
  public void testBuild() {
    String input = "5\n" +
        "4 7 2 5 12\n" +
        "0 1 0 0 1\n" +
        "1 2\n" +
        "1 3\n" +
        "3 4\n" +
        "3 5";
    Tree root = buildTree(new ByteArrayInputStream(input.getBytes()));
    log("Tree is %s", showTree(root));
    SumInLeavesVisitor v1 = new SumInLeavesVisitor();
    v1.visitNode(root);
    log("SumInLeaves value is %s", v1.getResult());
    ProductRedNodesVisitor v2 = new ProductRedNodesVisitor();
    v2.visitNode(root);
    log("ProductRedNodes value is %s", v2.getResult());
    FancyVisitor v3 = new FancyVisitor();
    v3.visitNode(root);
    log("Fancy value is %s", v3.getResult());

  }

}
