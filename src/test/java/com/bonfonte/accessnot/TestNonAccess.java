package com.bonfonte.accessnot;


import org.junit.Test;

import com.bonfonte.access.Access;

public class TestNonAccess {

	@Test 
	public void testAccess() throws Exception {
		Access access = new Access();
		System.out.println( "Accessing private   - only by public method to do so: " + access.getPrivateString() );
		System.out.println( "Accessing public    - " + access.publicString );
		System.out.println( "Accessing protected - No Can Do!!" );
		System.out.println( "Accessing default   - No Can Do!!" );
	}

	@Test 
	public void testInheritedAccess() throws Exception {
		InheritedAccess access = new InheritedAccess();
		System.out.println( "Accessing private   - only by public method to do so: " + access.getPrivateString() );
		System.out.println( "Accessing public    - " + access.publicString );
		System.out.println( "Accessing protected - No Can Do!!" );
		System.out.println( "Accessing default   - No Can Do!!" );
	}

}
