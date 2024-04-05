package com.bonfonte.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class B extends A {
	private static int index = 0;
	private int value;
	public B() {
		setValue(index++);
	}
	
	@Override
	public A doIt() throws IOException, NumberFormatException {
	    B res = new B();
	    return res;
	}

	@Override
	public List<? extends A> doThese() {
		List< B > blist = new ArrayList<B>();
		blist.add( new B() );
		blist.add( new B() );
		return blist;
	}

	@Override
	public List<A> doThose() {
		List<A> alist = new ArrayList<A>();
		alist.add( new B() );
		alist.add( new B() );
		return alist;
	}

	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
