package com.bonfonte.wealth.q1;
/*
Problem Question 1:
   Given a set of N dice that are rolled onto a table and have various top oip counts, 
   compute the minimum number of 1/4 turns of the dice to ensure all dice have the same number showing,
   
   E.g.
   3 dice showing 1, 2, 3 ... the answer is two ... turn the 2 and 3 to show all 1's
   3 dice showing 1, 2, 6 ... also two ... but this time must turn 1 and 6 to show all 2's
   
   Recall all dice are such that opposite sides add up to 7
 
   Speed is not a part of this question.
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
	
	static int solveSlow(int[] arr) {
		int least = -1;
		// For each possible pip compute the number of turns required:
		for(int pip = 1; pip <= 6; pip++) {
			int total = 0;
			for(int i = 0; i < arr.length; i++) {
				if(arr[i] == pip) continue;
				if(arr[i] + pip == 7) {
					total++; // Extra turn required here when pip is opposite pip
				}
				total++;
			}
			if(least == -1 || total < least) {
				least = total;
			}
		}
		return least;
	}
	
	static int solution(int[] arr) {
		return solveSlow(arr);
	}

	public static void main(String[] args){
		int[] p1 = { 1, 2, 3 };
		int[] p2 = { 1, 1, 6 }; // Should return 2
		int[] p3 = { 1, 6, 2, 3 }; // Should return 3
		int[] p4 = { 1, 1, 1, 1 }; // Should return 0
		int[] p5 = { 1, 1, 6, 6, 6}; // Should return 4
		log(a2s(p1) + " => 2? " + solution(p1));
		log(a2s(p2) + " => 2? " + solution(p2));
		log(a2s(p3) + " => 3? " + solution(p3));
		log(a2s(p4) + " => 0?" + solution(p4));
		log(a2s(p5) + " => 4?" + solution(p5));
	}
}