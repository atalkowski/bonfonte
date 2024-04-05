package com.hack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

public class ProRata {

	static class Resolver<T> implements Comparable<Resolver<T>>{
		T key;
		int requested;
		int given;
		int shortfall;
		double percentError;
		public Resolver(T key, int requested, int given) {
			this.key = key;
			this.requested = requested;
			this.given = given;
			this.shortfall = requested - given;
			if(requested == 0 || shortfall <= 0) {
				percentError = 0.0;
			}else {
				percentError = (1.0 * shortfall) / (1.0 * requested);
			}
		}
		@Override
		public int compareTo(Resolver<T> o) {
			return Double.compare(o.shortfall, this.shortfall);
		}
	}
	
	public static <T> Map<T, Integer> apportion(int available, Map<T, Integer> requested){
		int totalRequested = requested.values().stream().mapToInt(i -> i).sum();
		if(totalRequested <= 0 || totalRequested <= available) {
			return requested;
		}
		double prorate = available/totalRequested;
		Map<T, Integer> result = new HashMap<>();
		for(T key : requested.keySet()) {
			result.put(key, (int)(requested.get(key) * prorate)); 
		}
		int totalAllocated = result.values().stream().mapToInt(i -> i).sum();
		int remaining = available - totalAllocated;
		for(;;) {
			List<Resolver<T>> resolverList = new ArrayList<>();
			for(T key : requested.keySet()) {
				Resolver<T> r = new Resolver<T>(key, requested.get(key), result.get(key));
				resolverList.add(r);
			}
			Collections.sort(resolverList);
			for(Resolver<T> r : resolverList) {
				if(r.given < r.requested) {
					r.given++;
					remaining--;
					if(remaining <= 0) break;
				}
			}
			result = resolverList.stream()
					.collect(Collectors.toMap(r -> r.key, r -> r.given));
			if(remaining <= 0) break;
		}
		return result;
	}
	
	private static void log(String s) {
		System.out.println(s);
	}

	private static void title(String s) {
		System.out.println("");
		String bar = s.replaceAll(".", "-");
		System.out.println(bar);
		System.out.println(s);
		System.out.println(bar);
	}

	public static class ProRataTest{
		private static String[] keys = { "Apples", "Facebk", "Google", "Amazon" };
		
		private Map<String, Integer> runTest(int available, int[] values){
			Map<String, Integer> map = new HashMap<>();
			for(int index = 0; index < keys.length; index++) {
				if(index == values.length) {
					break;
				}
				map.put(keys[index],  values[index]);
			}
			Map<String,Integer> result = ProRata.apportion(available, map); 
			title("When total available is " + available);
			for( String key : map.keySet()) {
				log("  Gave " + key + " " + result.get(key) + " of " + map.get(key));
			}
			return map;
		}
		
		@Test
		public void checkSimple1() {
			int[] values = { 5, 10, 20, 15 };
			runTest(50, values);
			runTest(25, values);
		}
		
		@Test
		public void checkSimple2() {
			int[] values = { 1, 2, 1, 1 };
			runTest(5, values);
			runTest(4, values);
			runTest(3, values);
			runTest(2, values);
			runTest(1, values);
		}

	}
	
}
