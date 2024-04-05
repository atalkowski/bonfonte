package com.bonfonte.wealth.q2;
/* 
Problem Question 2:
  Beads problem - find the longest necklace. I.e. find the longest sequence from 
  an array arr where each element identifies its successor.
  
  Issues to take care of: 
  No catches here.
*/
//you can also use imports, for example:
//import java.util.*;

//you can write to stdout for debugging purposes, e.g.
//System.out.println("this is a debug message");

/* 
Problem Question 2:
Beads problem - find the longest necklace. I.e. find the longest sequence from 
an array arr where each element identifies its successor.

Issues to take care of: 
No catches here. Just an O(n) simple scan.
*/

import java.util.Arrays;
import java.util.stream.Collectors;

public class Solution {
 
	static void log(String s) {
		System.out.println(s);
	}

	static String a2s(int[] arr) {
		return "[" + Arrays.stream(arr).boxed().map(n -> n.toString()).collect(Collectors.joining(" "))
				+ "]";
	}
	
	static int solution(int[] A) {
		if(A.length == 0) return 0; // Array empty => 0
		int longest = 0;
		// Init an array identifying beads we have already processed; 
		boolean[] done = new boolean[A.length];

		int pos = 0;
		while(pos < A.length) {
			if(done[pos]) { // This chain sequence already counted so move on
				pos++;
				continue;
			}
			int bead = A[pos];
			done[pos++] = true;
			
			int next = A[bead];
			int leng = 1;
			// Scan through the beads until we hit the original bead
			while(next != bead) {
				leng++;
				done[next] = true;
				next = A[next];
			}
			if(leng > longest) {
				longest = leng;
				if(longest > A.length / 2) break;
			}
		}
		return longest;
	}

	public static void main(String[] args){
		int[] a1 = { 5, 4, 0, 3, 1, 6, 2 };
		int[] a2 = { 0 };
		int[] a0 = {};
		log("Longest for " + a2s(a0) + " is " + solution(a0));
		log("Longest for " + a2s(a1) + " is " + solution(a1));
		log("Longest for " + a2s(a2) + " is " + solution(a2));
	}
}