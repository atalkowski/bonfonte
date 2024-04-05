package com.bonfonte.algos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

public class StringSPlits {

	static void log(String s) {
		System.out.println(s);
	}
	
	static void box(String s) {
		String line = s.replaceAll(".", "*") + "****";
		log(line + "\n* " + s + " *\n" + line);
	}

	static boolean isValidIpPart(String s) {
		if (s == null | s.length() == 0) return false;
		//log("Checking IP part " + s);
		try { 
			Integer i = Integer.parseInt(s);
			return (i >= 0 && i <= 255);
		}catch(Exception e) {
			return false;
		}
	}

	static List<String> getNextValid(String s, int pos){
		List<String> res = new ArrayList<>();
		for(int i = 1; i <= 3; i++) {
			if(pos + i > s.length()) break;
			String part = s.substring(pos, pos + i);
			if(isValidIpPart(part)) {
				res.add(part);
			}
		}
		return res;
	}
	
	static List<String> getValidIps(String s){
		if(s == null) return new ArrayList<>();
		return getIps(s, 0, 0);
	}
	
	static List<String> getIps(String s, int pos, int depth) {
		//log("getIps(" + pos + ", " + depth + ") -> prefix part = " + s.substring(0, pos));
		List<String> res = new ArrayList<>();
		if(4 - depth + pos > s.length()) return res;
		List<String> nextList = getNextValid(s, pos);
		for(String next : nextList) {
			if(depth == 3) {
				if(next.length() + pos == s.length()) {
					//log("Select only valid part " + next + " at depth " + depth);
					res.add(next);
					break;
				}else {
					//log("Rejecting " + next + " at pos " + pos);
				}
			}else{
				List<String> parts = getIps(s, pos + next.length(), depth + 1);
				for(String part : parts) {
					//log("Adding " + next + " and " + part + " at " + depth);
					res.add(next + "." + part);
				}
			}
		}
		return res;
	}
	
	public static class TestStringSPlits{

		private void runTest(String s, String... strings) {
			box("Testing input " + s);
			List<String> actual = getValidIps(s);
			log("Result = " + actual);
			Collections.sort(actual);
			List<String> expected = Arrays.asList(strings);
			Collections.sort(expected);
			Assert.assertEquals(expected, actual);
			int[] sweets = { 3, 1, 0, 0, 2, 5 }; 
			int xtra = Arrays.stream(sweets).boxed().mapToInt(c -> c).sum();
			log("Total xtra = " + xtra);
		}
		
		@Test
		public void testSimple(){
			runTest("255255111", "2.55.255.111", "25.5.255.111", "25.52.55.111",
					"255.2.55.111", "255.25.5.111", "255.25.51.11", "255.255.1.11", "255.255.11.1");			
		}

		@Test
		public void testShort(){
			runTest("2511", "2.5.1.1");			
		}

	}
}
