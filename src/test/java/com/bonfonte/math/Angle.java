package com.bonfonte.math;

public class Angle {
	public Double angle = null;;
	private Double radians = null;

	public Angle() {
		this.radians = new Double( 0 );
	}

	public Angle( double value, boolean asDegrees ) {
		if ( asDegrees ) {
			this.angle = value;
		} else {
			this.radians = value;
		}
	}
	
	public double getRadians() {
		if ( radians == null ) {
			radians = toRadians( angle );
		}
		return radians;
	}

	public static double toRadians( double r ) {
		if ( r == 0 ) return 0;
		return r / 180 * Math.PI;
	}
	
	public static double toDegrees( double r ) {
		if ( r == 0 ) return 0;
		return r * 180 / Math.PI;
	}
	
	public double getDegrees() {
		if ( angle == null ) {
			angle = toDegrees( radians );
		}
		return angle;
	}

	public void setDegrees(double r) {
		this.angle = r;
		this.radians = null;
	}
	
	public void setRadians(double radians) {
		this.radians = radians;
		this.angle = null;
	}
	
	public Angle copy() {
		if ( this.angle == null ) {
			return new Angle( radians, false );
		} else {
			return new Angle( angle, true );
		}
	}
}
