package com.bonfonte.threads;

import java.util.HashMap;
import java.util.Map;

public class QueueFactory  {

	private static Map<String,MemQueue> queues = new HashMap<String,MemQueue>();
	
	public static MemQueue getQueue( String topic ) {
	   	synchronized( queues ) {
	   		if ( topic == null ) topic = "";
	   		if ( queues.containsKey( topic ) ) return queues.get( topic );
	   		MemQueue queue = new MemQueue( topic );
	   		queues.put( topic, queue );
	   		return queue;
	   	}
	}

	public void start() {
//TODO		t.start(this, "Dhh");
	}
	
	public void run() {
		
	}

	
}
