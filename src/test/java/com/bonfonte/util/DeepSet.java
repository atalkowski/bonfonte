package com.bonfonte.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DeepSet<T>{
	Set<T> set;
	int max;
	int reduceBy;

	public DeepSet(int max){
		this.max = max < 10 ? 10 : max;
		this.reduceBy = this.max/10;
		this.set = new LinkedHashSet<>();
	}

	public void add(T value){
		if(set.size() >= max){
			Iterator<T> it = this.set.iterator();
			int index = 0;
			// Do not remove the early stuff
			while(index++ < reduceBy){
				it.next();
			}
			// Remove every other item between 10% and 30%
			index = 0;
			while(index++ < reduceBy){
				it.next();
				it.next();
				it.remove();
			}
		}
		set.add(value);
	}

	public boolean contains(T value){
		return set.contains(value);
	}
}
