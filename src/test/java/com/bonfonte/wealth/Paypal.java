package com.bonfonte.wealth;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.function.Consumer;

public class Paypal {

	static class Node<T> {
		T t;
		Node next, prev;
		
		public Node(T t) {
			this.t = t;
		}
		
		public void walk(Consumer<Node> func) {
			func.accept(this);
			if(this.next != null) this.next.walk(func);
		}
		
		public Node(T... ts) {
			Node tail = null;
			for (int i = 0; i < ts.length; i++) {
				if(tail == null) {
					this.t = ts[i];
					tail = this;
				}else {
					Node next = new Node(ts[i]);
					tail.next = next;
					next.prev = tail;
					tail = next;
				}				
			}
		}
		
		
	}
	
	static class Visitor{
		int count;
		
		public void getLength(Node n) {
			if(n == null) return;
			count++;
		}
		
	}
	
	private static int calc(Node<?> node) {
		Visitor v = new Visitor();
		node.walk(v::getLength);
		return v.count;
	}
	
	public static void main(String[] args) {
		Visitor v = new Visitor();
		Visitor v2 = new Visitor();
		Node<Integer> test = new Node<>(1);
		Node<Integer> test2 = new Node<>(2);
		test.next = test2;
		
		test.walk(v::getLength);
		
		System.out.println("Length v= " + v.count);		
		System.out.println("Length v2= " + calc(test2));	
		TreeSet<Integer> tree = new TreeSet<Integer>();
		tree.add(4);
		tree.add(2);
		tree.add(18);
		tree.add(5);
		tree.add(9);
		System.out.println("Tree first = " + tree.first());
		System.out.println("Tree last = " + tree.last());
		
		
		LinkedList<Integer> list = new LinkedList<>();
		list.add(3);
		list.add(5);
		for(Integer i : tree) {
			list.add(i);
		}
		int i = 0;
		System.out.println("Tree=" + tree);
		System.out.println("List=" + list);
	
		
		Node root = new Node(1, 5, 2, 3, 6, 7, 9, 4);
		System.out.println("Length root= " + calc(root));		
		
	}
	
}
