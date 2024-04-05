package com.bonfonte.sort;

public class ChopLeaf extends Chopex {
	public String[] nodes;
	public ChopLeaf() {
		nodes = new String[ size ];
	}
	
	public int locate( String s ) {
		if ( last < 0 ) return 0; // No nodes in this item - will populate cell at 0!
		int lo = 0;
		int hi = last;
		int c = 0;
		while ( lo <= hi ) {
			int mid = chop( lo, hi );
			c =  s.compareTo( nodes[ mid ] );
			if ( c == 0 ) return mid;
			if ( c < 0 )  hi = mid - 1;
			else lo = mid + 1;
		}
        if ( c < 0 ) return hi+1;
        return lo;
	}
	
	
	public void insert( String s, int pos ) {
		last = last + 1;
		String[] source = nodes;
		if ( last >= size ) {
			int newSize = size + GROWSIZE;
			nodes = new String[ newSize ];
			size = newSize;
			// Copy all nodes below the pos:
			for ( int i = 0; i < pos; i++ ) {
				nodes[i] = source[i];
			}
		}
		// Make room for the incoming node
		// by shunting original source up by one
		for ( int i = last; i > pos; i-- ) {
			nodes[i] = source[ i-1 ];
		}
		// Finally we can put the new node in place
		nodes[ pos ] = s;
	}
	
	public String[] getNodes() {
		String[] result = new String[ last + 1 ];
		for ( int i = 0; i <= last; i++ ) {
			result[ i ] = nodes[ i ];
		}
		return result; 
	}
	
	public String show() {
		StringBuilder sb = new StringBuilder( "[");
        String dlm = "";		
		for ( String s : getNodes() ) {
			sb.append( dlm ).append( s );
			dlm = ",";
		}
		return sb.append( "]" ).toString();
	}
}
