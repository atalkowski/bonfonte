package com.bonfonte.wealth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.Test;

public class Frogger extends FrogTest {
    static final double MIN_ODDS_PER_CELL = 0.0000005;
    
	static void log(String s) {
		System.out.println(s);
	}
	
	static void box(String s) {
		String line = s.replaceAll(".", "*") + "****";
		log(line + "\n* " + s + " *\n" + line);
	}
	
	enum Type{
		BRICK('#'), /*TUNNEL('T'), */ FREE('O'), BOMB('*'), EXIT('%');
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
		int tno;
		double previousOdds;
		double odds;
		boolean visited;
		List<Cell> reachable;
		List<Cell> visitable;
		List<Cell> adjacent;
		
		public Cell(Type type, int row, int col) {
			this.type = type;
			this.row = row;
			this.col = col;
		}
		
		public void clear() {
			odds = 0.0;
			previousOdds = 0.0;
			visited = false;
		}
		
		public List<Cell> getAdjacent(){
			if(adjacent == null) {	
				List<Cell> res = new ArrayList<>();
				if(n != null) res.add(dropThru(n));
				if(s != null) res.add(dropThru(s));
				if(e != null) res.add(dropThru(e));
				if(w != null) res.add(dropThru(w));
				adjacent = res;
			}
			return adjacent;
		}
		
		private Cell dropThru(Cell c) {
			return c.tunnel == null ? c : c.tunnel;
		}

		public List<Cell> getReachable(){
			if(reachable == null) {
				reachable = getAdjacent().stream()
					.filter(Objects::nonNull)
					.filter(cell -> cell.type != Type.BRICK)
					.collect(Collectors.toList());
			}
			return reachable;
		}
		
		public List<Cell> getVisitable(){
			if(visitable == null) {
				visitable = getReachable().stream()
						.filter(cell -> !cell.visited)
						.collect(Collectors.toList());
			}
			return visitable;
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
		int tunnelNo;
		double minOddsPerCell;
		
		public Graph(int rows, int cols, int tuns) {
			this.rows = rows;
			this.cols = cols;
			this.tuns = tuns;
			if(rows <= 0 || cols <= 0) throw new RuntimeException("Illegal row x col size " + rows + "x" + cols);
			grid = new Cell[rows][cols];
			minOddsPerCell = MIN_ODDS_PER_CELL / (rows * cols * 1.0);
		}
		
		public void addTunnel(int row1, int col1, int row2, int col2) {
			Cell from = getTunnelCell(row1, col1);
			Cell to = getTunnelCell(row2, col2);
			if(to == from) {
				throw new RuntimeException("Tunnel cannot start and end at same place:" + to);
			}
			from.tunnel = to;
			to.tunnel = from;
			tunnelNo++;
			from.tno = tunnelNo;
			to.tno = tunnelNo;
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
			
		}
		
		public void locateTerminators() {
			for(Cell cell : cells) {
				switch(cell.type) {
				case BOMB: case EXIT: terminators.add(cell); continue;
				case FREE:
					List<Cell> reachable = cell.getReachable();
					if(reachable.size() == 0 || (reachable.size() == 1 && reachable.get(0).tunnel != null)) {
						terminators.add(cell); // A tunnel ending where there is only one way in
						// TODO ... what about if this tunnel cell is the origin?
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
					if(cell.tno > 0) {
						sb.append(cell == origin ? "A" : "{").append(cell.tno).append("}");
					}else {
						sb.append(" ").append(cell == origin ? 'A' : cell.type.ch);
						sb.append(" ");
					}
					sb.append(format(cell.odds, 7));
					sb.append(" |");
				}
			}
			addPadLine(sb, 8);
			return sb.toString();
		}
	}

	public static double computeOddsOfEscape(Graph g) {
		Cell start = g.origin;
		if(start == null || g.exits.isEmpty()) {
			return 0.0; 
		}
		switch(start.type) {
		case BOMB: return 0.0;
		case EXIT: return 1.0;
		case FREE: break;
		default:
		case BRICK: return 0.0;
		}
		List<Cell> cells = Arrays.asList(start);
		start.previousOdds = 1.0;
		start.odds = 1.0;
		int step = 0;
		while(!cells.isEmpty()) {
			List<Cell> next = new ArrayList<>();
			step++;
			log("Process STEP " + step + " for " + cells.size() + " cells .....");
			List<Cell> process = new ArrayList<>();
			// Compute all new odds for the various routes to free cells:
			for(Cell cell : cells) {
//				if(cell.visited) continue;
//				cell.visited = true;
				if(cell.previousOdds <= g.minOddsPerCell) {
					log("Excluding cell now as its odds are too small " + cell);
					continue;
				}
				process.add(cell);
				List<Cell> reachables = cell.getReachable();
				if(reachables.isEmpty()) {
					log("Excluding cell because it is a dead end " + cell);
					cell.odds = cell.previousOdds; // Copy the only value that was ever seeded.
					continue;
				}
				
				double odds = cell.previousOdds/reachables.size();
				log("Processing cell " + cell + " prevOdds " + cell.previousOdds + " for " + reachables.size() + " reachables: "
						+ "giving new odds " + odds);

				for(Cell reachable : reachables) {
					switch(reachable.type){
					case EXIT:
					case BOMB:
						// Defer changing odds until all new odds are calculated:
						break;
					case FREE:
						// if(reachable.visited) continue;
						reachable.odds += odds;
						break;
					default:
						// Anything else should not be reachable
						throw new RuntimeException("This cell is not reachable - " + cell); 
					}
				}
			}
			for(Cell cell : process) {
				List<Cell> reachables = cell.getReachable();
				if(reachables.isEmpty()) continue;
				double odds = cell.odds/reachables.size();
				if(odds > 1.0) {
					throw new RuntimeException("Odds now exceed 1.0 --> bug!!");
				}
				for(Cell reachable : reachables) {
					switch(reachable.type){
					case EXIT:
					case BOMB:
						reachable.odds += odds;
						break;
					case FREE:
						next.add(reachable);
					default:
						break;
					}
				}
			}
			// Now get ready for the final move to nest step:
			cells = next;
			for(Cell cell : cells) {
				cell.previousOdds = cell.odds;
			}
			for(Cell cell : cells) {
				cell.odds = 0.0;
			}
		}
		
		// At this point all odds of the exit points are correct relative to each other 
		// but they are not absolutely correct because that would typically require an infinite
		// process of adding the (ever diminishing) odds by retracing the visited cells.
		// As that is (even if approximated) too long winded - the theory is that we can get the 
		// absolute probability by ensuring all exit odds are rescaled so that the total probability is 1.0.
		double terminatorsProbality = g.terminators.stream()
				.mapToDouble(cell -> Double.valueOf(cell.type == Type.FREE ? cell.previousOdds :cell.odds))
				.sum();
		if(terminatorsProbality <= 0.00000001) return 0.0;
		double answer = 0.0;
		for(Cell cell : g.terminators) {
			switch(cell.type) {
			case EXIT:
				answer += cell.odds;
			case BOMB:
				cell.odds = cell.odds / terminatorsProbality;
				break;
			case FREE:
				cell.odds = cell.previousOdds / terminatorsProbality;
			default:
				break;
			}
		}
		log("Terminators total probability = " + terminatorsProbality + " and exit = " + answer);
		return answer/terminatorsProbality;
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
        graph.locateTerminators();
        scanner.close();
		//log("Got Graph:" + graph);
        return graph;
    }

	private static void solve() {
		Graph graph = buildGraph(System.in);
		log("" + computeOddsOfEscape(graph));
	}
	
	public double test(String input) {
		log("\n\n");
		log("================================");
		log("Starting new test for this input");
		log(input);
		log("================================");
		long t0 = System.currentTimeMillis();
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		Graph graph = buildGraph(stream);
		double escapeOdds = computeOddsOfEscape(graph);
		box("Odds of escaping are " + escapeOdds);
		log("Here is final graph with probs:");
		log(graph.displayProbabilityMatrix());
		long t1 = System.currentTimeMillis();
    	log("Time for Frogger 1: " + (t1 - t0) + "ms");
    	return escapeOdds;
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
	

	public static double runLive(String input) {
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		Graph graph = buildGraph(stream);
		int maxTest = 3000000;
		double maxTestD = maxTest + 0.0;
		Map<Type, Integer> counts = new TreeMap<>();
		for(int i = 0; i < maxTest; i++) {
			graph.clear();
			Type outcome = runTrial(graph);
			counts.put(outcome, counts.getOrDefault(outcome, 0) + 1);
		}
		double res = 0.0;
		for(Type t : counts.keySet()) {
			Integer count = counts.getOrDefault(t, 0);
			double d = (count + 0.0) / maxTestD;
			if(t == Type.EXIT) res = d;
			log("Prob " + t + " : " + d);
		}
		return res;
	}

	
    public static void main(String[] args) {
    	Frogger f = new Frogger();
//    	runLive(f.test0());
    	runLive(f.test1());
//    	runLive(f.test2());
//    	runLive(f.test3());
//	 	runLive(f.test4());
    }
    
    public static class TestFrog{
    	
    	private void runTest(String input) {
    		Frogger f = new Frogger();
    		double actual = f.test(input);
        	double sample = runLive(input);
        	double diff = actual - sample;
        	if(diff < 0.0) diff = -diff;
        	if(diff < 0.01) {
        		box("SUCCESS " + actual + " approx equals expected " + sample);
        	}else {
        		box("FAIL!!? " + actual + " does not equal expected " + sample);
        	}
    	}

    	
    	@Test
    	public void test0() {
    		runTest(TEST0);
    	}
    }
}
