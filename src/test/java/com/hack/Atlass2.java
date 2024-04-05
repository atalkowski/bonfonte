package com.hack;

import java.util.Stack;

public class Atlass2 {
    static void log(String s){
        System.out.println(s);
    }
    
    public static class Node{
        int data;
        Node left, right;
        public Node(int data){
            this.data = data;
        }
        
        public void add(int data, boolean asc){
            if(data == this.data) return;
            if(data < this.data == asc){
                if(this.left == null){
                    this.left = new Node(data);
                }else{
                    this.left.add(data, asc);
                }
            }else{
                if(this.right == null){
                    this.right = new Node(data);
                }else{
                    this.right.add(data, asc);
                }
            }
        }
        
    
        @Override 
        public String toString(){
            String res = "" + data;
            res += " " + left; // 
            res += " " + right; // Same deal here
            return res;
        }
        
    }

    public static boolean reflects(Node a, Node b){
        Stack<Node> as = new Stack<>();
        Stack<Node> bs = new Stack<>();
        if(a == null || b == null) return b == a;
        as.push(a);
        bs.push(b);
        while(as.size() > 0 && bs.size() > 0){
            a = as.pop();
            b = bs.pop();
            if(a == null || b == null){
                if(a != b) return false;
            }else{
                if(a.data != b.data) return false;
                as.push(a.left);
                as.push(a.right);
                bs.push(b.right);
                bs.push(b.left);
            }
        }
        return as.size() == bs.size();        
    }

    static Node createTree(boolean asc, int... values){
        Node head = new Node(values[0]);
        for(int i = 1; i < values.length; i++){
            head.add(values[i], asc);
        }
        return head;
    }

    static void runTest(Node t1, Node t2, boolean expected){
        boolean res = reflects(t1, t2);
        log("t1:" + t1 + "\nt2:" + t2 + "\nReflects?:" + res);
        log("Test " + ( expected == res ? "SUCCESS" : "FAILED?"));
    }

    public static void main(String args[] ) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        Node t1 = createTree(true, 1, 5, 6, 7, 4, 3);
        Node t2 = createTree(false, 1, 5, 6, 7, 4, 3);
        Node t3 = createTree(true, 1, 6, 5, 7, 4, 3);
        
        runTest(t1, t2, true);
        runTest(t1, t3, false);
    }
}