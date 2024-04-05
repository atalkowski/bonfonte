package com.bonfonte.access;

import org.junit.Test;

public class TestAccess {

	@Test 
	public void testAccess() throws Exception {
		Access access = new Access();
		System.out.println( "Accessing private   - only by public method to do so: " + access.getPrivateString() );
		System.out.println( "Accessing public    - " + access.publicString );
		System.out.println( "Accessing protected - " + access.protectedString );
		System.out.println( "Accessing default   - " + access.defaultString );
	}
}
