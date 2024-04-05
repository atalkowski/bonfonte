package com.hack.bestpath;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BestPath1 {
	static void log(String s) {
		System.out.println( s );
	}
	
	static class Cell{
		int value;
		int x;
		int y;
		int visit;
		
		boolean goal = false;
		boolean origin = false;
		char ch; // One of 
		// NOrth, south east and west cells
		Cell n;
		Cell s;
		Cell e;
		Cell w;
		Cell right;
		Cell down;
		Cell(char ch, int value, int x, int y){
			this.value = value;
			this.ch = ch == 'X' || ch == '.' ? ch : 'X';
			this.x = x;
			this.y = y;
		}
		
		Cell getN() {
			return n;
		}

		Cell getS() {
			return s;
		}
		
		Cell getE() {
			return e;
		}

		Cell getW() {
			return w;
		}

		void bindWest(Cell o) {
			o.right = this;
			if(this.ch == 'X' || o.ch == 'X') return;
			o.e = this;
			this.w = o;
		}
		
		void bindNorth(Cell o) {
			o.down = this;
			if(this.ch == 'X' || o.ch == 'X') return;
			o.s = this;
			this.n = o;
		}
		
		@Override
		public String toString() {
			String type = origin ? "o" : goal ? "g" : ("" + ch);	
			return this.value + "[" + type + "]@(" + x + "," + y + ")" ;
		}
		
		public void walkBox(Consumer<Cell> func, Consumer<Cell> lineFunc) {
			Cell node = this;
			while(node != null) {
				lineFunc.accept(node);
				Cell cell = node;
				while(cell != null) {
					func.accept(cell);
					cell = cell.right;
				}
				node = node.down;
			}
		}
		
		public static List<Cell> getOkCellsFromDirection(Cell from, int visit, char direction, int goal){
			if(from.value == goal) {
				return new ArrayList<>();
			}
			Function<Cell, Cell> getNext;
			switch(direction) {
			case 'n': getNext = Cell::getN; break;
			case 's': getNext = Cell::getS; break;
			case 'e': getNext = Cell::getE; break;
			case 'w': getNext = Cell::getW; break;
			default : throw new RuntimeException( "Illegal direction");
			}
			Stack<Cell> cells = new Stack<>();
			while(true) {
				Cell next = getNext.apply(from);
				if(next == null || next.visit == visit){
					break;
				}
				cells.push(next);
				from = next;
			}
			List<Cell> result = new ArrayList<>();
			while(cells.size() > 0) {
				result.add(cells.pop());
			}
			return result;
		}
		
		public static List<Cell> getOkCellsWithin1Step(Cell from, int visit, int goal){
			List<Cell> result = new ArrayList<>();
			char[] types = { 'n', 's', 'e', 'w' };
			for(char ch : types) {
				result.addAll(getOkCellsFromDirection(from, visit, ch, goal));
			}
			return result;
		}
		
		public static LinkedList<Cell> createNew(LinkedList<Cell> cells, Cell cell){
			LinkedList<Cell> result = cells.stream()
					.collect(Collectors.toCollection(LinkedList::new));
			result.add(cell);
			return result;
		}
		
		public static List<LinkedList<Cell>> buildNewPaths(LinkedList<Cell> path, List<Cell>cells){
			List<LinkedList<Cell>> result = new ArrayList<>();
			for(Cell cell : cells) {
				result.add(createNew(path, cell));
			}
			return result;
		}
		
		public static List<LinkedList<Cell>> visitWalk(int visit, Cell from, Cell to) {
			List<LinkedList<Cell>> paths = new ArrayList<>();
			LinkedList<Cell> initialPath = new LinkedList<>();
			List<Cell> edges = new ArrayList<>();
			int goal = to.value;
			
			initialPath.add(from);
			edges.add(from);
			from.visit = visit;
			paths.add(initialPath);
			List<LinkedList<Cell>> finalPaths = new ArrayList<>();
			
			while(edges.size() > 0){
				edges = new ArrayList<>();
				List<LinkedList<Cell>> newPaths = new ArrayList<>();

				for(LinkedList<Cell> path : paths) {
					Cell edge = path.getLast();
					if(edge.equals(to)) {
						finalPaths.add(path);
						log("Found final path " + path);
						continue;
					}
					List<Cell> cells = getOkCellsWithin1Step(edge, visit, goal);
					edges.addAll(cells);
					newPaths.addAll(buildNewPaths(path, cells));
				}
				// Finally mark all those edges as visited
				edges.forEach(edge -> edge.visit = visit);
				paths = newPaths;
			}
			
			return finalPaths;
		}
	}	
	
	static class Graph{
		Cell root;
		Cell origin;
		Cell goal;
		Graph(Cell origin, Cell goal, Cell root){
			this.root = root;
			this.origin = origin;
			this.goal = goal;
		}
		
		@Override
		public String toString() {
			return "Graph : origin=" + origin + ", goal=" + goal + ", root=" + root;
		}
	}
	
	static Graph buildGraph(String[] grid, int startX, int startY, int goalX, int goalY) {
		List<List<Cell>> graph = new ArrayList<>();
		int y = 0;
		int value = 1;
		Cell root = null;
		Cell origin = null;
		Cell goal = null;
		List<Cell> above = new ArrayList<>();
		for(String s : grid) {
			log("Processing grid line y = " + y + " -> [" + s + "]");
			char[] chs = s.toCharArray();
			List<Cell> cells = new ArrayList<>();
			Cell prev = null;
			for(int x = 0; x < s.length(); x++) {
				Cell cell = new Cell(chs[x], value++, x, y);
				if(prev != null) {
					cell.bindWest(prev);
				}
				if(above.size() > 0) {
					cell.bindNorth(above.get(x));
				}
				prev = cell;
				if(x == 0 && y == 0) root = cell;
				if(x == startX && y == startY) {
					cell.origin = true;
					origin = cell;
				}
				if(x == goalX && y == goalY) {
					cell.goal = true;
					goal = cell;
				}
				cells.add(cell);
			}
			y++;
			graph.add(cells);
			above = cells;
		}
		final StringBuilder sb = new StringBuilder();
		root.walkBox(node -> sb.append(node).append(" | "), node -> sb.append("\n| "));
		log("Graph: " + sb.toString());
		return new Graph(origin, goal, root);
	}
	
	static List<Cell> compressList(LinkedList<Cell> list){
		List<Cell> result = list.stream().collect(Collectors.toList());
		return result;
	}
	
    // Complete the minimumMoves function below.
    static int minimumMoves(String[] grid, int startX, int startY, int goalX, int goalY) {
    	Graph graph = buildGraph(grid, startX, startY, goalX, goalY);
    	log("Got graph as " + graph);
    	List<LinkedList<Cell>> paths = Cell.visitWalk(1, graph.origin, graph.goal);
    	if(paths.size() == 0) {
    		log("Did not find any path!");
    		return 0;
    	}
    	int best = -1;
    	for(LinkedList<Cell> path : paths) {
    		List<Cell> cells = compressList(path);
    		if(best == -1 || best > cells.size()) best = cells.size();
    	}
    	best = best -1;
    	log("Minimum moves = " + best);
    	return best;
    }
    
	static final String sample1 = "3\n" + 
			".X.\n" + 
			".X.\n" + 
			"...\n" + 
			"0 0 2 0";

	static final String sample = "4\n" + 
			"....\n" + 
			".X..\n" + 
			"..X.\n" + 
			"X...\n" + 
			"2 1 1 3";

    private static Scanner scanner;
    
    static int find(int steps){
        if (steps <= 2) return steps;
        if (steps == 3) return 4;
        return find(steps - 1) + find(steps - 2) + find(steps - 3);
    }
    // Complete the stepPerms function below.
    static int stepPerms(int n) {
        return find(n);
    }
    
    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(
        		new FileWriter("/tmp/output.txt"));// System.getenv("OUTPUT_PATH")));
        scanner = new Scanner(new ByteArrayInputStream(sample.getBytes()));//System.in);

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        String[] grid = new String[n];

        for (int i = 0; i < n; i++) {
            String gridItem = scanner.nextLine();
            grid[i] = gridItem;
        }

        String[] startXStartY = scanner.nextLine().split(" ");

        int startX = Integer.parseInt(startXStartY[0]);

        int startY = Integer.parseInt(startXStartY[1]);

        int goalX = Integer.parseInt(startXStartY[2]);

        int goalY = Integer.parseInt(startXStartY[3]);

        int result = minimumMoves(grid, startX, startY, goalX, goalY);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
        
        for (int i = 1; i < 10; i++) {
        	log("Steps = " + i + " => " + find( i ));
        }
    }
}
