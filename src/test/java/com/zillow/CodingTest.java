package com.zillow;

import org.junit.Test;

public class CodingTest {

	public static class StringParse {
		private int pos = 0;   // Current position within string.
		private int leng;      // Total length of string
		private int ch;        // Current character (as an int)
		private String source; // Original source - suitably trimmed.
		private String error;
		private static final int EOF = -1;
		public StringParse( String source ) {
			init( source );
		}
		
		private void init( String source ) {
			this.source = ( source == null ) ? "" : source;
			this.source = source.trim();
			this.leng = source.length();
			this.error = null;
		}
		
		private int nextChar() {
			if ( pos < leng ) ch = source.charAt( pos++ );
			else ch = EOF;
			return ch;
		}
		
		private void raiseError( String description ) throws Exception {
			StringBuilder sb = new StringBuilder( description );
			sb.append( " encountered at position " ).append( pos ).append( " within [" ) .append( source ).append( "]" );
			error = sb.toString();
			log( error );
			throw new Exception( error );
		}
		
		public String getLastError() {
			return error;
		}
		
		public long toLong() throws Exception {
			long result = 0;
			int sign = 0;
			boolean ok = false;
			
			while ( nextChar() != EOF ) {
				switch ( ch ) {
				case '-': 
				case '+': 
					if ( sign != 0 ) {
						raiseError( "Multiple +/- symbols" );
					}
					sign = ch == '+' ? 1 : -1;
					break;
				default: 
					if ( '0' <= ch && ch <= '9' ) {
						ok = true;
						result = result * 10 + ( ch - '0' );
					} else {
						raiseError( "Non numeric character" );
					}
					if ( result < 0 ) {
						raiseError( "Overflow - number cannot fit into a long!" );
					}
				}
			}
			
			if ( !ok ) {
				raiseError( "No numeric data found" );
			}
			return sign >= 0 ? result : -result;
		}
	}

	private static void log( String message ) {
		System.out.println( message );
	}
	
	/**
	 * Simple string to long converter accepting "[+/-]nnnnn" where n are digits.
	 * @param input which can have leading or trailing spaces.
	 * @return a long value or 0 if the input is valid.  
	 */
	public static long StringToLong( String input ) {
		StringParse parser = new StringParse( input );
		try {
			return parser.toLong();
		} catch ( Exception e ) {
			return 0;
		}
	}
	
	private void assertTrue( String text, boolean res ) {
		log( ( res ? "SUCCESS: " : "FAILURE: " ) + text );
	}
	
	private void runTest( String input, long expected ) {
		long result = StringToLong( input );
		assertTrue( "StringToLong(" + input + ") => expected result " + expected + " got result " + result,  result == expected );
	}
	
	@Test
	public void testSimple123() {
		runTest( "123", 123L );
	}

	@Test
	public void testSimpleNegative() {
		runTest( "-987654321", -987654321L );
	}

	@Test
	public void testSimplePositive() {
		runTest( "+987654321", 987654321L );
	}

	@Test
	public void testBadInput() {
		runTest( "1 42345", 0L );
	}

	
}
