package com.bonfonte.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Sample1 {

	static List<String> listString( String input ) {
		return enumerate( input, 0 );		
	}
	
	// Recursive version
	static List<String> enumerate( String a, int pos ) {
		List<String> res = new ArrayList<String>();
		if ( pos >= a.length() ) {
			return res;
		}
		String prefix = a.substring(pos);
		List<String> children = enumerate( a, pos + 1 );

		if ( children.size() == 0 ) {
	    	res.add( a.substring( pos ).toUpperCase() );
	    	res.add( a.substring( pos ).toLowerCase() );
        } else {
        	for  ( String child : children ) {
        		// BC, Bc, bC, bc
        		res.add(  a.substring( pos, pos+1 ).toUpperCase() + child );
        		res.add(  a.substring( pos, pos+1 ).toLowerCase() + child );
        	}
        }
		return res;
	}
	
    // Non recursive version:
	static List<String> enum2( String a ) {
		List<String> res = new ArrayList<String>();
		int pos = 0;
		
		if ( a != null && a.length() > 0 ) {
			String uc = a.toUpperCase();
			String lc = a.toLowerCase();
			
			int elements = 2;
			for ( int i = 1 ; i < a.length(); i++ ) {
				elements <<= 1;
			}
			for ( int i = 0; i < elements; i++ ) {
				StringBuilder nth = new StringBuilder();
				int caseN = i;
				for ( int j = 0; j < a.length(); j++ ) {
				    if ( (caseN & 1) > 0 ) {
				    	nth.append( uc.charAt(j) );
				    } else {
				    	nth.append( lc.charAt(j) );
				    }
				    caseN >>= 1;
				}
				res.add( nth.toString() );
			}
		}
		return res;
	}
	
	public void rmRF( File file ) {
		if ( file.exists() ) {
			if ( file.isDirectory() ) {
				File[] nodes = file.listFiles();
				for ( File node : nodes ) {
					rmRF( node );
				}
			}
			file.delete();
		}
	}
	
	public void rmRFIt( File file ) {
		Vector<File> stack = new Vector<File>();

		while ( file != null && file.exists() ) {
			if ( file.isDirectory() && !(file.listFiles().length > 0) ) {
				File[] nodes = file.listFiles();
				stack.add( file );
				for ( File node : nodes ) {
					if ( node.isDirectory() && !(node.listFiles().length > 0)) {
						stack.add( node );
					} else {
						node.delete();
					}
				}
			} else {
				file.delete();
			}
			if ( stack.size() > 0) {
				file = stack.elementAt(stack.size() - 1);
				stack.remove(stack.size() - 1);
			} else {
				break;
			}
		}
	}
	
	private static List<String> showList( int option, String s ) {
		String msg = option == 0 ? "Recursive" : "Iterative";
		System.out.println( "Show list for " + msg );
		List<String> r1 = null;
		Date d = new Date();
		if ( option == 0 ) {
			r1 = listString( s );
		} else {
			r1 = enum2( s );
		}
		Date now = new Date();
		
		for ( int i = 0; i < r1.size(); i ++ ) {
			System.out.println( i + " -> " + r1.get( i ));
		}
		long t = now.getTime() - d.getTime();
		System.out.println( "Total time for " + msg + " was " + t + " msecs" );
		return r1;
	}

	private static void testEnumerate( String s ) {
	    showList( 0, s );
	    showList( 1, s );
	}
	
	public static void main( String[] args ) {
	   	testEnumerate( "AB" );
	    testEnumerate( "ABC" );
	    //testEnumerate( "ABCdefgHIJ" );
	    testLinks();
	    
	}
	
	
	public static LinkedList<String> reverse( LinkedList<String> links ) {
		int i = 0; 
		int last = links.size() - 1;
		
		while ( i < last ) {
			String s = links.get( last );
			links.set( last--, links.get( i ) );
			links.set( i++, s );
		}
		return links;
	}
	
	public static void testLinks() {
		LinkedList<String> linked = new LinkedList<String>();
		List<String> list = showList( 0, "ABC" );
		for ( int i = 0; i < list.size(); i++ ) {
			linked.add( list.get( i ) );
		}
		LinkedList<String> rev = reverse( linked );
		System.out.println( "Reversed list :"  );
		for ( int i = 0; i < rev.size(); i++ ) {
			System.out.println( "Element " + i + ":" + rev.get(i) );
		}
		
	}
	
}
