package com.bonfonte.math;

public class Polar {
	public double r = 0;
	public Angle a = new Angle();
	public Polar() {
	}
	
	public Polar( Number r ) {
		this.r = r.doubleValue();
	}
	
	public Polar( Number r, Angle a ) {
		this.r = r.doubleValue();
		this.a = a.copy();
	}

	public Polar( int r, int degrees ) {
		this.r = r;
		Angle a = new Angle( degrees, true );
	}
	
	public Complex toComplex() {
	    if ( r == 0 || a.getDegrees() == 0  ) {
	    	return new Complex( r, 0 );
	    }
	    return new Complex( r * Math.sin( a.getRadians() ), r * Math.cos( a.getRadians() ) );
	}
}
