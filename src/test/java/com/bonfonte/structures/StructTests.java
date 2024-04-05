package com.bonfonte.structures;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class StructTests {

	
	private void print( String name, List<String> l ) {
		StringBuilder sb = new StringBuilder( name );
		if ( l != null ) {
			for ( String s : l  ) {
				sb.append( " " );
				sb.append( s );
			}
 		}
		System.out.println( sb.toString() );
	}
	
	@Test
	public void testLinked() {
		LinkedList<String> list = new LinkedList<String>();
	    String[] samples = { "Hello", "World", "will", "run", "a", "few", "tests" };
	    for ( String s : samples ) {
	    	list.add( s );
	    }
	    print( "Initial list   :", list );
	    list.remove( 2 );
	    print( "Removed item @2:", list );
	    list.add( "now" );
	    print( "Append word now:", list );
	    for ( int i = 0; i < list.size(); i++ ) {
	    	String head = list.pop();
	    	list.add( head );
    	    print( "Rolling.......:", list );
	    }
	}
	
}
