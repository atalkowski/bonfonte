package com.monetise;


import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;


public class CodeTest {
 
	private static boolean isAlpha( char ch ) {
		if ( 'a' <= ch && ch <= 'z' ) return true;
		if ( 'A' <= ch && ch <= 'Z' ) return true;
		return false;
	}
	
	public String getAlpha1( String source ) {
		TreeSet<String> output = new TreeSet<String>();
	    String lcase = source.toLowerCase();
		for ( int i = 0; i < source.length(); i++ ) {
			char ch = lcase.charAt(i);
			if ( isAlpha( ch ) ) {
				output.add( "" + ch );
			}
		}
		StringBuilder result = new StringBuilder();
		Iterator<String> it = output.iterator();
		while (it.hasNext()) {
			result.append(it.next());
		}
		return result.toString();
	}
	
	public String getAlpha2( String source ) {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
	    char[] alphaUsed = new char[26];
	    for ( int i = 0 ; i < 26; i++ ) {
	    	alphaUsed[i] = 0;
	    }
	    String lcase = source.toLowerCase();
		for ( int i = 0; i < source.length(); i++ ) {
			char c = lcase.charAt( i );
			int index = ( alphabet.indexOf( c ) );
			if ( index >= 0 ) {
				alphaUsed[index] = c;
			}
		}
		StringBuilder result = new StringBuilder();
		for ( int i = 0; i < alphaUsed.length; i++ ) {
			if ( alphaUsed[i] > 0 ) {
				result.append( alphaUsed[i]);
			}
		}
		return result.toString();
	}
	
	@Test 
	public void testSample() throws Exception {
		String sample = "8aurne82aarne";
		String alpha1 = getAlpha1( sample );
		String alpha2 = getAlpha2( sample );
		System.out.println( "Converted " + sample + " using getAlpha1 to '" + alpha1 + "'" );
		System.out.println( "Converted " + sample + " using getAlpha2 to '" + alpha2 + "'" );
		Assert.assertTrue( alpha1.equals( "aenru" ) );
		Assert.assertTrue( alpha2.equals( "aenru" ) );
	}
   
	@Test 
	public void testUcase() throws Exception {
		String sample = "A8bgaurne8K2Gaarnef";
		String alpha1 = getAlpha1( sample );
		String alpha2 = getAlpha2( sample );
		System.out.println( "Converted " + sample + " using getAlpha1 to '" + alpha1 + "'" );
		System.out.println( "Converted " + sample + " using getAlpha2 to '" + alpha2 + "'" );
		Assert.assertTrue( alpha1.equals( "abefgknru" ) );
		Assert.assertTrue( alpha2.equals( "abefgknru" ) );
	}
 
	
	
}
