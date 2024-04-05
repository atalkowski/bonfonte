package com.hack.bestpath;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Solution {
	static BufferedWriter bufferedWriter;
    static void log(String s){
    	if(bufferedWriter != null) {
    		try {
    		   bufferedWriter.append(s);
    		   bufferedWriter.newLine();
    		}catch(IOException e) {
    			System.out.println("Failed to write to file");
    		}
    	}
        System.out.println(s);
    }
    
    static class Node{
        int id;
        int wgt = 6;
        int distance = -1;
        
        List<Node> kids = new ArrayList<>();
        List<Node> parents = new ArrayList<>();
        
        public Node(int id){
            this.id = id;
        }

        public void connect(Node link){
        	kids.add(link);
        	link.parents.add(this);
        }
        
        void calculate(int distance) {
        	this.distance = distance;
        	for(Node node : kids) {
        		if(node.distance < 0 || node.distance > distance + 1) node.calculate(distance + 1);
        	}
        	for(Node parent: parents){
        		if(parent.distance < 0 || parent.distance > distance + 1) parent.calculate(distance + 1);
        	}
        }
    }

    static String input1 = "2\n" + 
    		"4 2\n" + 
    		"1 2\n" + 
    		"1 3\n" + 
    		"1\n" + 
    		"3 1\n" + 
    		"2 3\n" + 
    		"2";
    
    static String input = "1\n" + 
    		"7 4\n" + 
    		"1 2\n" + 
    		"1 3\n" + 
    		"3 4\n" + 
    		"2 5\n" + 
    		"2";

    static Scanner scanner = //new Scanner(System.in);
    		new Scanner(new ByteArrayInputStream(input.getBytes()));

    static int nextInt() {
        int val = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        return val;
    }
    
    static class Query{
    	int nodeCount;
    	int edgeCount;
    	int root;
    	int[][] edges;
    	public Query() {
    	    nodeCount = nextInt(); 
            edgeCount = nextInt();
            edges = new int[edgeCount][];
            for(int i = 0; i < edgeCount; i++) {
            	int[] edge = new int[2];
            	edge[0] = nextInt();
            	edge[1] = nextInt();
            	edges[i] = edge;
            }
            root = nextInt();
    	}
    }

    static void solve(Query query) {
    	Map<Integer,Node> nodes = new TreeMap<>();
    	for(int[] edge : query.edges) {
    		
    		Node parent = nodes.computeIfAbsent(edge[0], Node::new); 
    		Node child = nodes.computeIfAbsent(edge[1], Node::new);
    		parent.connect(child);
    	}
      	Node root = nodes.get(query.root);
    	if(root == null) root = new Node(query.root);
    	root.calculate(0);
    	StringBuilder sb = new StringBuilder();
    	for(int i = 1; i <= query.nodeCount; i++) {
    		if( i == query.root) continue;
    		Node node = nodes.get(i);
    		if(sb.length() > 0) sb.append(" ");
    		if(node == null || node.distance < 0){
    			sb.append("-1");
    		}else {
    			sb.append(node.distance * 6);
    		}
    	}
    	log(sb.toString());
    }
    
    static void solve(List<Query> queries) {
    	for(Query query: queries) {
    		solve(query);
    	}
    }
    
    public static void main(String[] args) throws IOException {
        scanner.close();
        File initialFile = new File("/Users/andy/wspaces/data/dist9.txt");
        InputStream input = new FileInputStream(initialFile);
        
        scanner = new Scanner(input);
 
    	bufferedWriter = new BufferedWriter(
        		new FileWriter("/Users/andy/wspaces/data/res.txt"));

        int numberQueries = nextInt();
        List<Query>queries = new ArrayList<>( numberQueries);
        for(int i = 0; i < numberQueries; i++){
        	queries.add(new Query());
        }
        scanner.close();
        solve(queries);
		bufferedWriter.flush();
		bufferedWriter.close();

    }
}
