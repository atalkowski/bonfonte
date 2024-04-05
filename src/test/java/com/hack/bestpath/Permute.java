package com.hack.bestpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Permute{
	int size;
	int origin = 0;
	int choose = -1;
	int pos;
	List<Integer> last = null;
	Set<String> chosen = new HashSet<>();
	Permute child;
	Permute(int size){
		if(size > 30) {
			throw new RuntimeException("Permute limited to 30");
		}
		this.size = size;
		recycle();
	}

	Permute(int size, int choose){
		this(size);
		this.choose = choose;
	}

	void recycle() {
		this.pos = size-1;
		if(size >= 2) {
			 child = new Permute(size-1);
		}
	}

	boolean rawHasNext() {
		return pos > 0 || 
				(pos == 0 && (
					child == null ||
				    child.hasNext()));
	}
	
	boolean hasNext(){
		if(last != null) return true;
		boolean res = rawHasNext();
		if(!res) {
			return false;
		}
		while(choose > 0 && res) {
			List<Integer> nx = next().stream().limit(choose)
					.collect(Collectors.toList());
			String nxs = nx.toString();
			if(chosen.add(nxs)) {
				last = nx;
				return true;
			}
    		res = rawHasNext();
		}
		return res;
	}
	
	List<Integer> next(){
		List<Integer> result = last;    		
		if(last != null) {
			last = null;
			return result;
		}
		if(child != null) {
			if(!child.hasNext()){
				if(pos == 0) {
					throw new RuntimeException("Next called but hasNext false");
				}
				child.recycle();
				pos--;
			}
			result = child.next();
			result.add(pos, size + origin - 1);
		}else{
			pos--;
			result = new ArrayList<>();
    		result.add(origin);
		}
		return result;
	}

	public static class Permutation<T>{
		List<T> objects;
		Permute perm;
		public Permutation(List<T> objects) {
			this(objects, -1);
		}
	
		public Permutation(List<T> objects, int choose) {
			this.objects = objects;
			this.perm = new Permute(objects.size(), choose);
		}
		
		boolean hasNext() {
			return perm.hasNext();
		}
		
		List<T> next(){
			List<T> result = new ArrayList<>(objects.size());
			List<Integer> order = perm.next();
			for(Integer i : order) {
				result.add(objects.get(i));
			}
			return result;
		}
	}
}