package com.bonfonte.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class MultQue<T>{
	private int factor;
	private List<T> stash;
	private int index = 0;
	private MultQue<T> child;
	public MultQue(){
		this(7);
	}
	public MultQue(int factor){
		this.factor = factor < 2 ? 2 : factor;
		this.stash = new ArrayList<>(factor + 1);
	}

	public void add(T item){
		if(item == null | stash.contains(item)){
			return;
		}
		stash.add(item);
		index++;
		if(stash.size() > factor){
			item = stash.remove(0);
			if(index >= factor){
				pushdown(item);
				index = 0;
			}
		}
	}

	private void pushdown(T item){
		if(child == null){
			child = new MultQue<T>(factor);
		}
		child.add(item);
	}

	public boolean contains(T item){
		MultQue<T> mq = this;
		while(mq != null){
			if(mq.stash.contains(item)){
				return true;
			}
			mq = mq.child;
		}
		return false;
	}

	public Iterator<T> getIterator(){
		return new MqIterator<T>(this);
	}

	private static class MqIterator<T> implements Iterator<T>{
		MultQue<T> source;
		Iterator<T> iter;
		MqIterator<T> childIter;
		private MqIterator(MultQue<T> source){
			this.source = source;
			iter = source.stash.iterator();
		}

		@Override
		public boolean hasNext() {
			if(iter.hasNext()){
				return true;
			}
			if(source.child == null){
				return false;
			}
			return getChild().hasNext();
		}

		private MqIterator<T> getChild(){
			if(childIter != null){
				return childIter;
			}
			childIter = new MqIterator<T>(source.child);
			return childIter;
		}

		@Override
		public T next() {
			if(iter.hasNext()){
				return iter.next();
			}
			if(source.child != null){
				return getChild().next();
			}
			return null;
		}
	}

	public static class MultQueTest{
		@Test
		public void testSimple(){
			MultQue<Integer> mq = new MultQue<Integer>(5);
			for (int i = 0; i < 100; i++){
				mq.add(i);
			}
			Iterator<Integer> iter = mq.getIterator();
			int ix = 0;
			while(iter.hasNext()){
				System.out.println("Elt " + ix + " -> " + iter.next());
				ix++;
			}
		}
	}
}
