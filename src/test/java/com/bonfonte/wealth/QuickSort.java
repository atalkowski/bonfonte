package com.bonfonte.wealth;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;


public class QuickSort {

	static void log(String s) {
		System.out.println(s);
	}

	// Implementation of quickSort
	// Takes elements between lo and hi-1 and for each if <= pivot swaps it with element at 
	// next available pos - where pos starts life at lo and is incremented each time a swap occurs.
	// This ensures the arr is split into 
	public static void quickSort(int[] arr) {
		qsort(arr, 0, arr.length-1);
	}
	
	private static void qsort(int[] arr, int lo, int hi) {
		if (lo >= hi) return; // done
		int pivot = partition(arr, lo, hi);
		qsort(arr, lo, pivot-1);
		qsort(arr, pivot+1, hi);
	}
	
	private static int partition(int[] arr, int lo, int hi) {
		int pivot = arr[hi];
		int split = lo;
		for(int j = lo; j < hi; j++) {
			if (arr[j] <= pivot) 
            { 
                // swap arr[split] and arr[j] .. unless of course split = j (no change) 
				if(j != split) {
					int temp = arr[split]; 
					arr[split] = arr[j]; 
					arr[j] = temp;
				}
				split++;
            }
		}
		if(split < hi) { // Put pivot cell at its correct spot in the array (i.e. at the split point) 
			int temp = arr[split]; 
			arr[split] = arr[hi]; 
	        arr[hi] = temp; 
		}
        return split; 
	}

	static String a2s(int[] arr) {
		return "[" + Arrays.stream(arr).boxed().map(n -> n.toString()).collect(Collectors.joining(" "))
				+ "]";
	}
	
	@Test
	public void testCase1() {
		int[] arr = { 3, 8, 2, 4, 5, 7, 1, 6 };
		log("Initial = " + a2s(arr));
		quickSort(arr);
		log("Sorted  = " + a2s(arr));
		quickSort(arr);
		log("Sorted  = " + a2s(arr));
	}	
	

	// you can write to stdout for debugging purposes, e.g.
	// System.out.println("this is a debug message");

	public static class Solution {
		// This was just a solution to a different on line issue - to
		// find the leads positive number not in a list. However,
		// This really doesn't scale well and may need to utilize a divide and conquer 
		// solution.
	    public static int solution(int[] A) {
	        // write your code in Java SE 8
	        if(A == null || A.length == 0) return 1; // Exclude dopey use cases quickly
	        List<Integer> list = Arrays.stream(A).boxed().collect(Collectors.toList());
	        Collections.sort(list);
	        int least = list.get(0);
	        if(least > 1) return least-1;
	        for(int i = 1; i < list.size(); i++){
	           int next = list.get(i);
	           if(least + 1 < next) break;
	           least = next;
	        }
	        return least + 1;
	    }
	}
	
	@Test
	public void testLeastPositiveNumberNotInTheList() {
		int[] arr = { 3, 8, 2, 5, 7, 1, 6 };
		int least = Solution.solution(arr);
		log("Least for " + a2s(arr) + " is " + least);
	}	
	
	@Test
	public void testLogs() {
		int[] pows = { 1, 2, 4, 8, 16 };
		double l2 = Math.log(2.0);
		double invlog2 = 1.0 / l2;
		for (int pow = 0; pow < pows.length; pow++) {
			double p = 0.0 + pows[pow];
			double loge = Math.log(p);
			double log2 = loge * invlog2;
			log("Log base 2 of " + p + " should be " + pow + ": got " + log2 + " (log = " + loge + ")");
		}
		double oneover2xl2 = 0.5 / l2;
		log("1 / 2log2 = " + oneover2xl2);
	}

}
