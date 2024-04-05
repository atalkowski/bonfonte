package com.zillow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class CodeTest3 {
	/**
	 * Bitmap will initialize byte array to cover bit positions for the range (base, top-1).
	 * Used as part of the BitBucket which will hold an array of these objects.
	 * @author andyt
	 *
	 */
	private static class BitMap { 
		private int count; // Count of elements actually added
		private int span;  // Count of possible elements
		private int base;  // This number is stored at position 0 in the bitmap
		private int top;   // This is the upperbound of the bitmap (> maximum allowed number)  
		private byte[] bytes; // Contains the basic bits;
		private int maxByte;  // Number of bytes required to support this bitmap 
		/**
		 * @param base the lowest member of the bitmap at bit 1.
		 * @param top the upperbound of the bitmap, it is greater than any value that can be stored.
		 */
		public BitMap( int base, int top ) {
			init( base, top );
		}
		
		private void init( int base, int top ) {
            this.base = base;
            this.top = top;
			this.count = 0;
			this.span = top - base;
			int finalByte = span & 7;
			this.maxByte = span >> 3; // Number of bytes needed to process this span  
			// Add an extra byte if the span doesn't exactly divide by 8, or is 0
			if ( finalByte != 0 || maxByte == 0 ) {  
				maxByte++;
			}
			this.bytes = new byte[ maxByte ];
			for ( int i = 0; i < maxByte; i++ ) {
				bytes[i] = 0;
			}
//			System.out.println( "Initialized bitmap spanning " + base + " to " + top + " requires " + maxByte + " bytes" );
		}
		
		public void add( int n ) {
			if ( !( base <= n && n < top)) return;
			int relative = n - base ;
			int offset = relative >> 3; // n / 8 the fast way
			int shift = ( relative & 7 );
			int bit = 1 << shift;
			if ( ( bytes[offset] & bit ) == 0 ) {
				count++;
				bytes[offset] |= bit;
			}
		}

		public void delete( int n ) {
			if ( !( base <= n && n < top)) return;
			int relative = n - base ;
			int offset = relative >> 3; // n / 8 the fast way
			int shift = ( relative & 7 );
			int bit = 1 << shift;
			if ( ( bytes[offset] & bit ) > 0 ) {
				count--;
				bytes[offset] &= ~bit;
			}
		}
		
		public int listMissing( List<Integer>list ) {
			if ( count >= span ) {
				// If none are missing (as is likely in the question) then nothing to add
				return 0;
			}
			int c = 0;
			int n = base;
			for ( int i = 0; i < maxByte; i++ ) {
				byte b = bytes[i];
				if ( b != 0xFF ) {
					int bit = 1;
					while ( bit < 0x100 ) {
						if ( n >= top ) break;
						if ( ( bit & b ) == 0 ) {
							c++;
							list.add( n );
						}
						bit = bit << 1;
						n++;
					}
				}
			}
			return c;
		}
	}
	
	private static class BitBucket {
		int top;
		int mapCount;
		int added = 0;
	
		BitMap[] maps;
		private static final int BITMAP_SPAN = 4 * 1024; // 4K
		private static final int BITMAP_BITS = 12; // 2 ^ 12 = 4K
		
		public BitBucket( int top ) {
			init( top );
		}

		private void init( int top ) {
			this.top = top;
			this.mapCount = top / BITMAP_SPAN + 1; // 
			int base = 0;
			int index = 0;
			maps = new BitMap[ mapCount ];
			while ( base < top ) {
				int next = base + BITMAP_SPAN;
				if ( next > top ) {
					next = top;
				}
				maps[index++] = new BitMap( base, next );
				base = next;
			}
			System.out.println( "Created a bitbucket of " + mapCount + " bitmaps covering 0 to " + top );
		}
		
		public void add( int n ) {
			if ( n < top ) {
				added++;
				int index = n >> BITMAP_BITS;
				maps[index].add( n );		
			}
		}

		public void delete( int n ) {
			if ( n < top ) {
				added--;
				int index = n >> BITMAP_BITS;
				maps[index].delete( n );		
			}
		}
		
		public List<Integer> listMissing() {
			List<Integer> list = new ArrayList<Integer>();
			for ( int i = 0; i < mapCount; i++ ) { 
				maps[i].listMissing( list );
			}
			return list;
		}
    } 

	private void testBitMap( int size, Integer... holes ) throws Exception {
		long currentTime = System.currentTimeMillis();
		BitBucket bb = new BitBucket( size );
		for ( int i = 0; i < size; i++ ) {
			bb.add( i );
		}
		List<Integer> check = new ArrayList<Integer>();
		// This is simulating the missing numbers
		for ( Integer hole : holes ) {
			bb.delete( hole );
			check.add( hole );
		}
		List<Integer> result = bb.listMissing();
		assertTrue( "List size " + result.size() + " should match " + check.size(), check.size() == result.size() );
		for ( int index = 0; index < result.size(); index++ ) {
			assertTrue( "List element [" + index + "] should match "
		       + holes[index] + "; it is " + result.get(index), 
			   result.get( index ).intValue() == holes[index] );
			System.out.println( "Successful match at offset " + index + " = " + holes[index] );
		}
		long finalTime = System.currentTimeMillis() - currentTime;
		int added = bb.added;
		int expected = size - holes.length;
		
		System.out.println( "Total time for " + size + " test : " + finalTime + " msecs; added total=" + added );
		assertTrue( "Total added was " + added, added == expected );
	}

	private void assertTrue(String desc, Boolean flag) {
		Assert.assertTrue(desc, flag);
	}
	
	@Test 
	public void testTinyBitMap() throws Exception {
		testBitMap( 6, 1, 3 );
	}

	
	@Test 
	public void testSmallBitMap() throws Exception {
		testBitMap( 40, 3, 19, 32 );
	}
	
	@Test 
	public void testMediumBitMap() throws Exception {
		testBitMap( 14040, 1003, 3589, 8999 );
	}

	@Test 
	public void testLargeBitMap() throws Exception {
		testBitMap( 4345670, 3000, 113589, 3448999 );
	}

	@Test 
	public void testXLargeBitMap() throws Exception {
		testBitMap( 14345670, 13000, 2113589, 13448999 );
	}

	@Test 
	public void testXXXLargeBitMap() throws Exception {
		testBitMap( 400000000, 130000, 21135890, 134489990 );
	}
	
	
}
