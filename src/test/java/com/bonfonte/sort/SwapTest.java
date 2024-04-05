package com.bonfonte.sort;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

public class SwapTest {

	static void log(String s) {
		System.out.println(s);
	}
		
    static long countInversions(int[] arr) {
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
	
	static long merge(int[] arr, int lf, int m, int r) {
		int[] a1 = create(arr, lf, m + 1 - lf);
		int[] a2 = create(arr, m + 1, r - m);
		int i1 = 0;
		int i2 = 0;
		long swaps = 0L;
		int pos = lf;
		while(i1 < a1.length && i2 < a2.length){
			if(a1[i1] <= a2[i2]) {
				arr[pos++] = a1[i1++];
			}else {
				arr[pos++] = a2[i2++];
				swaps += (a1.length - i1);
			}
		}
		swaps += (a1.length - i1);
		while(i1 < a1.length){
			arr[pos++] = a1[i1++];
		}
		while(i2 < a2.length){
			arr[pos++] = a2[i2++];
		}
//		log(" -- merge " + lf + "," + m + "," + r + " -> " + swaps);
		return swaps;
	}
	
	static long mergeSort(int[] arr, int lf, int rt) { 
		long swaps = 0L;
	    if (lf < rt) 
	    { 
	        // Same as (lf+rt)/2, but avoids overflow for 
	        // large lf and h 
	        int m = lf+(rt-lf)/2; 
	  
	        // Sort first and second halves 
	        swaps += mergeSort(arr, lf, m); 
	        swaps += mergeSort(arr, m+1, rt); 
	        swaps += merge(arr, lf, m, rt); 
	    } 
	    return swaps;
	}
	
	static long mergeSort(int[] arr) {
		return mergeSort(arr, 0, arr.length-1) - 1;
	}
  
	public static class TestSwapTest{
		
		private void runTest(int[] arr) {
			log("\n---------------- START TEST ---------------");
	    	int[] c1 = create(arr, 0, arr.length);
	    	int[] c2 = create(arr, 0, arr.length);
	        log("Here is the old array: " + aToS(c1));
	        long t0 = System.currentTimeMillis();
	        long c1swaps = countInversions(c1);
	        long t1 = System.currentTimeMillis() - t0;
	        log("Here is the new array: " + aToS(c1));
	        
	        log("Here is the old array: " + aToS(c2));
	        t0 = System.currentTimeMillis();
	        long c2swaps = mergeSort(c2);
	        long t2 = System.currentTimeMillis() - t0;
	        log("Here is the new array: " + aToS(c2));
	        log("Total swaps were v1=" + c1swaps + " : (v2)" + c2swaps);
	        log("Total times were v1=" + t1 + "ms : v2=" + t2 + "ms");
			log("----------------- END TEST ----------------");
		}
		
		@Test
		public void test1() {
			int[] a1 = { 3, 2 };
			runTest(a1);
		}

		@Test
		public void test2() {
			int[] a1 = { 3, 2, 4, 5, 7, 1, 8, 2, 9 };
			runTest(a1);
		}

		@Test
		public void test3() {
			int[] a1 = { 11, 9, 18, 77, 3, 2, 4, 5, 7, 1, 8, 2, 9, 25, 14, 19, 18, 88, 1004, 32, 111, 435 };
			runTest(a1);
		}

	
	}
    
    


}
