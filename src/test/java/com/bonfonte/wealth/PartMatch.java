package com.bonfonte.wealth;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import org.junit.Test;

public class PartMatch {
	
	static final String NO = "NO";
	static final String YES = "YES";
	static void log(String s) {
		System.out.println(s);
	}
	
	static class PointerList{
		private Stack<Integer> stack = new Stack<Integer>();
		
		public PointerList() {
			stack.push(0);
		}
		
		public int current() {
			return stack.peek();
		}
		
		public int next() {
			int value = stack.pop() + 1;
			stack.push(value);
			return value;
		}
		
		public int down() {
			stack.pop();
			return stack.peek();
		}
		
		public void up() {
			stack.push(stack.peek() + 1);
		}
		
		public void set(int value) {
			stack.pop();
			stack.push(value);
		}
		
		public boolean isValidFor(char[] a) {
			int cur = current();
			return 0 <= cur && cur < a.length; 
		}
		
		@Override
		public String toString() {
			Stack<Integer> saved = new Stack<>();
			String p = "Pointers " + stack.size() + " elements: [";
			while(stack.size() > 0) {
				saved.push(stack.pop());
			}
			while(saved.size() > 0) {
				stack.push(saved.pop());
				p += " " + stack.peek(); 
			}
			return p + "]";
		}
	}

	
	static boolean isLower( int a ){
		return 'a' <= a && a <= 'z';
	}
	
	
	static boolean matchUpperB(char a, char b){
		return a == b || ( a - 'a' + 'A') == b;
	}

	static int getNextMatchPos(char[] amixed, BitSet bits, int from, int B){
		while(from < amixed.length){
			int A = amixed[from];
			if(B == A) return from;
			if(!bits.get(from)) break;
			from++; 
		}
		return -1;
	}

	static boolean restAreLower(char[] amixed, int from) {
		while(from < amixed.length) {
			if(!isLower(amixed[from++])) return false;
		}
		return true;
	}

	static Set<Integer> asSet(char[] a){
		Set<Integer> used = new HashSet<>();
		for(int i = 0; i < a.length; i++) {
			char ac = a[i];
			if(isLower(ac)) {
				used.add(a[i] - 'a' + 'A');
			}else {
				used.add(0 + a[i]);
			}
		}
		return used;
		
	}
	
	static char[] cleanUseless(char[] aa , Set<Integer> used) {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < aa.length; i++) {
			char ch = aa[i];
			if( isLower(ch) && !used.contains(ch - 'a' + 'A') ) {
				continue;
			}
			sb.append(ch);
		}
		return sb.toString().toCharArray();
	}
	
	static boolean allPresent(Set<Integer> used, Set<Integer> avail) {
		for(Integer a : used) {
			if(!avail.contains(a)) return false;
		}
		return true;
	}
	
	static char[] asUpper(char[] a) {
		char[] res = new char[a.length];
		for(int j = 0; j < a.length; j++) {
			char c = a[j];
			if(isLower(c)) {
				res[j] = (char)(c - 'a' + 'A');
			}else {
				res[j] = c;
			}
		}
		return res;
	}

	static BitSet lowBits(char[] a) {
		BitSet res = new BitSet(a.length);
		for(int j = 0; j < a.length; j++) {
			res.set(j, isLower(a[j]));
		}
		return res;
	}

	
	static String abbreviation(String aa, String bb) {
		if(bb.length() == 0) return NO;
		char[] b = bb.toCharArray();
		char[] a = aa.toCharArray();
		Set<Integer> used = asSet(b);
		a = cleanUseless(a, used);
		
		if(b.length > a.length) return NO;
		Set<Integer> avail = asSet(a);
		if(!allPresent(used, avail)) return NO;
		char[] A = asUpper(a);
		BitSet bits = lowBits(a);
		
		PointerList pointers = new PointerList();
		int bpos = 0;
		int offset = 0;
		
		while(bpos < b.length) {
			boolean lastChar = bpos + 1 == b.length;
			if(!pointers.isValidFor(a)) {
				return NO;
			}
			offset = pointers.current();
			log("Examining " + bpos + " at offset " + offset);
			int matchPos = getNextMatchPos(A, bits, offset, b[bpos]);
			if(matchPos >= 0) {
				pointers.set(matchPos);
				if(lastChar) {
					if(restAreLower(a, matchPos + 1)) break; // BINGO!!
					// Aggh failed... so fall thru and check...
				}else {
					bpos++;
					pointers.up();
					continue;
				}
			}
			while(true) {
				if(bpos == 0) return NO;
				bpos--; // Go back to previous search
				int d = pointers.down();
				offset = pointers.current();
				log("Rewinding to previous stack at " + bpos + " of " + b.length + " from pos " + d + " to " + offset);
				if(bits.get(offset)) {
					pointers.next(); // try the next lowercase character
					log("Settling on bpos = " + bpos + " pointer now at " + pointers.current());
					break;
				}
			}
		}
		return YES;
	}
	
	public static class TestMatches{
		static boolean simple = false;
		private void runTest(String a, String b, String expected) {
			if(!simple) return; 
			String actual = abbreviation(a, b);
			log("Running abbrev(" + a + ", " + b + ") = " + actual + " " 
					+ (expected.equals(actual) ? " -> SUCCESS" : " -> FAIL"));
		}
		
		@Test
		public void runTest1() {
			runTest("ABC", "ABC", YES);
			runTest("ABC", "ACB", NO);
		}
		
		@Test
		public void runTest2() {
			runTest("dABCxx", "ABC", YES);
			runTest("dABcC", "ABC", YES);
			runTest("ccCdCD", "CCD", YES);
			runTest("ccCdCD", "CDD", NO);
			runTest("abeceAfBfbCcaaa", "ABC", YES);
			runTest("abeceAfBfBbCcaaa", "ABC", NO);
		}
		
		@Test
		public void runTestComplex() {
			runTest("beFgH", "EFH", YES);
		}
		
		public static void runFileTest(String name) {
			if(simple) return;
			File file = new File("/Users/andy/wspaces/data/" + name);
			InputStream input = null;
			try {
				input = new FileInputStream(file);
				Scanner scanner = new Scanner(input);
		        int q = scanner.nextInt();
		        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
		        log("Total queries = " + q);
		        for (int qItr = 0; qItr < q; qItr++) {
		        	log("Reading query lines " + qItr);
		        	String a = scanner.nextLine();
		            String b = scanner.nextLine();
		            String result = abbreviation(a, b);
		            log("\n================================================>>>>");
		            log("a=" + a);
		            log("b=" + b);
		            log("result=" + result);
		            log("==================== DONE ==========================");
		        }
		        scanner.close();
		        
			}catch(Exception e) {
				log("Ooops .. failed to process file " + e.getMessage());
			}
			try {
				if(input != null) input.close();
			}catch(Exception e) {
				log("Ooops .. failed to close input " + e.getMessage());
			}
		}
		
		@Test
		public void runCase20() {
			runFileTest("abbrev.inp");
		}
		
	}

}
