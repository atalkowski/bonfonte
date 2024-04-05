package com.bonfonte.graph;

import java.util.*;
import java.util.stream.Collectors;

public class GeekShortestPath {
  /*
  Example Input: N=3, M=4
   A=[[1,0,0,0],
      [1,1,0,1],
      [0,1,1,1]]
   X=2, Y=3
   Output: 5
   Explanation: The shortest path is as follows: (0,0)->(1,0)->(1,1)->(2,1)->(2,2)->(2,3).
    @param N the number of rows
    @param M number of columns
    @param A matrix fully populated with 1's and 0's
    @param X row of cell to be reached from (0,0) via shortest path
    @param Y col of cell to be reached from (0,0) via shortest path
    Result is the mimimum length of path from (0,0) to (X,Y)
  */
  int shortestDistance(int N, int M, int A[][], int X, int Y) {
    if (A[0][0] == 0 || A[X][Y] == 0) return -1;
    // Build a DynamicGraph? DynamicGraph g = new DynamicGraph();
    Cell origin = new Cell(0, 0);
    Cell target = new Cell(X, Y);
    if (target.equals(origin)) return 0;

    Stack<Cell> reachedSoFar = new Stack<>();
    Visited visited = new Visited();
    visited.mark(origin);
    int depth = 0;
    reachedSoFar.push(origin);
    while (reachedSoFar.size() > 0) {
      depth++;
      Stack<Cell> next = new Stack<>();
      while (reachedSoFar.size() > 0) {
        Cell cell = reachedSoFar.pop(); // We know this guy is not the same as target
        List<Cell> neighbors = findNewNeightbors(N, M, A, cell, visited);
        for (Cell neighbor : neighbors) {
          if (neighbor.equals(target)) {
            System.out.println(String.format("Found cell( %s, %s ) after %s edges", X, Y, depth));
            return depth;
          }
          next.push(neighbor);
        }
      }
      reachedSoFar = next;
    }
    System.out.println(String.format("Was not able to reach %s, %s after %s moves", X, Y, depth));
    return -1;
  }

  private static class Visited {
    private Map<Integer,Set<Integer>> visits = new HashMap<>();
    public boolean mark(int row, int col) {
      Set<Integer> set = visits.computeIfAbsent(row, notThere -> new HashSet<>());
      return set.add(col);
    }
    public boolean mark(Cell cell) {
      return mark(cell.row, cell.col); // Returns true if newly marked ... otherwise false.
    }
  }

  private List<Cell> findNewNeightbors(int N, int M, int A[][], Cell cell, Visited visited) {
    List<Cell> results = cell.getAdjacent(N, M);
    List<Cell> results2 = new ArrayList<>();
    for (Cell nbor : results) {
      if (visited.mark(nbor) && A[nbor.row][nbor.col] == 1) {
        results2.add(nbor);
      }
    }
    // System.out.println(String.format("Neighbors for %s are %s but valid unmarked are %s", cell, results, results2));
    return results2;
  }
  public static class Cell  {
    public int row;
    public int col;
    public Cell(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) return true;
      if (o instanceof Cell) {
        Cell other = (Cell)o;
        return this.row == other.row && this.col == other.col;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return (row << 8) + (col & 0xFF);
    }

    public List<Cell> getAdjacent(int N, int M) {
      List<Cell> result = new ArrayList<>();
      if (row > 0) {
        result.add(new Cell(row - 1, col));
      }
      if (row < N-1) {
        result.add(new Cell(row + 1, col));
      }
      if (col > 0) {
        result.add(new Cell(row, col - 1));
      }
      if (col < M-1) {
        result.add(new Cell(row, col + 1));
      }
      return result;
    }

    @Override
    public String toString() {
      return "(" + row + "," + col + ")";
    }
  }
}
