package com.bonfonte.sort;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class sortTest {
	
	private static void log( String s ) {
		System.out.println( s );
	}

//	@Test
//	public void testChops() throws Exception {
//		ChopLeaf leaf = new ChopLeaf();
//	    String[] list = { "Jim", "Bert", "Mary", "Sid", "Fred", "Alan", "Peter", "Mathew", "Steve", "Joe", "Zoe", "Arthur" };
//	    for ( String s : list ) {
//	    	int pos = leaf.locate( s );
//	    	leaf.insert( s, pos );
//	    	log( "Added " + s + " : " + leaf.show() );
//	    }
//	}
	
	   static int maximumToys(int[] prices, int k) {
	        Map<Integer,Integer> priceCounts = new TreeMap<>();
	        for(int price : prices){
	            Integer count = priceCounts.computeIfAbsent(price, p -> 0) + 1;
	            priceCounts.put(price, count);
	        }
	        int total = 0;
	        long spend = 0;
	        for(int price : priceCounts.keySet()){
	            int count = priceCounts.get(price);
	            while(count-- > 0){
	                spend += price;
	                if(spend > k) break;
	                total++;
	            }
	        }
	        return total;
	    }
	   
	   @Test
	   public void test1() {
		   int[] list = { 3, 8, 5, 3, 5, 6 };
		   int[] t2 = { 1, 12, 5, 111, 200, 1000, 10 };
		   for (int i = 1; i <= 60; i++) {
			   int max1 = maximumToys(t2, i);
			   log("Max toys for " + i + " = " + max1);
		   }
	   }
}
