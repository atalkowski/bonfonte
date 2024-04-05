package com.bonfonte.wealth.partition;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Solution {

	
	/**
	 * After an initial gambit with this using the "lo" value as a means of pinpointing the "nearby" item
	 * to be amended or added to etc ... I realized this won't work. We have to search for the RANGE
	 * of items that are going to be affected. You cannot base your search on low and hope the tree 
	 * will work in your favor when you start splitting nodes. 
	 * 
	 * See this link 
	 * https://www.hackerrank.com/challenges/crush/problem?h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=arrays&h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
	 * 
	 * So the rewrite ... we need a search algorithm that locates the collection on nodes that are affected.
	 * to be updated.
	 * updating.
	 * @param s
	 */
	static void log(String s) {
		System.out.println(s);
	}
	
	static String a2s(int[] arr) {
		return "[" + Arrays.stream(arr).boxed().map(n -> n.toString()).collect(Collectors.joining(" "))
				+ "]";
	}

	static class Node{
		int lo;
		int hi;
		long total;
		Node left, right, parent;
		public Node(int lo, int hi, int value) {
			this.lo = lo;
			this.hi = hi;
			this.total = value;
		}
		
		// This is a top down search only:
		public Node findNearest(int key) {
			return findNearest(this, key);
		}
				
		public static Node findNearest(Node node, int key) {
			for(;;) {
				if(node.lo <= key && key <= node.hi) {
					return node;
				}
				if(key < node.lo) {
					if( node.left == null ) break;
					node = node.left;
				}else {
					if( node.right == null) break;
					node = node.right;
				}
			}
			return node;
		}
		
		public Node findFirst() {
			return findNearest(this, Integer.MIN_VALUE);
		}

		public Node findLast() {
			return findNearest(this, Integer.MAX_VALUE);
		}

		public void walk(Consumer<Node> func) {
			if(left != null) {
				left.walk(func);
			}
			func.accept(this);
			if(right!= null) {
				right.walk(func);
			}
		}
		
		public String display() {
			Visitor v = new Visitor();
			this.walk(v::capture);
			return v.text;
		}
		
		public static Node insert(Node root, int lo, int hi, int value) {
			log("insert [" + lo + "," + hi + "]=>" + value);
			Node node = new Node(lo, hi, value);
			if(root == null) {
				log("C1: root case");
				return node;
			}
			
			Node near = findNearest(root, lo);
			// Deal with easiest cases : 
			if(lo == near.lo && hi == near.hi) {
				near.total += value;
				log("C2: match case");
				return near;
			}

			if(hi < near.lo) {
				near.left = node;
				node.parent = near;
				log("C3: leftmost case");
				return node;
			}
			
			if(lo > near.hi) {
				near.right = node;
				node.parent = near;
				log("C4: rightmost case");
				return node;
			}

			// So we have some kind of partial overlap;
			// First deal with the lower part of any overlap (if any)
			if(lo < near.lo) {
				log("C5: split lo");
				insert(root, lo, near.lo-1, value);
				return insert(root, near.lo, hi, value);
			}
			
			// Now deal with any higher part of an overlap:
			if(hi > near.hi) {
				log("C6: split hi");
				int split = near.hi;
				insert(root, lo, split, value);
				log("C6 split hi part A did this:\n  " + root.display());
				// And deal with the overlap with near:
				
				node = insert(root, split + 1, hi, value);
				log("C6 split hi part B did this:\n  " + root.display());
				return node;
			}
			
			if(lo > near.lo) {
				log("C7: split inside lo");
				
				// We first have to split this node into two component parts : 
				int temp = near.hi;
				near.hi = lo - 1;
				log("C7 split existing now this\n:" + root.display());
		
				// Now add the original part of near as a new node
				node = insert(root, lo, temp, 0);
				node.total = near.total;
				// We can now try continuing to add this new data: 
				return insert(root, lo, temp, value);
			}
			
			// If we reach here the lo == near.lo:
			if(hi < near.hi) {
				log("C7: split inside hi");
				// Similar to above .. we need to split this near item up into two parts:
				int temp = near.hi;
				long total = near.total;
				near.hi = hi;
				near.total += value;
				node = insert(root, hi+1, temp, 0);
				node.total = total;
				return node;
			}
			
			log("Error ... unexpected insert ... failed to locate where to place " + node);
			return null;
		}
			
		
		@Override	
		public String toString() {
			return "[" + lo + ":" + hi +"]=" + total;
		}
	}
	
	static class Visitor{
		long max = 0L;
		String text = "";
		
		void visit(Node n) {
			if(n.total > max) max = n.total;
			log("Visited node " + n + ": max is " + max);
		}
		
		void logger(Node n) {
			log("Node " + n);
		}
		
		void capture(Node n) {
			text += n + ";";
		}

		void captureN(Node n) {
			text += "\n" + n;
		}
}
	
	static class Slow{
		List<Long> values = new ArrayList<>();
		
		public void add(int lo, int hi, int value) {
			while(values.size() <= hi) {
				values.add(0L);
			}
			for(int j = lo; j <= hi; j++) {
				values.set(j,  values.get(j) + value);
			}
		}
		
		@Override
		public String toString() {
			String res = "";
			int j = 1;
			int lastPos = values.size() - 1;
			while(j <= lastPos) {
				long curval = values.get(j);
				if(curval == 0) {
					j++;
					continue;
				}
				int i = j+1;
				while(i <= lastPos && values.get(i) == curval) i++;
				res += "[" + j + ":" + (i-1) + "]=" + curval + ";";
				j = i;
			}
			return res;
		}
	}
	
    // Complete the arrayManipulation function below.
    static long arrayManipulation(int n, int[][] queries) {
    	Node root = null;
    	Slow slow = new Slow();
    	
    	for(int[] line : queries) {
    		Visitor v = new Visitor();
    		log("Inserting range " + a2s(line));
    		slow.add(line[0], line[1], line[2]);
    		Node node = Node.insert(root, line[0], line[1], line[2]);
    		if(root == null) root = node;
        	root.walk(v::capture);
        	String text = slow.toString();
        	if(v.text.equals(text)) {
        		log("All OK so far: " + v.text);
        	}else {
        		log("Slow and node differ:\nNEW :" + v.text + " versus\nSLOW:" + text);
        	}
    	}
    	
    	Visitor maxFind = new Visitor();
    	root.walk(maxFind::visit);
    	return maxFind.max;
    }
    
    private static final String INPUT1 = "5 3\n" + 
    		"1 2 100\n" + 
    		"2 5 100\n" + 
    		"3 4 100";

    private static final String INPUT2 = "10 6\n" + 
    		"1 5 1000\n" + 
    		"3 7 100\n" + 
    		"1 10 1\n" + 
    		"2 2 4\n" + 
    		"5 9 50\n" + 
    		"3 7 100\n" + 
    		"4 8 100";
    
     // 1=1001 2=1005 3=1201 4=1301 5=1351 6=351 7=351 8=151 9=51 10=1
    

    
    private static final String INPUT = "40 30\n" + 
    		"29 40 787\n" + 
    		"9 26 219\n" + 	
    		"21 31 214\n" + 
    		"8 22 719\n" + 
    		"15 23 102\n" + 
    		"11 24 83\n" + 
    		"14 22 321\n" + 
    		"5 22 300\n" + 
    		"11 30 832\n" + 
    		"5 25 29\n" + 
    		"16 24 577\n" + 
    		"3 10 905\n" + 
    		"15 22 335\n" + 
    		"29 35 254\n" + 
    		"9 20 20\n" + 
    		"33 34 351\n" + 
    		"30 38 564\n" + 
    		"11 31 969\n" + 
    		"3 32 11\n" + 
    		"29 35 267\n" + 
    		"4 24 531\n" + 
    		"1 38 892\n" + 
    		"12 18 825\n" + 
    		"25 32 99\n" + 
    		"3 39 107\n" + 
    		"12 37 131\n" + 
    		"3 26 640\n" + 
    		"8 39 483\n" + 
    		"8 11 194\n" + 
    		"12 37 502";
    
    //private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/tmp/solveMax.txt"));
        InputStream inps = new ByteArrayInputStream(INPUT.getBytes());
        
        Scanner scanner = new Scanner(inps); //(System.in);
        
        String[] nm = scanner.nextLine().split(" ");

        int n = Integer.parseInt(nm[0]);

        int m = Integer.parseInt(nm[1]);

        int[][] queries = new int[m][3];

        for (int i = 0; i < m; i++) {
            String[] queriesRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int j = 0; j < 3; j++) {
                int queriesItem = Integer.parseInt(queriesRowItems[j]);
                queries[i][j] = queriesItem;
            }
        }

        long result = arrayManipulation(n, queries);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
