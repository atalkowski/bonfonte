package com.bonfonte.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GraphInterfaceTest {

  @Test
  public void testBasicArrayGraph() {
    /* Set up a graph like this:
       0 ---- 1 ---- 2 ---- 4
        `-3--'
     */
    ArrayGraph g = new ArrayGraph(5);
    g.addEdge(0, 1);
    g.addEdge(1, 2);
    g.addEdge(0, 3);
    g.addEdge(1, 3);
    g.addEdge(2, 4);
    Assert.assertEquals(5, g.getNEdges());
    List<Integer> adjs0 = g.getAdjList(0);
    List<Integer> adjs1 = g.getAdjList(1);
    List<Integer> adjs2 = g.getAdjList(2);
    List<Integer> adjs3 = g.getAdjList(3);
    List<Integer> adjs4 = g.getAdjList(4);
    Assert.assertEquals(2, adjs0.size());
    Assert.assertEquals(3, adjs1.size());
    Assert.assertEquals(2, adjs2.size());
    Assert.assertEquals(2, adjs3.size());
    Assert.assertEquals(1, adjs4.size());
    Assert.assertEquals( 0, g.getAdjList(5).size()); // No such vertex in the graph

    // Check node 0:
    Assert.assertTrue(adjs0.contains(1));
    Assert.assertTrue(adjs0.contains(3));
    // Check node 1:
    Assert.assertTrue(adjs1.contains(0));
    Assert.assertTrue(adjs1.contains(3));
    checkListMatch(adjs1, 0, 2, 3);
    checkListMatch(adjs2, 1, 4);

  }

  @Test
  public void testBasicDynamicGraph() {
    /* Set up a graph like this:
       0 ---- 1 ---- 2 ---- 4
        `-3--'
     */
    DynamicGraph g = new DynamicGraph(5);
    checkGraph5(g);
  }

  private void checkGraph5(GraphInterface g) {
    g.addEdge(0, 1);
    g.addEdge(1, 2);
    g.addEdge(0, 3);
    g.addEdge(1, 3);
    g.addEdge(2, 4);
    Assert.assertEquals(5, g.getNVertices());
    Assert.assertEquals(5, g.getNEdges());
    List<Integer> adjs0 = g.getAdjList(0);
    List<Integer> adjs1 = g.getAdjList(1);
    List<Integer> adjs2 = g.getAdjList(2);
    List<Integer> adjs3 = g.getAdjList(3);
    List<Integer> adjs4 = g.getAdjList(4);
    Assert.assertEquals(2, adjs0.size());
    Assert.assertEquals(3, adjs1.size());
    Assert.assertEquals(2, adjs2.size());
    Assert.assertEquals(2, adjs3.size());
    Assert.assertEquals(1, adjs4.size());
    Assert.assertEquals( 0, g.getAdjList(5).size()); // No such vertex in the graph

    // Check node 0:
    Assert.assertTrue(adjs0.contains(1));
    Assert.assertTrue(adjs0.contains(3));
    // Check node 1:
    Assert.assertTrue(adjs1.contains(0));
    Assert.assertTrue(adjs1.contains(3));
    checkListMatch(adjs1, 0, 2, 3);
    checkListMatch(adjs2, 1, 4);
  }


  private void checkListMatch(List<Integer> list, Integer... values) {
    Assert.assertNotNull(list);
    Assert.assertEquals(list.size(), values.length);
    for (int index = 0; index < values.length; index++) {
      Assert.assertEquals(list.get(index), values[index]);
    }
  }

  private static int A[][] = new int[][] {
      { 1, 0, 0, 1 },
      { 1, 1, 0, 1 },
      { 0, 1, 1, 1 },
      { 1, 1, 0, 1 },
      { 1, 0, 1, 1 }
  };

  private static int COUNT1[][] = new int[][] {
      { 1, 2, 1, 1 },
      { 2, 2, 3, 2 },
      { 3, 3, 2, 3 },
      { 2, 2, 4, 2 },
      { 1, 3, 1, 2 }
  };

  private static int COUNT[][] = new int[][] {
      { 2, 3, 3, 2 },
      { 3, 4, 4, 3 },
      { 3, 4, 4, 3 },
      { 3, 4, 4, 3 },
      { 2, 3, 3, 2 }
  };

  @Test
  public void neightborCounts() {
    for (int R = 0; R < A.length; R++) {
      for (int C = 0; C < A[R].length; C++) {
        GeekShortestPath.Cell cell = new GeekShortestPath.Cell(R, C);
        List<GeekShortestPath.Cell> cells = cell.getAdjacent(A.length, A[0].length);
        int expected = COUNT[R][C];
        Assert.assertTrue(expected == cells.size());
        long expected1 = 0L + COUNT1[R][C];
        long actual = cells.stream().filter(f -> A[f.row][f.col] == 1).count();
        System.out.println(String.format("Expected %s at %s,%s and got %s", expected1, R, C, actual));
        Assert.assertTrue(expected1 == actual);
      }
    }
  }

  @Test
  public void testShortestPath1() {
    GeekShortestPath tester = new GeekShortestPath();
    int path1 = tester.shortestDistance(5, 4, A, 1, 1);
    Assert.assertTrue(2 == path1);
  }

  @Test
  public void testShortestPath2() {
    GeekShortestPath tester = new GeekShortestPath();
    int pathsize = tester.shortestDistance(5, 4, A, 4, 2);
    Assert.assertTrue(8 == pathsize);
  }


}
