package com.hack;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BalancedForest {
   static class Node{
        int value;
        int sum;
        Node parent;
        List<Node> kids = new ArrayList<>();
        public Node(int value){
            this.value = value;
        }
        void add(Node node){
            this.kids.add(node);
            node.parent = this;
        }
        
        public String display() {
        	return "Node: " + value + " kids: " + this.kids + " SUM=" + this.sum;
        }
        
        @Override
        public String toString() {
        	return "" + value;
        }
    }

    static int computeSums(Node node){
        node.sum = node.value;
        for(Node kid : node.kids){
            node.sum += computeSums(kid);
        }
        return node.sum; 
    }

    static Node buildTree(Node[] nodes, int[][] edges){
        for (int[] edge : edges) {
            Node parent = nodes[edge[0] - 1];
            Node child = nodes[edge[1] - 1];
            parent.add(child);
        }
        // Find root
        Node root = nodes[0];
        while (root.parent != null) {
            root = root.parent;
        }
        computeSums(root);
        return root;
    }

    static void log(String s){
        System.out.println( s );
    }

    static void title(String s){
        System.out.println( s.replaceAll(".", "-") );
        System.out.println( s );
        System.out.println( s.replaceAll(".", "-") );
    }

    static int getSolution(Node root, int inx){
    	title("Seeing if inx = " + inx + " is a solution");
        if(inx > root.kids.size()){
        	log("No! - inx too large or root empty");
        	return -1;
        }
        int cuts = 0; // We are assuming this sub tree is a candidate
                      // for the final set of trees and it is to be cut off.
                      // Its sum is the target we have to balance to. 
        int sumThis = root.kids.get(inx).sum;
        boolean usedRoot = false;
        int addedNodes = 0;
        int valueOfAddedNode = -1;
        int posOfAddedNode = -1;
        // For the other trees can we balance them?
        for(int pos = 0; pos < root.kids.size(); pos++){
        	if(pos == inx) {
        		cuts++;
        		continue;
        	}
        	log("Scanning node at pos " + pos);
            Node sub = root.kids.get(pos);
            if(sub.sum == sumThis) {
                cuts++;
                log("Looks like a match at pos " + pos + " for " + inx);
                continue; // Yes this one needs no change .. cut it from root
            }
            // Can we balance using the root as the added value?
            if(!usedRoot && sub.sum + root.value == sumThis){
            	log("Using root value as solution at pos " + pos + " for inx " + inx);
                usedRoot = true;
                continue; // 
            }
            // Can we add a node to square this one ?
            if(addedNodes == 0){
                valueOfAddedNode = sumThis - sub.sum; 
            	log("Using ability to add a node[=" + valueOfAddedNode + "] at pos " + pos 
            			+ " for inx = " + inx);
                addedNodes++;
                cuts++; // Assume we must cut this guy from the root.
                posOfAddedNode = pos;
                continue;
            }
            log("Can't balance to subtree " + inx);
            return -1;
        }
        if(addedNodes == 1) {
        	if(usedRoot) {
        		log("HIT! Inx = " + inx + " is a solution with cuts = " + cuts + " and added node = " 
        				+ valueOfAddedNode);
        		return valueOfAddedNode;
        	}
        	// We didn't add the root so the node we need to add would have 1 fewer cuts
        	// and we reduce the result by the root.valuet...
        	cuts--;
    		log("HIT! Inx = " + inx + " is a solution with root node in line; cuts = " + cuts + " and added node = " 
    				+ (valueOfAddedNode - root.value));
        	return valueOfAddedNode - root.value;
        }
       	log("Duh ... no node was added - no solution at inx " + inx + "?");
        return -1;
    }
	    
    // Complete the balancedForest function below.
    static int balancedForest(int[] c, int[][] edges) {
        title("Starting balanced forest for array of size " + c.length);

        Node[] nodes = new Node[c.length];
        for(int inx = 0; inx < c.length; inx++){
            nodes[inx] = new Node(c[inx]);
        }
        Node root = buildTree(nodes, edges);
        List<Node> current = Arrays.asList(root);
        int depth = 1;
        while(current.size() > 0){
            log("Depth " + (depth++) + ": ");
            List<Node> next = new ArrayList<>();
            for(Node node : current){
                log("  - " + node.display());
                next.addAll(node.kids);
            }
            current = next;
        }
        // Assume that all subtrees of the root are candidates for the subtrees..
        // Try a simple iteration over these to see if we can find a solution:
        int bestSolution = -1;
        for(int inx = 0; inx < root.kids.size(); inx++){
            int solution = getSolution(root, inx);
            if(solution < 0) continue;
            bestSolution = bestSolution < 0 
                    ? solution  
                    : bestSolution > solution ? solution : bestSolution; 
        }    
        return bestSolution; // LOl .. we know this won't work
    }

    
    private static InputStream getStream() {
    	String res = "2"
    	+ "\n5"
    	+ "\n1 2 2 1 1"
    	+ "\n1 2"
    	+ "\n1 3"
    	+ "\n3 5"
    	+ "\n1 4"
    	+ "\n3"
    	+ "\n1 3 5"
    	+ "\n1 3"
    	+ "\n1 2";
    	
    	String res2 = "1\n" + 
    			"6\n" + 
    			"12 10 8 12 14 12\n" + 
    			"1 2\n" + 
    			"1 3\n" + 
    			"1 4\n" + 
    			"2 5\n" + 
    			"4 6";
    	return new ByteArrayInputStream(res2.getBytes(Charset.forName("UTF-8")));
    }

    private static final Scanner scanner = new Scanner(getStream()); // System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = null; //new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int qItr = 0; qItr < q; qItr++) {
            int n = scanner.nextInt();
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            int[] c = new int[n];

            String[] cItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int i = 0; i < n; i++) {
                int cItem = Integer.parseInt(cItems[i]);
                c[i] = cItem;
            }

            int[][] edges = new int[n - 1][2];

            for (int i = 0; i < n - 1; i++) {
                String[] edgesRowItems = scanner.nextLine().split(" ");
                scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

                for (int j = 0; j < 2; j++) {
                    int edgesItem = Integer.parseInt(edgesRowItems[j]);
                    edges[i][j] = edgesItem;
                }
            }

            int result = balancedForest(c, edges);
            System.out.println("Final answer:" + result);
            //bufferedWriter.write(String.valueOf(result));
            //bufferedWriter.newLine();
        }

        //bufferedWriter.close();

        scanner.close();
    }
}

