package com.bonfonte.math;


public class Complex {
    public double re = 0;
    public double im = 0;
    
    public Complex( int re, int im ) {
    	this.re = re;
    	this.im = im;
    }
    
    public Complex( Number re, Number im ) {
    	if ( re != null ) this.re = re.doubleValue();
    	if ( im != null ) this.im = im.doubleValue();    	
    }
    
    public Complex() {
    }
    
    public Complex( Complex c ) {
    	this.re = c.re;
    	this.im = c.im;
    }
    
    public Complex add( Complex c ) {
    	return new Complex( re + c.re, im + c.im );
    }
    
    public Complex conjugate() {
    	return new Complex( re, -im );
    }
    
    public Complex mult( Complex c ) {
    	return new Complex( re * c.re - im * c.im, re * c.im + im * c.re );
    }
    
    public boolean isZero() {
    	return re == 0 && im == 0;
    }
    
    public Polar toPolar() {
    	if ( re == 0 || im == 0 ) {
    		return new Polar();
    	}
     	double radius = Math.sqrt( re * re + im * im );
    	double theta = Math.atan( im / radius );
    	Angle a = new Angle( theta, false );
    	return new Polar( radius, a );
    }
    
    public Complex divide( Complex x ) throws DivBy0Exception {
    	if ( x.isZero() ) throw new DivBy0Exception( "Cannot divide by 0!" );
    	/*
    	 * Essentially (a+bi)/(c+di) = ( ac+bd + i( bc -ad) ) / ( cc + dd ) 
    	 * where 
    	 * a = re   b = im   c = x.re and d = x.im
    	 */
    	
    	double ccdd =  x.re * x.re + x.im * x.im;
    	return new Complex( (re * x.re + im * x.im)/ccdd, 
    			            (im * x.re - re * x.im)/ccdd );
    }
}
