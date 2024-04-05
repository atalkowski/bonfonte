package com.bonfonte.sort;

public abstract class Chopex {
	public static final int MINSIZE = 4;
	public static final int GROWSIZE = 3;
	protected int size;
    protected int last;
    
    public Chopex() {
    	init( MINSIZE );
    }
    
    private void init( int size ) {
    	this.size = size;
    	this.last = -1;
    }

    
    /**
     * We want chop to iterate over the items 0 to last -1 in a binary chop order to locate the node <= our given key.
     * The items visited depend on the comparison between the keys at each node.
     * For example suppose there are 4 keys with values A C E F at offsets 0, 1, 2 and 3.
     * If we want to find the position of D within that list, we start by looking at node = chop( 0, 4 ) = 2 -> E
     * D is less than E so we seek node = chop( 0, 1 ) = 0 -> A.
     * D is greater than A so we seek node chop ( 1, 1 ) = 1
     * @param lo lower bound of range to search
     * @param hi upper bound of range to search
     * @return midpoint between lo and hi
     */
    public int chop( int lo, int hi ) {
		return ( lo + hi )/2;	
	}
}
