package com.bonfonte.threads;

public class MemQueue implements Runnable {
    private Thread t;
    private String topic;
    private String[] messages = new String[QUEUE_SIZE];
    private int readPoint = 0;
    private int writePoint = 0;
    private static final int QUEUE_SIZE = 10;

    public void run() {
    	
	}
	
	public MemQueue( String topic ) {
		this.topic = topic;
		t = new Thread( this, topic );
	    t.start();	
	}


}
