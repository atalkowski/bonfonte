package com.bonfonte.radius;

import java.util.function.Consumer;

class Solution {
    
    static void log(String s) {
		System.out.println(s);
	}

	static class Tree{
		int x;
		Tree l, r;
		public Tree(int x) {
			this.x = x;
		}
	}	
	// We rely on a walk function that visits all nodes and counts those
	// (using the visitor class METHOD called countVisible)...
	// that for any value x >= root.x value (including the root itself)
	public static void walk(Tree t, Consumer<Tree> visitor) {
		if(t == null) return;
		visitor.accept(t);
		if(t.l != null) walk(t.l, visitor);
		if(t.r != null) walk(t.r, visitor);
	}		

	static class Visitor{
		// This class provides methods for recording information as we "walk" the tree.
		// For example: we can create the "visible node" count
		int visible = 0;
		int rootValue;

        public Visitor(Tree initial){
            this.rootValue = initial.x;
        }
        
        void countVisible(Tree t){
            if(t != null && t.x >= rootValue) visible++; 
        }		
	}

    public int solution(Tree T) {
        if(T == null) return 0;
        // write your code in Java SE 8
        Visitor visitor = new Visitor(T);
        walk(T, visitor::countVisible);
        return visitor.visible;       
    }
    
    private static void addKids(Tree t, int l, int r) {
    	if(l >= 0) {
    		t.l = new Tree(l);    		
    	}
    	if(r >= 0) {
    		t.r = new Tree(r);    		
    	}
    	
    }
    
    public static void main(String[] args) {
    	Tree root = new Tree( 10 );
    	addKids(root, 15, 8);
    	addKids(root.l, 3, 10);
    	addKids(root.r, 14, -3);
    	addKids(root.l.l, 7, 77);
    	addKids(root.l.r, 9, 99);
    	Solution s = new Solution();
    	log("Solution for this tree is " + s.solution(root) + " should be 6");	
    
    }
    
}