package com.bonfonte.wealth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Frogger2 extends FrogTest{

	static void log(String s) {
		System.out.println(s);
	}
	
	static final double MIN_PROB = 0.0000000001;
	
	enum Type{
		BRICK('#'), FREE('O'), BOMB('*'), EXIT('%');
		char ch;
		
		private Type(char ch) {
			this.ch = ch;
		}
		
		public static Type fromCh(char ch) {
			for(Type t : values()) {
				if(t.ch == ch) {
					return t;
				}
			}
			return FREE;
		}
	}
	
	static class Cell{
		Cell n, s, e, w; // North south east west
		int row, col;
		Type type;
		Cell tunnel;
		double lastodds;
		double odds;
		boolean visited;
		
		public Cell(Type type, int row, int col) {
			this.type = type;
			this.row = row;
			this.col = col;
		}
		
		public void clear() {
			odds = 0.0;
			lastodds = 0.0;
			visited = false;
		}
		
		public List<Cell> getAdjacent(){
			List<Cell> res = new ArrayList<>();
			if(n != null) res.add(dropThru(n));
			if(s != null) res.add(dropThru(s));
			if(e != null) res.add(dropThru(e));
			if(w != null) res.add(dropThru(w));
			return res;
		}
		
		private Cell dropThru(Cell c) {
			return c.tunnel == null ? c : c.tunnel;
		}

		public List<Cell> getReachable(){
			return getAdjacent().stream()
					.filter(Objects::nonNull)
					.filter(cell -> cell.type != Type.BRICK)
					.collect(Collectors.toList());
		}
		
		public List<Cell> getVisitable(){
			return getReachable().stream()
					.filter(cell -> !cell.visited)
					.collect(Collectors.toList());
		}

		public String toString() {
			return type + " cell[" + row + "," + col + "]";
		}

		public String getDetail() {
			String res = toString();
			res += "\n t " + tunnel;
			res += "\n n " + n;
			res += "\n s " + s;
			res += "\n e " + e;
			res += "\n w " + w;
			return res;
		}
	}
	
	static class Graph{
		List<Cell> cells = new ArrayList<>();
		Cell[][] grid;
		Cell origin; // Where the frog starts!
		List<Cell> exits = new ArrayList<>(); // Where the exits are.
		List<Cell> terminators = new ArrayList<>();
		
		int rows, cols;
		int tuns;
		
		public Graph(int rows, int cols, int tuns) {
			this.rows = rows;
			this.cols = cols;
			this.tuns = tuns;
			if(rows <= 0 || cols <= 0) throw new RuntimeException("Illegal row x col size " + rows + "x" + cols);
			grid = new Cell[rows][cols];
		}
		
		public void addTunnel(int row1, int col1, int row2, int col2) {
			Cell from = getTunnelCell(row1, col1);
			Cell to = getTunnelCell(row2, col2);
			if(to == from) {
				throw new RuntimeException("Tunnel cannot start and end at same place:" + to);
			}
			from.tunnel = to;
			to.tunnel = from;
			
		}

		private Cell getCell(int row, int col) {
			if(row >= 0 && row < this.rows && col >= 0 && col < this.cols) {
				return grid[row][col];
			}
			throw new RuntimeException("Illegal row/col coordinate " + row + "/" + col);
		}

		private Cell getTunnelCell(int row, int col) {
			Cell cell = getCell(row, col);
			switch(cell.type) {
			case BRICK:
				throw new RuntimeException("Tunnel cannot start or end in a brick wall " + cell);
			default:
				return cell;
			}
		}
		
		public void parseLines(List<String> lines) {
			List<Cell> above = new ArrayList<>();
			for(int row=0; row < rows; row++) {
				String line = row < lines.size() ? lines.get(row) : "";  
				List<Cell> next = new ArrayList<>();
				Cell left = null;
				for(int col = 0; col < cols; col++) {
					char ch = col < line.length() ? line.charAt(col) : '#';
					Cell cell = new Cell(Type.fromCh(ch), row, col);
					if(ch == 'A') { // Frog Alex is here!!
						origin = cell;
					}
					if(cell.type == Type.EXIT) {
						exits.add(cell);
					}
					if(left != null) {
						cell.w = left;
						left.e = cell;
					}
					if(above.size() > col) {
						Cell north = above.get(col);
						cell.n = north;
						north.s = cell;
					}
					
					left = cell;
					next.add(cell);
					grid[row][col] = cell;
					cells.add(cell);
				}
				above = next;
			}
			for(Cell cell : cells) {
				switch(cell.type) {
				case BOMB: case EXIT: terminators.add(cell); continue;
				case FREE:
					List<Cell> reachable = cell.getReachable();
					if(reachable.size() == 0 || (reachable.size() == 1 && reachable.get(0).tunnel != null)) {
						terminators.add(cell); // A tunnel in a dead end
					}
					continue;
				default:
					continue;
				}
			}
		}
		
		public void clear() {
			for(Cell c : cells) {
				c.clear();
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int row = 0; row < rows; row++) {
				addPadLine(sb, 0);
				sb.append("\n|");
				for(int col = 0; col < cols; col++) {
					Cell cell = grid[row][col];
					sb.append(cell.tunnel == null ? " " : "{");
					sb.append(cell == origin ? 'A' : cell.type.ch);
					sb.append(cell.tunnel == null ? " " : "}");
					sb.append('|');
				}
			}
			addPadLine(sb, 0);
		
			return sb.toString();
		}
		
		private void addPadLine(StringBuilder sb, int size) {
			sb.append("\n|");
			String pad = "";
			for(int i = 0; i < size; i++) pad += "-";
			for(int col = 0; col < cols; col++) {
				sb.append(pad).append("---|");
			}
		}
		
		private String format(double d, int leng) {
			String s = d + "";
			while(s.length() < leng) s += "        ";
			return s.substring(0,7);
		}
		
		public String displayProbabilityMatrix() {
			StringBuilder sb = new StringBuilder();
			for(int row = 0; row < rows; row++) {
				addPadLine(sb, 8);
				sb.append("\n|");
				for(int col = 0; col < cols; col++) {
					Cell cell = grid[row][col];
					sb.append(cell.tunnel == null ? " " : "{");
					sb.append(cell == origin ? 'A' : cell.type.ch);
					sb.append(cell.tunnel == null ? " " : "}");
					sb.append(" ");
					sb.append(format(cell.odds, 7));
					sb.append('|');
				}
			}
			addPadLine(sb, 8);
			return sb.toString();
		}
	}

	public static Type runTrial(Graph g) {
		Cell cell = g.origin;
		if(cell == null || g.exits.isEmpty()) {
			return Type.FREE; 
		}
		for(;;) {
			switch(cell.type) {
			case BOMB: 
			case EXIT: 
			case BRICK: return cell.type;
			default:
			case FREE: 
				List<Cell> reachables = cell.getReachable();
				switch(reachables.size()) {
				case 0: return Type.FREE; // Lock in a tunnel probably
				case 1: cell = reachables.get(0);
					continue;
				default:
					cell = reachables.get(RANDOM.nextInt(reachables.size()));
					continue;
				}
			}
		}
	}
	
	public static double computeOddsOfEscape(Graph g) {
		Cell start = g.origin;
		if(start == null || g.exits.isEmpty()) {
			return 0.0; 
		}
		Queue<Cell> que = new LinkedList<>();
		que.add(start);
		start.odds = 1.0;
		start.lastodds = 1.0;
		
		switch(start.type) {
		case BOMB: return 0.0;
		case EXIT: return 1.0;
		case FREE: break;
		default:
		case BRICK: return 0.0;
		}

		while(!que.isEmpty()) {
			Cell cell = que.remove();
			//log("Examining cell " + cell.getDetail());
			if(cell.visited) continue;
			cell.visited = true;
			List<Cell> reachables = cell.getReachable();
			//log("Got visitable as:" + visitable);
			if(reachables.isEmpty()) continue;
			double odds = cell.lastodds/reachables.size();
			
			for(Cell reachable : reachables) {
				switch(reachable.type){
				case EXIT:
				case BOMB:
					reachable.odds += odds;
					break;
				case FREE:
					reachable.odds += odds;
					reachable.lastodds = odds;
					if(odds > MIN_PROB) {
						que.add(reachable);
					}
					break;
				default:
					// Anythng else should not be reachable
					throw new RuntimeException("This cell is not reachable - " + cell); 
				}
			}
		}
		
		// At this point all odds of the exit points are correct relative to each other 
		// but they are not absolutely correct because that would typically require an infinite
		// process of adding the (ever diminishing) odds by retracing the visited cells.
		// As that is (even if approximated) too long winded - the theory is that we can get the 
		// absolute probability by ensuring all exit odds are rescaled so that the total probability is 1.0.
		double totalTerminators = g.terminators.stream()
				.mapToDouble(cell ->Double.valueOf(cell.odds))
				.sum();
		if(totalTerminators <= 0.00000001) return 0.0;
		double answer = 0.0;
		for(Cell cell : g.terminators) {
			cell.odds = cell.odds / totalTerminators;
			if(cell.type == Type.EXIT) {
				answer += cell.odds;
			}
		}
		return answer;
	}
	
    private static Graph buildGraph(InputStream input) {
		Scanner scanner = new Scanner(input);
		String[] nmk = scanner.nextLine().split(" ");
	    int n = Integer.parseInt(nmk[0]);
	    int m = Integer.parseInt(nmk[1]);
	    int k = Integer.parseInt(nmk[2]);

	    Graph graph = new Graph(n, m, k);
	    List<String> lines = new ArrayList<>();
	    for (int nItr = 0; nItr < n; nItr++) {
	        String row = scanner.nextLine();
	        lines.add(row);
	        // Write Your Code Here
        }
	    graph.parseLines(lines);
        for (int kItr = 0; kItr < k; kItr++) {
        	String[] txt = scanner.nextLine().split(" ");
            int row1 = Integer.parseInt(txt[0]) -1;
            int col1 = Integer.parseInt(txt[1]) -1;
            int row2 = Integer.parseInt(txt[2]) -1;
            int col2 = Integer.parseInt(txt[3]) -1;
            graph.addTunnel(row1, col1, row2, col2);
           // Write Your Code Here
        }
        scanner.close();
		//log("Got Graph:" + graph);
        return graph;
    }

    private static void solve() {
		Graph graph = buildGraph(System.in);
		log("" + computeOddsOfEscape(graph));
	}
    
 
	public double test(String input) {
		long t0 = System.currentTimeMillis();
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		Graph graph = buildGraph(stream);
		double res = computeOddsOfEscape(graph);
		log("Odds of escaping are " + res);
		log("Here is final graph with probs:");
		log(graph.displayProbabilityMatrix());
    	long t1 = System.currentTimeMillis();
    	log("Time for Frogger 2: " + (t1 - t0) + "ms");
    	return res;
	}
	
	public static void runLive(String input) {
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		Graph graph = buildGraph(stream);
		int maxTest = 1000000;
		double maxTestD = maxTest + 0.0;
		Map<Type, Integer> counts = new TreeMap<>();
		for(int i = 0; i < maxTest; i++) {
			graph.clear();
			Type outcome = runTrial(graph);
			counts.put(outcome, counts.getOrDefault(outcome, 0) + 1);
		}
		for(Type t : counts.keySet()) {
			Integer count = counts.getOrDefault(t, 0);
			double d = (count + 0.0) / maxTestD;
			log("Prob " + t + " : " + d);
		}
	}
	
	
    public static void main(String[] args) {
//    	test1();
		Frogger2 f2 = new Frogger2();
    	runLive(f2.test4());
//    	solve();
    }
}
