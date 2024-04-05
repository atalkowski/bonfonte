package com.bonfonte.wealth.w1;
/* Problem:
  Write a function that prints the least positive integer that is NOT present 
  in a given array of integers.
  
  Possible issues to take care of: 
  1. Empty/null array
  2. Array with dupes
  3. Array with negatives.
*/

// you can also use imports, for example:
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");
public class Solution {
    
	static void log(String s) {
		System.out.println(s);
	}

	static final int GROUP_SIZE = 1024;
	static final int BITS_PER_INT = 16;
	static final int MAX_POS = GROUP_SIZE/BITS_PER_INT;
	static final int ALL_BITS = 0xFFFF;
	
	static class NGroup{
		int[] bits = new int[MAX_POS];
		int min;

		public NGroup(int min) {
			this.min = min;
			for(int j = 0; j < MAX_POS; j++) bits[j] = 0;
			log("Created group " + min);
		}
		
		public void add(int n) {
			n = n - min;
			int pos = n/BITS_PER_INT;
			int bit = 1 << ( n % BITS_PER_INT);
			bits[pos] |= bit;
		}

		public boolean isBitSet(int n) {
			n = n - min;
			int pos = n/BITS_PER_INT;
			int bit = 1 << (n % BITS_PER_INT);
			return (bits[pos] & bit) == bit;
		}
		
		public Optional<Integer> getMinUnused() {
			int pos = 0;
			for(int j = 0; j < MAX_POS; j++) {
				if(bits[j] == ALL_BITS) {
					pos++;
				}else { 
					break;
				}
			}
			if(pos > MAX_POS) {
				return Optional.empty();
			}
			int value = bits[pos];
			int result = min + pos * BITS_PER_INT;
			int bit = 1;
			while((bit & value) == bit) { // Reject items where the bit is present:
				result++;
				bit = bit << 1;
			}
			return Optional.of(result);
		}
	}
	
	static class Grouper{
		Map<Integer, NGroup> groups = new TreeMap<>();
		
		public void add(int n) {
			if( n <= 0) {
				return; // Skip negatives and 0.
			}
			n = n - 1; // We want to count bits from 0 always.
			int groupNo = n - n % GROUP_SIZE;
			NGroup ng = groups.computeIfAbsent(groupNo, g -> new NGroup(groupNo));
			ng.add(n);
		}
		
		public int getMin() {
			int expectedGroup = 0;
			
			for(NGroup ng : groups.values()) {
				if(ng.min == expectedGroup) {
					Optional<Integer> unused = ng.getMinUnused();
					if(unused.isPresent()) {
						return unused.get() + 1;
					}
					expectedGroup += GROUP_SIZE;
				}else {
					break;
				}
			}
			NGroup missing = new NGroup(expectedGroup);
			return missing.getMinUnused().get() + 1;
		}
	}

	static String a2s(int[] arr) {
		return "[" + Arrays.stream(arr).boxed().map(n -> n.toString()).collect(Collectors.joining(" "))
				+ "]";
	}

    public static int solutionOld(int[] A) {
        // write your code in Java SE 8
        if(A == null || A.length == 0) return 1; // Exclude dopy use cases quickly
        // Get filtered and sorted list of integers using stream
        List<Integer> list = Arrays.stream(A).filter(n -> n > 0).boxed().sorted().collect(Collectors.toList());
        if(list.isEmpty()) return 1; // Double check filtered list!!
        
        int least = list.get(0);
        if(least > 1) return 1; 
        for(int i = 1; i < list.size(); i++){
           int next = list.get(i);
           if(least + 1 < next) break;
           least = next;
        }
        return least + 1;
    }

    public static int solution(int[] A) {
        // write your code in Java SE 8
        if(A == null || A.length == 0) return 1; // Exclude dopy use cases quickly
        Grouper grouper = new Grouper();
        for(int n : A) {
        	grouper.add(n);
        }
        return grouper.getMin();
    }

    // Sample tests
	public static void testLeast1() {
		int[] arr = { 3, 8, 2, 2, 2, 3, 5, 7, 1, 6 };
		int least = Solution.solution(arr);
		log("Least for " + a2s(arr) + " is " + least);
	}	

	public static void testLeast2() {
		int[] arr = { 3, -1, 8, -4, 2, 5, 7, 0, 0, 1, 3, 6 };
		int least = Solution.solution(arr);
		log("Least for " + a2s(arr) + " is " + least);
	}	
	
	public static void testLeast3() {
		int[] arr = { 81, 82, 83 };
		int least = Solution.solution(arr);
		log("Least for " + a2s(arr) + " is " + least);
	}	

	
	public static void testLeast4() {
		int[] arr = { 8001, 82, 1, 999, -1, 0, 2, 3, 4, 83, 400000, Integer.MAX_VALUE };
		int least = Solution.solution(arr);
		log("Least for " + a2s(arr) + " is " + least);
	}	

	public static void main(String[] args){
        testLeast1();
        testLeast3();
        testLeast4();
	}
}