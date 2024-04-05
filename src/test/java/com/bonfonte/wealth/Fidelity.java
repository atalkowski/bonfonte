package com.bonfonte.wealth;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.junit.Test;

public class Fidelity {

	/*
	 * Count of Shakespeare independent of case;
	 * 
	 * Get top ten ... 
	 */
	static void log(String s) {
		System.out.println(s);
	}

	static InputStream getInput( String file) {
		try {
	        File initialFile = new File("/Users/andy/Downloads/" + file);
	        InputStream input = new FileInputStream(initialFile);
	        return input;
		}catch(Exception e) {
			return null;
		}
	}
	
	// Heap ... sort word + count
	static Map<String, Long> topTen(String file){
		Map<String, Long> wordMap = new HashMap<>();
		Map<String, Long> result = new TreeMap<>(); // Short ansewr
		TreeMap<Long, String> countMap = new TreeMap<>();
	
		InputStream stream = getInput(file);
		if(stream == null) return result;
		Scanner scanner = new Scanner(stream);

		while(scanner.hasNext()) {
			String line = scanner.nextLine().toLowerCase();
			String[] words = line.split("[\t)( ,.;?!+-]");
			for(String word : words) {
				if(word == null || word.length() == 0) continue;
				wordMap.put(word, wordMap.computeIfAbsent(word, w -> 0L) + 1L);
			}
		}
		scanner.close();

		for(String word : wordMap.keySet()) {
			Long count = wordMap.get(word);
			if(countMap.size() < 10 || (countMap.size() > 0 && countMap.firstEntry().getKey() < count)) {
				countMap.put(count, word);
				if(countMap.size() > 10) {
					countMap.remove(countMap.firstKey());
				}
			}
		}
		for(Map.Entry<Long, String> e : countMap.entrySet()) {
			result.put(e.getValue(), e.getKey());
		}
		
		for(String word : result.keySet()) {
			log(word + " -> " + result.get(word));
		}
		return result;
	}
	
	static long count(String keyword, String filename) {
		// ... null? 
		InputStream stream = getInput(filename);
		if(stream == null) return 0L;
		Scanner scanner = new Scanner(stream);
		long total = 0;
		keyword = keyword.toLowerCase();
		
		while(scanner.hasNext()) {
			String line = scanner.nextLine().toLowerCase();
			long tot = find(line, keyword);
			total += tot;
			if(tot > 0) {
				log("Found " + total);
			}
			
		}
		scanner.close();
		return total;
	}

	static long find(String line, String pattern) {
		if(line.indexOf(pattern) < 0) return 0L;
		return Arrays.stream(line.split("[ ;,+-]")).filter(word -> word.equals(pattern)).count();
	}
	
	
	public static class TestFidelity{
		
		private void runtest(String name, String file) {
			long count = count(name, file);
			log("Total for " + name + " = " + count);
		}
		
		//@Test
		public void test1() {
			runtest("Shakespeare", "sample.txt");
		}
		
		@Test
		public void test2() {
			topTen("guten.txt");
		}

	}
	
}
