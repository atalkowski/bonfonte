package com.bonfonte.graph;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Test;

import junit.framework.Assert;

public class Connected {

	static final boolean DEBUG = true;
	static void log(String s) {
		System.out.println(s);
	}

	static int[][] NEIGHBORS = { 
			{ -1, -1 }, { -1, 0 }, { -1, 1 },
			{ 0,  -1 }, /* 0, 0 */ {  0, 1 },
			{ 1,  -1 }, { 1,  0 }, {  1, 1 }};
	
	private static String getKey(long row, long col) {
		if(row < 0 || col < 0) return "";
		return row + "/" + col;
	}
	
	public static String getCellKey(Cell cell) {
		return getKey(cell.row, cell.col);
	}
	
	static class Graph{
		int[][] grid;
		int rows; 
		int cols;
		List<Cell> cells;
		Map<String,Cell> map = new HashMap<>();
		
		public Graph(int[][] grid) {
			this.grid = grid;
			this.rows = grid.length;
			if(rows > 0) {
				this.cols = grid[0].length;
			}
			buildCells();
		}
		
		private void mapCell(Cell cell) {
			map.put(cell.getKey(), cell);
		}
		
		private Cell getCell(int row, int col) {
			return map.get(getKey(row, col));
		}
		
		private void buildCells() {
			cells = new ArrayList<>();
			map = new HashMap<>();
			for(int row = 0; row < grid.length; row++) {
				for(int col = 0; col < grid[row].length; col++) {
					if(grid[row][col] != 0) {
						Cell cell = new Cell(row, col);
						mapCell(cell);
						cells.add(cell);
					}
				}
			}
			
		}
		
		private List<Cell> getNbrs(Cell cell){
			List<Cell> nbrs = new ArrayList<>();
			for(int[] pair : NEIGHBORS) {
				Cell nbr = getCell(cell.row + pair[0], cell.col + pair[1]);
				if(nbr != null) {
					nbrs.add(nbr);
				}
			}
			return nbrs;
		}
		
		private Stream<Cell> getUnvisitedNbrs(Cell cell){
			return getNbrs(cell).stream().filter(c -> c.visited == false);
		}
		
		public void walk(Cell cell, Consumer<Cell> whatToDo) {
			if(cell.visited) return;
			Stack<Cell> cells = new Stack<>();
			cells.add(cell);
			while(!cells.isEmpty()) {
				Cell c = cells.pop();
				if(c.visited) continue;
				whatToDo.accept(c);
				c.visited = true;
				getUnvisitedNbrs(c).forEach(cells::push);
			}			
		}
		
		public void clear() {
			for(Cell c : cells) {
				c.clear();
			}
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder("G " + rows + " x " + cols + ":" );
			for(int row = 0; row < rows; row++) {
				sb.append("\n");
				for(int col = 0; col < cols; col++) {
					Cell c = getCell(row, col);
					sb.append(c == null ? "0" : "1");
				}
			}
			return sb.toString();
		}
	}
	
	static class Cell{
		int row;
		int col;
		boolean visited;
		public Cell(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
		public String getKey() {
			return getCellKey(this);
		}
		
		public void clear() {
			this.visited = false;
		}
		
		public String toString() {
			return "[" + row + "/" + col + "]";
		}
	}
	
	static class IntCounter{
		int count = 0;
		
		public void increment() {
			count++;
		}
	}
	
	static int maxRegion(int[][] grid) {
		Graph g = new Graph(grid);
		int max = 0;
		g.clear();
		if(DEBUG) log("" + g);
		
		for(Cell cell : g.cells) {
			if(cell.visited) continue;
			IntCounter region = new IntCounter();
			g.walk(cell, c -> region.increment());
			if(DEBUG) log("Got count as " + region.count + " for cell " + cell);
			if(region.count > max) max = region.count;
		}
		return max;
	}

	public static int runTest(String input){
//		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		Scanner scanner = new Scanner(stream);

		int n = scanner.nextInt();
		scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

		int m = scanner.nextInt();
		scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

		int[][] grid = new int[n][m];

		for (int i = 0; i < n; i++) {
			String[] gridRowItems = scanner.nextLine().split(" ");
			scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

			for (int j = 0; j < m; j++) {
				int gridItem = Integer.parseInt(gridRowItems[j]);
				grid[i][j] = gridItem;
			}
		}

		int res = maxRegion(grid);
		log("Max region is " + res);
		scanner.close();
		return res;
	}

	
	public static class TestConnected{

		@Test
		public void test1() {
			String input = "4\n" + 
					"4\n" + 
					"1 1 0 0\n" + 
					"0 1 1 0\n" + 
					"0 0 1 0\n" + 
					"1 0 0 0";
			
			Assert.assertEquals(5, runTest(input));
		}

		@Test
		public void test2() {
			String input = "5\n" + 
					"5\n" + 
					"1 0 1 1 0\n" + 
					"1 1 0 0 1\n" + 
					"0 1 1 1 0\n" + 
					"0 0 0 0 1\n" + 
					"1 1 1 0 0";
			
			Assert.assertEquals(10, runTest(input));
		}
	}
}
