package com.bonfonte.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.junit.Test;

public class Islands {

	/*
	 * To execute Java, please define "static void main" on a class named Solution.
	 *
	 * If you need more classes, simply define them inline.
	 */
	static boolean DEBUG = false;
	
	public static void main(String[] args) {
	}

	static void title(String s) {
		String line = s.replaceAll(".", "=");
		log(line);
		log(s);
		log(line);
	}
	static void log(String s) {
		System.out.println(s);
	}

	static void dbg(String s) {
		if(DEBUG) System.out.println(s);
	}

	static class Cell {

		/*
		 * (
		 * 
		 * C11 <-> C12 ^ ^ v v C21 <-> C22
		 * 
		 * 
		 */

		int row, col;
		int elv;
		Cell n, s, e, w;
		boolean visited;

		public Cell(int row, int col, int elv) {
			this.row = row;
			this.col = col;
			this.elv = elv;
		}

		public void clear() {
			visited = false;
		}
		
		public String toString() {
			return "(" + row + "," + col + ")";
		}
	}

	static class Graph {
		
		List<Cell> cells = new ArrayList<>(); // Layout grid
		List<Island> islands = new ArrayList<>();

		public Graph(int[][] grid) {
			buildCells(grid);
			buildIslands();
		}

		public void buildCells(int[][] grid) {
			List<Cell> above = new ArrayList<>();

			for (int row = 0; row < grid.length; row++) {
				Cell left = null;

				List<Cell> curr = new ArrayList<>();
				for (int col = 0; col < grid[row].length; col++) {
					int elv = grid[row][col];
					Cell cell = null;
					if (elv > 0) {
						// Ok lets build this cell
						cell = new Cell(row, col, elv);
						cells.add(cell);
						if (above.size() > col && above.get(col) != null) {
							cell.n = above.get(col);
							above.get(col).s = cell;
						}
						if (left != null) {
							cell.w = left;
							left.e = cell;
						}
					}
					left = cell;
					curr.add(cell);
				}
				// Now make my line above the current line
				above = curr;
			}

		}

		public void clear() {
			for (Cell c : cells)
				c.clear();

		}

		public void walk(Cell cell, Consumer<Cell> func) {
			if (cell == null || cell.visited)
				return;
			cell.visited = true;
			func.accept(cell);
			walk(cell.n, func);
			walk(cell.s, func);
			walk(cell.e, func);
			walk(cell.w, func);
		}	
		
		public void buildIslands() {
			clear();
			for (Cell cell : cells) {
				if (cell.visited)
					continue;
				Island isle = new Island();
				walk(cell, isle::add);
				islands.add(isle);
			}
		}

	}
	
	static class Island{
		List<Cell> cells = new ArrayList<>();
		public Island add(Cell c) {
			this.cells.add(c);
			return this;
		}
		
		public double averageHeight() {
			double sumHeight = cells.stream().mapToInt(c -> c.elv).sum() + 0.0;
			return sumHeight / cells.size();
		}
		
		public String toString() {
			String res = "Island: Area=" + cells.size() + "\nAvgHeight=" + averageHeight();
			for(int i = 0; i < cells.size(); i++) {
				if(i % 8 == 0) res += "\n  ";
				res += cells.get(i).toString();
			}
			return res;
		}
	}

	
	static Island findBiggestIslandV1(int[][] grid) {
		Graph g = new Graph(grid);
		int maxSize = 0;
		Island isle = null;
		for(Island i : g.islands) {
			if(i.cells.size() > maxSize) {
				isle = i;
				maxSize = isle.cells.size();
			}
		}
		if(isle == null) { 
			dbg("No island was found!!");
		}else
			log("Here is the biggest island v1:" + isle);
		return isle;
	}
	
	static String format(int j, int width) {
		String res = "" + j;
		while(res.length() < width) res = " " + res;
		return res;
	}

	static Island findBiggestIslandV2(int[][] grid) {
		Region g = new Region(grid);
		int maxSize = 0;
		Island isle = null;
		for(Island i : g.islands) {
			if(i.cells.size() > maxSize) {
				isle = i;
				maxSize = isle.cells.size();
			}
		}
		if(isle == null) { 
			dbg("No island was found!!");
		}else
			log("Here is the biggest island v2:" + isle);
		return isle;
	}

	
	//// HERE IS TAKE TWO... can we do it more simply?
	// I think so .. but only by using a grid lookup or hash...
	
	static int[][] CONNECTED = { 
			            { -1, 0 }, 
			{ 0,  -1 }, /* 0, 0 */ {  0, 1 },
			            { 1,  0 } };

	static class Region{
		List<Island> islands = new ArrayList<>();
		List<Cell> cells = new ArrayList<>();
		Cell[][] grid;
		int maxCol = 0;
		int maxRow = 0;
		
		public Region(int[][] arr) {
			maxRow = arr.length;
			for(int row = 0; row < arr.length; row++) {
				if(arr[row].length > maxCol) maxCol = arr[row].length;
			}
			grid = new Cell[maxRow][maxCol];
			
			for(int row = 0; row < arr.length; row++) {
				for( int col = 0; col < arr[row].length; col++) {
					int elv = arr[row][col];
					if(elv > 0) {
						Cell cell = new Cell(row, col, elv);
						cells.add(cell);
						grid[row][col] = cell;
					}
				}
			}
			buildIsles();
		}
		
		public void clear() {
			for (Cell c : cells) c.clear();
		}

		private void buildIsles() {
			clear();
			for(Cell cell : cells) {
				if(cell.visited) continue;
				cell.visited = true;
				Island isle = new Island();
				dbg("\n=========> Adding new island v2 ====> " + cell);
				islands.add(isle);

				Stack<Cell> processed = new Stack<>();
				processed.add(cell);
				while(processed.size() > 0) {
					cell = processed.pop();
					isle.add(cell);
					dbg(" ... added cell " + cell);
					List<Cell> connected = findConnected(cell);
					for(Cell c : connected) {
						if(c.visited) continue;
						c.visited = true;
						processed.push(c);
					}
				}
				dbg("Constructed v2 island:" + isle);
			}
		}
		
		private List<Cell> findConnected(Cell cell){
			List<Cell> nbrs = new ArrayList<>();
			for(int[] nbr : CONNECTED) {
				int row = cell.row + nbr[0];
				int col = cell.col + nbr[1];
				if(0 <= row && row < maxRow && 0 <= col && col < maxCol) {
					Cell c = grid[row][col];
					if(c != null) {
						nbrs.add(c);
					}
				}
			}
			return nbrs;
		}
	}
	
	public static class TestIslands{
				
		private void runTest(int[][] arr) {
			title("Running test on this data:");
			for(int[] row : arr) {
				String data = "[";
				for(int col : row) {
					data += format(col, 3);
				}
				data += "  ]";
				log(data);
			}
			title("V1 call");
			findBiggestIslandV1(arr);
			title("V2 call");
			findBiggestIslandV2(arr);
		}
		
		@Test
		public void test1() {
			int arr[][] = {
					{ 1, 0, 0, 0 },
					{ 0, 1, 3 },
					{ 0, 2, 0, 1 }};
			runTest(arr);
		}

		@Test
		public void test2() {
			int arr[][] = {
					{ 1 },
					{ 1, 0, 0, 0 },
					{ 0, 1, 3 },
					{ 0, 2, 0, 1 },
					{ 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 0, 7 },
					{ 0, 1, 0, 1 },
					{ 0, 1, 2, 1, 3, 1, 0, 0, 2, 1 },
					{ 0, 0, 2, 1, 1, 1, 0, 0, 1, 0, 1 },
					{ 0, 0, 2, 4, 1, 1, 0, 0, 2, 1 },
					{ 0, 0, 1, 4, 3, 0, 0, 0, 0, 1 },
					{ 1, 1, 0, 1, 2, 1, 0, 0, 2, 1 },
					{ 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 3 },
					{ 1, 0, 0, 0, 0, 1, 1, 2, 1, 1 },
					{ 1, 2, 0, 0, 0, 1, 0, 0, 0, 2 },
					{ 1, 3, 1, 2, 0, 0, 0, 2, 1, 1 }
					};
			runTest(arr);
		}

	}

}

/*
 * Your previous C++ content is preserved below:
 * 
 * Hello Andrew.
 */

/*
 * Find the highest point? Average elevation of largest [ // Above..... [0, 1,
 * 3, 1, 0, ...], // Multiple islands [0, 1, 0, 0, 0, ...], [0, 0, 0, ...], [1],
 * [0, 44, 0, 0, ...], ... ]
 */
