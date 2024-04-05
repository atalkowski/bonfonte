package com.atypon;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Atypon {
    /* 
     * This test was about checking compile time constraints for generics.
     * Type Erasure was the topic. 
     * The type erasure is done at compile time and ensure that affected code is type safe.
     * The "erasure" means that, for example, List<Integer> is converted to List at compile time
     * and any call add( x ) will get converted to add( (Integer)x ) 
     * Also the attempt to add a float to the list is not allowed because you cannot do ((Integer) 3.4)
     */
	@Test
	public void checkOut() {
		List<Integer> ints = new ArrayList<Integer>();
		List<? extends Number> nums = ints;
		List<Number> xnums = new ArrayList<Number>();
		Integer x = new Integer( 1 );
		// Neither of these is allowed:
		// nums.add( new Integer( 3 ) );
		// nums.add( x );
		ints.add( new Integer( 3 ) );
		ints.add( x );
		ints.add( 0 );
		ints.add( (Integer) 2 );
		
		xnums.add( x );
		xnums.add( 3.4 );
		
		print1( "ints", ints );
		print1( "nums", nums );
		// Neither of these is allowed:
		// print2( "ints", ints );
		// print2( "nums", nums );
		print2( "xnums", xnums );

		print3( "ints", ints );
		print3( "nums", nums );
		print3( "xnums", xnums );
    }
	
	private void print1( String name, List<? extends Number> numbs ) {
		System.out.println( "This is print1" );
		for ( Number n : numbs ) {
			System.out.println( name + " number = " + n );
		}
	}

	private void print2( String name, List<Number> numbs ) {
		for ( Number n : numbs ) {
			System.out.println( name + " number = " + n );
		}
	}

	private void print3( String name, List numbs ) {
		System.out.println( "This is print3:" );
		for ( Object n : numbs ) {
			System.out.println( name + " number = " + ((Number)n) );
		}
	}

}
