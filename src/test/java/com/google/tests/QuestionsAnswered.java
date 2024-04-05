package com.google.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


public class QuestionsAnswered {

	public static class FindSentences {
		private static Set<String> DICTIONARY = new HashSet<String>();
		static {
			String[] words = { "a", "i", "ape", "pea", "peanut", "but", "utter", "butt", "butter", "butte", "nut" };
			for (String word : words ) {
				DICTIONARY.add( word );
			}
		}
		
		public static List<String> parse( String s ) {
			List<String> result = new ArrayList<String>();
			if ( s == null || s.isEmpty() ) {
				return result;
			}
			List<String> starters = getStartWords( s );
			for (String word : starters) {
				String tail = s.substring( word.length() );
				List<String> tailResults = parse( tail );
				if (tailResults.isEmpty()) {
					if (tail.length() == 0) {
						result.add( word );
					}
				} else {
					for ( String tailResult : tailResults ) {
						result.add( word + " " + tailResult );
					}
				}
			}
			return result;
		}
		
		private static List<String> getStartWords( String s ) {
			List<String> words = new ArrayList<String>();
			int pos = 0;
			while ( pos++ < s.length() ) {
				String word = s.substring(0,pos);
				if (DICTIONARY.contains(word.toLowerCase())) {
					words.add( word );
				}
			}
			return words;
		}
	
		public List<String> testOne(String sample) {
			List<String> results = parse( sample );
			System.out.println("Found " + results.size() + " permutations for " + sample);
			for ( String result : results ) {
				System.out.println( "---> " + result);
			}
			return results;
		}
	
		@Test
		public void testIt() {
			Assert.assertTrue( testOne("peanutbutter").size() == 2 );
			testOne("peanut");
			testOne("peanututter");
			testOne("PeanutButte");
			testOne("PeanutBue");
			Assert.assertTrue( testOne("apeanutbutter").size() == 3 );
		}
	}
	
	public static class FindFirstMatchingChar {
		private static int findFirst1(String a, String b) {
			if (a == null || b == null || a.length() == 0 || b.length() == 0) {
				return -1;
			}
			int pos = 0;
			int alen = a.length();
			int blen = b.length();
//			a = a.toLowerCase();
//			b = b.toLowerCase();
			int max = alen < blen ? blen : alen;
			while (pos < max) {
				if (pos < alen) {
					if (b.indexOf(a.charAt(pos)) >= 0) { 
						return pos;
					}
				}
				if (pos < blen) {
					if (a.indexOf(b.charAt(pos)) >= 0 ) {
						return pos;
					}
				}
				pos++;
			}
			
			return -1;
		}

		private static int findFirst2(String a, String b) {
			if (a == null || b == null || a.length() == 0 || b.length() == 0) {
				return -1;
			}
			int pos = 0;
//			a = a.toLowerCase();
//			b = b.toLowerCase();
			if (a.length() > b.length()) {
				String temp = a;
				a = b;
				b = temp;
			}
			while (pos < a.length()) {
				int index = b.indexOf(a.charAt(pos));
				if (index >= 0) {
					return pos < index ? pos : index;
				}
				pos++;
			}
			return -1;
		}

		private static int findFirst3(String a, String b) {
			if (a == null || b == null || a.length() == 0 || b.length() == 0) {
				return -1;
			}
			int pos = 0;
//			a = a.toLowerCase();
//			b = b.toLowerCase();
			while (pos < a.length()) {
				int index = b.indexOf(a.charAt(pos));
				if (index >= 0) {
					return pos < index ? pos : index;
				}
				pos++;
			}
			return -1;
		}

		private void runTest( int method, String a, String b, int expect ) {
			int actual = expect;
			if (method == 1) {
				actual = findFirst1( a, b );
			}
			if (method == 2) {
				actual = findFirst2( a, b );
			}
			if (method == 3) {
				actual = findFirst3( a, b );
			}
			String msg = "FindFirst" + method + "(" + a + "," + b + ") expects " + expect + " but got " + actual;
			long now = System.currentTimeMillis();
			for ( int i = 0; i < 100000; i++) {
				if (method == 1) {
					findFirst1( a, b );
				} else {
					findFirst2( a, b );
				}
			}
			long then = System.currentTimeMillis();
			
			System.out.println( msg + "; time=" + (then-now) + "ms / 100000" );
			Assert.assertTrue(msg, expect==actual);
		}
		
		@Test
		public void testFindFirst1()  {
			runTest(1, "hello", "marble", 1 ); 
			runTest(2, "hello", "marble", 1 ); 
			runTest(3, "hello", "marble", 1 ); 
			runTest(1, "Some text here", "abcdf", -1);
			runTest(2, "Some text here", "abcdf", -1);
			runTest(3, "Some text here", "abcdf", -1);
			runTest(1, "This is a particularly long piece of text", "01234567890---15---20xxx", 21);
			runTest(2, "This is a particularly long piece of text", "01234567890---15---20xxx", 21);
			runTest(3, "This is a particularly long piece of text", "01234567890---15---20xxx", 21);
			runTest(1, "This is a particularly long piece of text", "01234567890---15---20", -1);
			runTest(2, "This is a particularly long piece of text", "01234567890---15---20", -1);
			runTest(3, "This is a particularly long piece of text", "01234567890---15---20", -1);
			runTest(1, "01234567890---15---20", "This is a particularly long piece of text", -1);
			runTest(2, "01234567890---15---20", "This is a particularly long piece of text", -1);
			runTest(3, "01234567890---15---20", "This is a particularly long piece of text", -1);
		}
	}
}
