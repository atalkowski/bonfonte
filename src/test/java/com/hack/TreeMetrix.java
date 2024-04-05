package com.hack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TreeMetrix {
	static final String TEST = "7 3"
			+ "\n1 2\n1 3\n1 4\n3 5\n3 6\n3 7"
			+ "\n2\n2 4"
			+ "\n1\n5"
			+ "\n3\n2 4 5";
	static final long MAXVAL = 1000000007L;
	
	static Scanner scanner;
	
	static void log(String s) {
		System.out.println(s);
	}

	static Map<String, Integer> distances = new HashMap<>();
	
	static class Node{
		int value;
		Node parent;
		List<Node> kids = new ArrayList<>();
		Node(int value) {
			this.value = value;
		}

		Node(Node parent, int value) {
			this.parent = parent;
			this.value = value;
		}
		
		private String pad(int indent) {
			String result = "";
			String delim = " ";
			if(indent > 100) {
				indent = 100;
				delim = "-";
			}
			while(indent-- > 0) result += delim;
			return result;
		}
		
		private String show(int indent) {
			String result = pad(indent);
			result += "[" + value;
			if(kids.size() == 0) {
				result += "]";
			}else {
				result += ":";
				for(Node kid : kids) {
					result += "\n" + kid.show(indent+2);
				}
				result += "\n" + pad(indent) + "]";
			}
			return result;
		}
		
		public int countNode() {
			final int[] totals = new int[1];
			walkDepthFirst(node -> totals[0]++);
			return totals[0];
		}
		
		public void walkDepthFirst(Consumer<Node> todo) {
			todo.accept(this);
			for(Node kid : kids) {
				kid.walkDepthFirst(todo);
			}
		}

		@Override
		public String toString() {
			return "{" + value + "}";
		}
	}
	
	static void dbg(String s) {
		return;
	}
	
	static Node setRoot(Node root, String comment) {
		log("Setting root as " + root + " " + comment);
		return root;
	}

	static Node buildTree(int size, int[][] edges, int p, int c) {
		Map<Integer,Node>nodes = new HashMap<>();
		for(int[] edge : edges) {
			// log("Processing edge " + showArray(edge));
			Node parent = nodes.computeIfAbsent(edge[p], Node::new);
			Node child = nodes.computeIfAbsent(edge[c], Node::new);
			child.parent = parent;
			parent.kids.add(child);
		}
		List<Node> roots = nodes.values().stream()
				.filter(n -> n.parent == null)
				.collect(Collectors.toList());
		Node root = roots.get(0);
		int count = root.countNode();
		dbg("Tree:\n" + root.show(0));
		dbg("Created a total of " + nodes.size() + " nodes");
		dbg("Size of root = " + root.countNode());
		if(nodes.size() == count) {
			return root;
		}
		return null;
	}

	static Node buildTree(int size, int[][] edges) {
		Node root = buildTree(size, edges, 1, 0);
		if(root == null) root = buildTree(size, edges, 0, 1);
		if(root == null) {
			throw new RuntimeException("Cannot find a consistent tree");
		}
		return root;
	}

	
	static boolean findPath(LinkedList<Node> path, Node node, int v) {
		path.add(node);
		if(node.value == v) {
			return true;
		}
		for(Node kid : node.kids) {
			if(findPath(path, kid, v)) {;
				return true;
			}
		}
		path.removeLast();
		return false;
	}

	static LinkedList<Node> findPath(Node node, int v) {
		LinkedList<Node> result = new LinkedList<>();
		if(!findPath(result, node, v))
			throw new RuntimeException("Cannot locate node with value " + v);		
		dbg("Path to " + v + " is " + result);
		return result;		
	}
	
	static int computeDistance(Node root, int v1, int v2) {
		if(v1 == v2) {
			return 0;
		}
		if(v1 > v2) return computeDistance(root, v2, v1);
		String key = v1 + ":" + v2;
		Integer result = distances.get(key);
		if(result != null) return result;

		LinkedList<Node> v1Path = findPath(root, v1);
		LinkedList<Node> v2Path = findPath(root, v2);
		while(v1Path.size() > 0 && v2Path.size() > 0 && v1Path.getFirst() == v2Path.getFirst()) {
			v1Path.removeFirst();
			v2Path.removeFirst();
		}
		result = v1Path.size() + v2Path.size();
		distances.put(key, result);
		return result;
	}
	
	static class Permutation{
		int[] set;
		int lo = 0;
		int hi = 1;
		Permutation(int[] set){
			this.set = set;
		}
		
		boolean hasNext() {
			return(lo + 1 < set.length && hi < set.length);
		}
		
		int[] nextPair() {
			int[] pair = new int[2];
			pair[0] = set[lo];
			pair[1] = set[hi++];
			if(hi >= set.length){ 
				lo++;
				hi = lo + 1;
			}
			return pair;
		}
	}
	
	static int computeKatty(Node root, int[] set) {
		if(set.length <= 1) {
			return 0; 
		}
		Permutation permutation = new Permutation(set);
		long total = 0;
		while(permutation.hasNext()) {
			int[] pair = permutation.nextPair();
			long distance = computeDistance(root, pair[0], pair[1]);
			dbg("Computing distance for pair [" + pair[0] + "," + pair[1] + "] -> " + distance);
			if(distance == 0L) {
				continue;
			}
			total = total + distance * pair[0] * pair[1];
			if(total > MAXVAL) {
				total = total % MAXVAL;
			}
		}
		return (int)total;
	}
	
	/* Main solution */
	static Node solve(int n, int[][] edges, int[][] sets) {
		distances = new HashMap<>();
//		log("Solving");
//		log("Edges: " + showArray(edges));
//		log("Sets: " + showArray(sets));
		Node root = buildTree(n, edges);
		for(int[] set : sets) {
			//log("Processing set " + showArray(set));
			log(computeKatty(root, set) + "");
		}
		return root;
	}
	
	/* ******************* */
	
	static int read(String[] args, int argn) {
		return Integer.parseInt(args[argn]);
	}

	static void setScanner(InputStream input) {
		scanner = new Scanner(input == null ? System.in : input);
	}
	
	static int readInt(){
		int result = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        return result;
	}
	
	static int[][] readPairs(int size){
		int[][] result = new int[size][];
		for(int i = 0; i < size; i++) {
			int[] pair = new int[2];
			pair[0] = readInt();
			pair[1] = readInt();
			result[i] = pair;
		}
		return result;
	}
	
	static int[][] readSets(int size){
		int[][] result = new int[size][];
		for(int i = 0; i < size; i++) {
			int elements = readInt();
			int[] set = new int[elements];
			for(int j=0; j < elements; j++) set[j] = readInt();
			result[i] = set;
		}
		return result;
		
	}

	static String showArray(int[] array) {
		String out = "";
		for(int a : array){
			if(out.length() > 0) out += ", ";
			out += a;
		}
		return "[" + out + "]";
	}

//	static String showArray(int[][] array) {
//		String out = "";
//		for(int[] a : array){
//			if(out.length() > 0) out += ",\n ";
//			out += showArray(a);
//		}
//		return "Array size " + array.length + ":[" + out + "]";
//	}
	
	public static void readStream(InputStream input) {
		setScanner(input); //new ByteArrayInputStream(TEST.getBytes()));
		int nEdges = readInt();
		int nSets = readInt();
		
		int[][] edges = readPairs(nEdges - 1); // n-1 edges to read
		int[][] sets = readSets(nSets);
		
		Node root = solve(nEdges, edges, sets);
		log("Showing a DepthFirst traversal:");
		log(root.show(0));		
	}

	public static void readLocal() {
		File file = new File("/Users/andy/wspaces/data/test1.txt");
		try {
			InputStream input = new FileInputStream(file);
			readStream(input);
			input.close();
		}catch(Exception e) {
			log("Ooops .. failed to process file " + e.getMessage());
		}
	}

	public static void readTest() {
		InputStream input = new ByteArrayInputStream(TEST.getBytes());
		readStream(input);
	}

	public static void main(String[] args) {
		// readStream(System.in);
		// readTest();
		// readLocal();
		readTest();
	}
}
