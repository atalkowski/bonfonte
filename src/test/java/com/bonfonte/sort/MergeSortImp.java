package com.bonfonte.sort;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

public class MergeSortImp {

	static void log(String s) {
		System.out.println(s);
	}
	
	static String aToS(int[] a) {
		return Arrays.stream(a).boxed().collect(Collectors.toList()).toString();
	}
	
	static void copy(int[] src, int from, int[] dest, int to, int count) {
		for(int i = 0; i < count; i++) {
			dest[to++] = src[from++];
		}
	}
	
	static int[] create(int[] src, int from, int count) {
		int[] res = new int[count];
		copy(src, from, res, 0, count);
		return res;
	}
	
	static void merge(int[] arr, int lf, int m, int r) {
		int[] a1 = create(arr, lf, m + 1 - lf);
		int[] a2 = create(arr, m + 1, r - m);
		int i1 = 0;
		int i2 = 0;
		while(i1 < a1.length && i2 < a2.length){
			if(a1[i1] <= a2[i2]) {
				arr[lf++] = a1[i1++];
			}else {
				arr[lf++] = a2[i2++];
			}
		}
		while(i1 < a1.length){
			arr[lf++] = a1[i1++];
		}
		while(i2 < a2.length){
			arr[lf++] = a2[i2++];
		}
	}
	
	static void mergeSort(int[] arr, int lf, int rt) 
	{ 
	    if (lf < rt) 
	    { 
	        // Same as (lf+rt)/2, but avoids overflow for 
	        // large lf and h 
	        int m = lf+(rt-lf)/2; 
	  
	        // Sort first and second halves 
	        mergeSort(arr, lf, m); 
	        mergeSort(arr, m+1, rt); 
	  
	        merge(arr, lf, m, rt); 
	    } 
	}
	
	static void mergeSort(int[] arr) {
		mergeSort(arr, 0, arr.length-1);
	}
	
	public static class TestMergeSort{
	
		private static void runTest(int[] a) {
			log("Sorting input:" + aToS(a));
			mergeSort(a);
			log("Result output:" + aToS(a));
		}
		
		@Test 
		public void test1() {
			int[] a = { 3, 5, 1, 9, 6, 7, 3, 4, 8 };
			runTest(a);
		}
		
	    static long countInversions1(int[] arr) {
	        int top = arr.length;
	        long swaps = 0L;
	        
	        while(top-- > 1){
	            long count = 0L;
	            for(int lo = 0; lo < top; lo++){
	                if(arr[lo] > arr[lo+1]){
	                    int tmp = arr[lo];
	                    arr[lo] = arr[lo+1];
	                    arr[lo+1] = tmp;
	                    count++;
	                }
	            }
	            if(count == 0L) break;
	            swaps += count;
	        }
	        return swaps;
	    }
	}
}
