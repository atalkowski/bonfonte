package com.bonfonte;

import org.junit.Test;
import com.bonfonte.experiment.alpha.Alpha;

public class TestAlpha{

	private static void log( String s ) {
		System.out.println( s );
	}

	private void testRun( String data, String coder, String expected ) throws Exception  {
		try {
			Alpha a = new Alpha( coder );
			String c = a.encode( data );
			String res = " encoding [" + data + "] with [" + coder + "] expecting " + expected + " got " + c;
			if ( expected.equals( c ) ) {
				log( "Success" + res );
			} else {
				throw new Exception( "Failed " + res );
			}
		} catch ( Exception e ) {
			log( "Unexpected exception " + e.getMessage() );
			throw e;
		}
	}
	
	@Test
	public void test1() throws Exception {
		testRun( "Hello World", "3l5m", "rldsom48" );
	}

	@Test
	public void test2() throws Exception {
		testRun( "Hello World", "3l8m", "rldsom48som" );
	}

	@Test
	public void test3() throws Exception {
		testRun( "Hello World", "3aGl", "smeOte" );
	}

	@Test
	public void test4() throws Exception {
		testRun( "Hello World", "3aGnl", "smeOte05" );
	}

	@Test
	public void test5() throws Exception {
		testRun( "Hello World", "aAafFfNl", "sMeoCi87" );
	}
	
	@Test
	public void test6() throws Exception {
		testRun( "abcdef", "3aGnp", "cdeCho06" );
	}
	
	@Test
	public void test7() throws Exception {
		testRun( "abcdef", "3aGNp", "cdeCho93" );
	}

	@Test
	public void test8() throws Exception {
		testRun( "abcdef", "10Fl", "ECOLRHECOL" );
	}

	@Test
	public void test9() throws Exception {
		testRun( "abcdef", "4gl", "eltchooxtlph" );
	}


}
