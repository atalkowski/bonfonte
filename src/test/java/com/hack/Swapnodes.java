package com.hack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Swapnodes {

	static void log(String s) {
		System.out.println(s);
	}
	
	static class Node{
		int value;
		int count;
		Node left;
		Node right;
		public Node(int value) {
			this.value = value;
			count++;
		}
		
		public void add(int value) {
			if(this.value == value) {
				count++;
				return;
			}
			if(this.value < value) {
				if(this.right == null) {
					this.right = new Node(value);
				}else{
					this.right.add(value);
				}
			}else{
				if(this.left == null) {
					this.left = new Node(value);
				}else{
					this.left.add(value);
				}
			}
		}
		
		public Node swap() {
			Node temp = left;
			left = right;
			right = temp;
			return this;
		}
		
		int size() {
			final List<Node> nodes = new ArrayList<>();
			walkOrdered((n) -> nodes.add(n));
			return nodes.size();
		}
		
		int[] widths(){
			int[] result = new int[2];
			result[0] = left == null ? 1 : left.size();
			result[1] = right == null ? 1 : right.size();
			return result;
		}
		
		void walkOrdered(Consumer<Node> func) {
			if(left != null) left.walkOrdered(func);
			func.accept(this);
			if(right != null) right.walkOrdered(func);

		}
		
		void pushLeftNode(Stack<Node> stack, Node node) {
			while(node != null) {
				stack.push(node);
				node = node.left;
			}
		}
		
		void walkDI(Consumer<Node> func) {
			// TODO ...
		}
		
		void walkDepth(Consumer<Node> func){
			List<Node> nodes = new ArrayList<>();
			nodes.add(this);
			while(!nodes.isEmpty()) {
				for(Node node : nodes){
					func.accept(node);
				}
				nodes = getAllKids(nodes);
			}
		}

		static List<Node>getKids(Node node){
			return Arrays.asList(node.left, node.right).stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

		static List<Node>getAllKids(List<Node> nodes){
			List<Node> kids = new ArrayList<>();
			for(Node node : nodes) {
				kids.addAll(getKids(node));
			}
			return kids;
		}

		static List<Node> getKidsAtDepth(List<Node> nodes, int depth){
			for(int d = 1; d < depth; d++) {
				nodes = getAllKids(nodes);
			}
			return nodes;
		}
		
		public String depthTravers() {
			final StringBuilder sb = new StringBuilder();
			this.walkDepth((n) -> sb.append(" " + n.value));
			return sb.substring(1);
		}

		public String normal() {
			final StringBuilder sb = new StringBuilder();
			this.walkOrdered((n) -> sb.append(" " + n.value));
			return sb.substring(1);
		}

		@Override
		public String toString() {
			return "" + value;
		}
		
	}
		
	static Node initTree(int[][] indexes) {
		List<Node> parents = new ArrayList<>();
		List<Node> children = new ArrayList<>();
		Node node = new Node(1);
		Node root = node;
		parents.add(node);
		int index = 0;
    	for(int[] pair : indexes) {
    		if(index >= parents.size()) {
    			index = 0;
    			parents = children;
    			children = new ArrayList<>();
    		}
   			node = parents.get(index++);
   		    int left = pair[0];
    		int right = pair[1];
    		if(left > 0) {
    			node.left = new Node(left);
    			children.add(node.left);
    		}
    		if(right > 0){
    			node.right = new Node(right);
    			children.add(node.right);
    		}
    	}		
    	return root;
	}

	static Node initOrderedTree(int[] values) {
		Node root = new Node(values[0]);
		for(int i = 1; i < values.length; i++) {
			root.add(values[i]);
		}
    	return root;
	}

	static Node swapAtDepth(Node tree, int depth, List<int[]> outputs) {
		List<Node> nodes = new ArrayList<>();
		nodes.add(tree);
		while(true) {
			nodes = Node.getKidsAtDepth(nodes, depth);
			if(nodes.size() == 0) break;
			for(Node node : nodes) {
				node.swap();
			}
			// Go to the next level (quirky count fix)
			nodes = Node.getAllKids(nodes);
		}
		outputs.add(walkValues(tree));
		return tree;
	}
	
	
	static int[] walkValues(Node node) {
		final List<Integer> values = new ArrayList<>();
		node.walkOrdered(n -> values.add(n.value));
		
		int[] result = new int[values.size()];
		for(int index = 0; index < values.size(); index++) {
			result[index] = values.get(index);
		}
		return result;
	}
	
    static int[][] swapNodes(int[][] indexes, int[] queries) {
    	Node tree = initTree(indexes);
    	List<int[]> list = new ArrayList<>();
    	for(int depth : queries) {
    		tree = swapAtDepth(tree, depth, list);
    	}
    	int[][] result = new int[list.size()][];
    	result = list.toArray(result);
	    return result;
    }
    
    public static String showArray(int[] arr) {
    	StringBuilder sb = new StringBuilder();
    	for(int r : arr) {
    		if(sb.length() > 0) sb.append(" ");
    		sb.append("" + r);
    	}
    	return sb.toString();
    }

    static void swapTest() {
		int[][] indexes = { 
				{2, 3},
				{4, -1},
				{5, -1},
				{6, -1},
				{7, 8},
				{-1, 9},
				{-1, -1},
				{10, 11},
				{-1, -1},
				{-1, -1},
				{-1, -1}
		};
    	int[] test = { 2, 4 };
		int[][] results = swapNodes(indexes, test); 
		for(int[] result : results) {
			log(showArray(result));
		}
    }

    static void topDownTest() {
    	int[] data = { 7, 8, 1, 3, 2, 5, 6, 4 };
    	/*
    	 *        .---7----.
    	 *       1--.      8
    	 *       .--3----.
    	 *       2     .-5-.
    	 *             4   6
    	 */
    	Node root = initOrderedTree(data);
    	log("Here is the tree as in order");
    	root.walkOrdered(node -> log("Order " + node));
    	log("Walking topDown");
    	root.walkDepth(node -> log("TopDown " + node));
    	
    	Stream<Integer> values = Arrays.stream(data).boxed();
    	String s = values.map(n -> n.toString()).collect(Collectors.joining(" "));
    	log("The input was " + s);
    }
    
	public static void main(String[] args) {
		swapTest();
		topDownTest();
	}
	
	
	boolean checkData(Integer prev, int data){
	    if(prev == null) return true;
	    return prev < data;
	}

    boolean checkBST(Node root) {
        Stack<Node> stack = new Stack<Node>();
        Integer prev = null;
        while(root != null){
            if(root.left != null){
                stack.push(root);
                root = root.left;
                continue;
            }
            if(!checkData(prev, root.value)) return false;
            prev = root.value;
            if(root.right != null){
                root = root.right;
                continue;
            }
            // Now handle pushed items 
            while(root != null){
                if(stack.isEmpty()) return true;
                root = stack.pop();
                if(!checkData(prev, root.value)) return false;
                prev = root.value;
                if(root.right != null){
                    root = root.right;
                    break;
                }
            }
        }
        return true;       
    }
}
