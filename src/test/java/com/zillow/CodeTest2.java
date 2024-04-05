package com.zillow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class CodeTest2 {

	/**
	 * Trinary class to support coding question of tree structure with 3 nodes.
	 * We do not really need a middle "tree" or we could implement that using a list - but
	 * in the spirit of the original question this class uses 3 Trinary children : l=left, m=middle, r=right.
	 * @author andyt
	 */
	public static class Trinary {
		Trinary l; // Left   (<) child tree
		Trinary m; // Middle (=) child tree
		Trinary r; // Right  (>) child tree
		int n; // Value at this node	
		
		public Trinary( int n ) {
			this.n = n;
		}
		
		/**
		 * Recursive method to create a new child if necessary or push the value into the subtree. 
		 * @param t tree in which we are trying to add a value - if null then create it.
		 * @param value to be added.
		 * @return the tree - a valid tree holding the value.
		 */
		private Trinary add( Trinary t, int value ) {
			if ( t == null ) t = new Trinary( value );
			else t.add( value );
			return t;
		}
		
		
	    /**
	     * Part of the API : method to add a value into this tree.
	     * @param value
	     */
	    public void add( int value ) {
	    	if ( value < n ) {
	    		this.l = add( this.l, value );
	    	} else
	    	if ( value > n ) {
	    		this.r = add( this.r, value );
	    	} else {
	    		this.m = add( this.m, value );
	    	}
	    }

	    /**
	     * Part of the API : method to delete an element from the tree.
	     * @param value to be deleted.
	     * @return the new tree after deleting that element;
	     */
	    public Trinary delete( int value ) {
	        if ( value < n ) {
	        	this.l = delete( this.l, value );
	        } else 
	        if ( value > n ) {
	        	this.r = delete( this.r, value );
	        } else {
	        	if ( this.m == null ) {
	        		// This node must be removed from the tree:
	        		// If there is no right node means we can use the left branch
	        		if ( this.r == null ) return this.l;
	        		// If there is no left node (ditto)
	        		if ( this.l == null ) return this.r;
	        		// Ok - we need to reconstruct this sub tree.
	        		Trinary right = this.r;
	        		Trinary newroot = promotePredecessor( this.l );
	        		newroot.r = right;
	        		return newroot;
	        	} else {
	        		// We promote the 2nd child - losing the first.
	        		this.m = this.m.m;
	        	}
	        }
        	return this;	    
        }

	    
	    /**
	     * Part of deletion process - promote the predecessor (rightmost node) to the head of this tree so that all its children are left of it.
	     * This is called when we find that the deletion of a value requires removing a node which has children.
	     * @param tree whose root node is being deleted.
	     * @return the rearranged tree.
	     */
	    private Trinary promotePredecessor( Trinary tree ) {
	    	 Trinary origin = tree;
	    	 Trinary parent = null;
	    	 while ( tree != null ) {
	    		 if ( tree.r == null ) {
	    			 // Bingo - we found the right most node.
	    			 if ( parent != null ) {
	    				 // Then we have descended the tree. 
	    				 // So the attach left branch to parent
	    				 parent.r = tree.l;
	    				 // .. and glue the original root as the left subtree: 
	    				 tree.l = origin;
	    			 }
	    			 return tree;
	    		 } else {
	    			 // Keep walking
	    			 parent = tree;
	    		     tree = tree.r;
	    		 }
	    	 }
	    	 return null;
	    }
	    
	    private Trinary delete( Trinary t, int value ) {
	    	if ( t == null ) {
	    		// Then value is not in the original tree.
	    		return null;
	    	} else {
	    		// Ok recurse and remove that value from the subtree 
	    		return t.delete( value );
	    	}
	    }
	    
	    private String indent( String padding, int depth ) {
	    	StringBuilder sb = new StringBuilder( "\n" );
	    	for ( int i = 0; i < depth; i++ ) sb.append( padding );
	    	return sb.toString();
	    }
	    
	    private void show( String name, StringBuilder sb, int depth ) {
	    	if ( l != null ) l.show( ".- Left", sb, depth + 1 );
	        sb.append( indent( "  ", depth ) );
	        sb.append( name ).append( " Values=[" ).append( this.n );
	    	Trinary child = this.m;
	    	while ( child != null ) {
	    		sb.append( "," ) .append( child.n );
	    		child = child.m;
	    	}
	    	sb.append( "]" );
	    	if ( r != null ) r.show( "`-Right", sb, depth + 1 );
	    }
	    
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	    	show( "Root", sb, 0 );
	    	return sb.toString();
	    }	

	    // Methods to extract a flat list of values:
	    private void addToList( List<Integer> list, Trinary t ) {
	    	if ( t != null ) t.toList( list );
	    }
	    
	    private void toList( List<Integer> list ) {
	    	addToList( list, this.l );
	    	list.add( n );
	    	addToList( list, this.m );
	    	addToList( list, this.r );
	    }
	    
	    public List<Integer> toList() {
	    	List<Integer> list = new ArrayList<Integer>();
	    	toList( list );
	    	return list;
	    }
    }

	@Test
	public void testBuildTrinary() throws Exception {
		int[] values = { 5, 4, 9, 5, 7, 2, 2 }; // Initial values to populate tree
		int[] toAdd  = { 6, 8, 3, 3, 0, 77 };   // Values we will continue to add
		int[] toDel  = { 5, 5, 2, 4, 2, 6, 1};  // Values we will delete
		int[] result = { 0, 3, 3, 7, 8, 9, 77 }; // Expected final result
		Trinary root = null;
		StringBuilder errmsgs = new StringBuilder();
		
		for ( int value : values )  {
			if ( root == null ) {
				root = new Trinary( value );
			} else {
				root.add( value );
			}
		}
	
		System.out.println( "Here is the tree after adding all initial values:" + root.toString() );

		for (int index = 0; ; index++) {
			// Iterate over values to add and delete - checking the new state of the tree each time
			int value = -1;
			if ( index < toAdd.length ) {
				value = toAdd[ index ];
				root.add( value );
				System.out.println( "\nHere is the tree after adding value " + value + " -" + root.toString() );
			} 
			if ( index < toDel.length ) {
				value = toDel[ index ];
				int currentRootValue = root.n;
				root = root.delete( value );
				System.out.println( "\nHere is the tree after deleting value " + value + " -" + 
				    ( ( root != null && root.n != currentRootValue ) ? 
					    " (note root has now changed)" : "" ) + root.toString() );
			}
			if ( value == -1 || root == null ) break;
		}
		
		if ( root == null ) {
			errmsgs.append( "Root is null - this is unexpected!" );
			System.out.println(  errmsgs.toString() );
			throw new Exception( errmsgs.toString() );
		}
		
		System.out.println( "Check the final order of elements: " );
		List<Integer> list = root.toList();
		if ( list.size() != result.length ) {
			errmsgs.append( "\nFAILURE: tree size " + list.size() +  " does not match expected " + result.length );
		} 
		
		for ( int index = 0; index < list.size(); index++ ) {
			int value = list.get( index );
			if ( index < result.length) {
				if ( value == result[ index ] ) {
					System.out.println( "SUCCESS: tree has value " + value + " at position " + index );
				} else {
					errmsgs.append( "\nFAILURE: tree has unexpected value " + value +
							" at position " + index + " : expected " + result[index]);
				}
			}
		}
		if ( errmsgs.length() > 0 ) {
			System.out.println( "There were errors in the tree:" + errmsgs.toString() );
			throw new Exception( errmsgs.toString() );
		}
	}
	
	
}
