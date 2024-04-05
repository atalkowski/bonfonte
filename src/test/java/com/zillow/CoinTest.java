package com.zillow;

import org.junit.Assert;
import org.junit.Test;

public class CoinTest {
    private static final int[] coins = { 100, 50, 25, 10, 5, 1 };

    private static int compare( int a, int b ) {
		return a < b ? -1 : ( a > b ? 1 : 0 );
	}
	/**
	 * Method to compute the number of ways we can compute a target monetary value by using any combination of coins.
	 * Basically our algorithm will work by iterating over each coin value as a "starting" coin. 
	 * We loop over the list of coins from 100 (dollar), 50, 25, 10 (dime), 5 (nickel) and 1 cent each as a "starting" coin.
	 * In EACH iteration of with that starting coin we have a nested loop:
	 * We initialize a total = 0 and add the starting coin  - each time comparing the total with the target value
	 * At each nested iteration we compare the current total with the target - and only 3 things can happen: 
	 *  1. our total > target - this means we have exhausted the possibilities with this coin as the starting coin
	 *  2. our total = target - same as above - we are done - but we have 1 new way to hit the target - so increment "ways".
	 *  In both the above we move to the next coin.
	 *  3. our total < the value - in which case we are short of our target by an amount = (target - total)
	 *    Before continuing with the current coin we now recurse:
	 *    - find all new ways that we can make change for this remaining amount but using ONLY the smaller coins.
	 *    - the critical call is this recursive call:
	 *      ways += compute( target - total, index + 1 );   
	 * Note: It would be a mistake to include the starting coin in the recursion call - it is being counted in this call.
	 * We would count certain permutations twice if we did that.
	 * For example using compute( 100, 0 ) we are computing target of 1 dollar using ALL coins (i.e from index 0).
	 * starting = 100 (and index = 0 => using all coins):
	 *   total=100  # case 2 : one new way to hit target and we are done with the 100 coin so we move on to the next coin 50
	 *   ways += 1
	 * starting = 50 (and index = 1 using 50 cents and down)
	 *   total = 50 # case 3: less than target by 50 cents so invoke step 3:
	 *              # (compute all way we can fulfill 50 cents using smaller coins):
	 *   ways += compute( 50, 2 ) // Position 1 means using quarters, dimes, nickels and cents.
	 *              # this would add 49 new ways
	 *   total = 50+50 # target reached so add 1 new way to fulfill target - move to next coin (quarter)
	 *   ways += 1
	 * starting = 25 (and index = 2 using quarters and below):
	 *   total = 25 # (case 3 : less than target by 75 cents, invoke step 3
	 *   ways += compute( 75, 3 )   
	 *   total = 50 # case 3 again
	 *   ways += compute( 50, 3 )
	 * etc  
     * @param target - the target sum we are trying to provide change for
	 * @param fromIndex - index to coins array - we can select on;y coins at or above this index (0 means all)
	 * @return the number of ways you ca 
	 */
	public static int compute( int target, int fromIndex ) {
		if ( target < 0 ) return 0;
		if ( fromIndex == coins.length - 1 ) return 1;
		int ways = 0;
		
		for ( int index = fromIndex; index < coins.length; index++ ) {
			int total = coins[ index ];
			for (;;) {
				int comp = compare( total, target );
				if ( comp > 0 ) break; // We are done this index;
				if ( comp == 0 ) {
					ways = ways + 1;
					break;
				}
				// First compute all the ways to provide change for remnant using smaller coinage
				ways += compute( target - total, index + 1 );  
				total += coins[ index ];
			} 
		}
		return ways;
	}
	
	private void testCoins( int value, int expectedWays ) throws Exception {
		int ways = compute( value, 0 );
		Assert.assertTrue(
				"To give change for " + value + " expected " + expectedWays + " -> computed " + ways,
				expectedWays == ways);
	}

	@Test 
	public void testZero() throws Exception {
		testCoins( 0, 0 ); 
	}

	@Test 
	public void testUptoANickel() throws Exception {
		int i = 1;
		while ( i < 5 ) {
			testCoins( i, 1 ); 
			i++;
		}
	}

	@Test 
	public void testNickelTo9() throws Exception {
		int i = 5;
		while ( i < 10 ) {
			testCoins( i, 2 ); 
			i++;
		}
	}
	
	@Test
	public void testDimeTo14() throws Exception {
		int i = 10;
		while ( i < 15 ) {
			testCoins( i, 4 ); 
			i++;
		}
	}
	
	@Test
	public void test15To19() throws Exception {
		int i = 15;
		while ( i < 20 ) {
			testCoins( i, 6 ); 
			i++;
		}
	}
	
	@Test
	public void test20To24() throws Exception {
		int i = 20;
		while ( i < 25 ) {
			testCoins( i, 9 ); 
			i++;
		}
		System.out.println( "Total ways to compute the 100 is " + compute( 100, 0 ) );
		System.out.println( "Total ways to compute the 50 using quarters or below: " + compute( 50, 2 ) );
		System.out.println( "Total ways to compute the 25 using quarters or below: " + compute( 25, 2 ) );
		System.out.println( "Total ways to compute the 25 using dimes or below: " + compute( 25, 3 ) );
	}

	
	
	
}
