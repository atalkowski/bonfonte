package com.zillow;

import org.junit.Test;

public class CodeTest1 {
	
	/**
	 * Code test 1 : the StringToLong as a single function.
	 * @param input source string to be parsed - allow lead/trailing space but otherwise must be a legitimate value.
	 * @return -1 if the number is illegal (because test cannot throw exception - a restriction in question).
	 * Normally this is bad practice - we want to throw an exception if the input is bad.
	 * LIMITATIONs:
	 * 1. We return a -1 if there are any errors.
	 * 2. May not handle the minimum long possible (it would overflow). 
	 */
	public static long StringToLong( String input ) {
		String source = (input == null ? "" : input).trim();
		int index = 0;
		int sign = 0;           // Allow for + and -
		boolean ok = false;     // Whether we have detected valid numeric input
		long result = 0;
		
		while ( index < source.length() ) {
			int ch = source.charAt( index++ );
			switch ( ch ) {
			case '-': 
			case '+': 
				if ( sign != 0 ) {
					return failResult( "Multiple +/- symbols" );
				}
				if ( ok ) {
					return failResult( "Sign must be prefix" );
				}
				sign = ch == '+' ? 1 : -1;
				break;
			default: 
				if ( '0' <= ch && ch <= '9' ) {
					ok = true;
					result = result * 10 + ( ch - '0' );
				} else {
					return failResult( "Non numeric character" );
				}
				if ( result < 0 ) {
					return failResult( "Overflow - number too large for a long!" );
				}
			}
		}
		if ( !ok ) return failResult( "No valid numeric data" );
		return ( sign < 0 ? -result : result );
	}

	private static long failResult( String description ) {
		System.out.println( description );
		return -1;
	}

	
	private void runTest( String input, long expected ) {
		long j = StringToLong( input );
		if ( j == expected ) {
			if ( expected == -1 ) {
				System.out.println( "FAILURE: cannot convert '" + input + "' to a long (see above)" );
			} else {
				System.out.println( "SUCCESS: converted " + input + " to " + expected + " as expected" );
			}
		} else {
			System.out.println( "CODE ERROR!: failed to convert " + input + " to " + j + " not as expected: " + expected );
		}
	}
	
	@Test 
	public void test123() {
		runTest( "123", 123L );
	}
	
	@Test 
	public void testNeg() {
		runTest( "-1235679802", -1235679802L );
	}
	
	@Test 
	public void testPos() {
		runTest( "+567890123", 567890123L );
	}

	@Test 
	public void testBadSign() {
		runTest( "567+890123", -1L );
	}

	@Test 
	public void testFails() {
		runTest( "Banana", -1L );
	}

	@Test 
	public void testOverflow() {
		runTest( "12312311112222223333333444444444455555555555", -1L );
	}
}
