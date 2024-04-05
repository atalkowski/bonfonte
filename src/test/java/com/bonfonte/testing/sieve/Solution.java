package com.bonfonte.testing.sieve;
/* Problem:
  Write a function that returns an array which for each element in the original array
  contains a count of the numbers in the array that do not divide the value
 */

// you can also use imports, for example:
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");
public class Solution {
    
	static void log(String s) {
		System.out.println(s);
	}

	static String a2s(int[] arr) {
		return "[" + Arrays.stream(arr).boxed().map(n -> n.toString()).collect(Collectors.joining(" "))
				+ "]";
	}

	static class Solute{
		// Solutes ensure we only apply calculation of divisors to the value. This avoids duplication of work.
		int value;  // A value that appears in the original array.
		int count;  // How often this item appears in the array
		int result; // The total non-divisors in the array (= total in array minus sum of all my divisor counts) 
		Set<Integer> divisors = new HashSet<>(); // List of all possible divisors for this value (may not be in array)
		Solute(int value){
			this.value = value;
		}
		
		void buildDivisors() {
			int start = value;
			divisors.add(1);
			divisors.add(value);
			
			int div = 1;
			int maxdiv = getSquareRoot();
			
			while(start > div && div++ < maxdiv) {
				if(divisors.contains(div)) { 
					continue;
				}
				// OK compute the various factors of this value.
				// E.g. Suppose value = 36
				// we will add 1, 36 initially
				// then 2 and 18;
				// then 4 and 9;
				// then we cannot further divide 9 by div == 2 .. so we move on
				int base = div;
				while(start >= div) { // Find how often we can divide value by div
					int rem = start % div;
					if(rem != 0) break;
					start = start / div;
					divisors.add(start);
					divisors.add(base);
					base = base * div;
				}
			}
		}
		
		int getSquareRoot() {
			Double d = new Double(value);
			if(d > 0.0) {
				Double sqrt = Math.sqrt(d);
				return sqrt.intValue();
			}
			return 0;
		}
	}
		
    public static int[] solution(int[] A) {
        // write your code in Java SE 8
    	int[] result = {};    	
    	// We need to count the items for each value instance:
    	if(A == null || A.length == 0) return result; // Exclude dopy use cases quickly
    	Map<Integer, Solute> solutes = new TreeMap<>();
        result = new int[A.length];
        // Build a list of counts of each entry in ascending order
        for(int val : A) {
        	Solute solute = solutes.computeIfAbsent(val, v -> new Solute(v));
        	solute.count++;
        }
        // Now using solutes - build the divisors of the 
        for(int val : solutes.keySet()) {
        	Solute solute = solutes.get(val);
        	solute.buildDivisors();
        }
        
        for(int j = 0; j < A.length; j++) {
        	int val = A[j];
        	// For each element we use
        	Solute sol = solutes.get(val);
        	if(sol.result <= 0) {
	        	int total = 0; // We fist calculate total that DO divide this element: 
	        	for(int div : sol.divisors) {
	        		Solute divSol = solutes.get(div);
	        		if(divSol != null){
	        			total += divSol.count;
	        		}
	        	}
	        	sol.result = A.length - total;
        	}
        	result[j] = sol.result;
        }
        return result;	
    }
    
    static void runTest(int... arr) {
    	log("Running test for this array:\n  " + a2s(arr));
    	int[] res = solution(arr);
    	log("  	" + a2s(res));
    }

	public static void main(String[] args){
		runTest(3, 1, 2, 3, 6 );
		
	}
}