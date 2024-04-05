package com.bonfonte;

import org.junit.Test;

import com.bonfonte.math.Complex;

public class TestComplex {
	private static final double near0 = 0.0000000001;
	public static final double root2 = Math.sqrt( 2 );
	
	private static void log( String s ) {
		System.out.println( s );
	}

	private boolean near( Double a, Double b ) {
		double diff = a - b;
		if ( diff < 0 ) diff = -diff;
		return diff <= near0;
	}
	
	private void checkEqual( String expression, double d1, double d2 ) throws Exception {
		if ( near( d1, d2 ) ) return;
		String errmsg = "Failed " + expression + ": " + d1 + " v " + d2;
		log( errmsg );
		throw new Exception( errmsg );
	}
	
	@Test
	public void testComplex1() throws Exception {
		Complex i1 = new Complex( 1, 1 );
		Complex i1sq = i1.mult( i1 );
		checkEqual( "(1,1) * (1,1) -> re 0 and im root 2", i1sq.re, 0 );
		checkEqual( "(1,1) * (1,1) -> re 0 and im root 2", i1sq.im, 2 );
	}
}
