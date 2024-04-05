package com.bonfonte.threads;

import java.util.ArrayList;
import java.util.List;

import com.bonfonte.threads.Talker.Person;

public class Conversation {

    public static void main( String[] args )
    {
    	List<Talker> people = new ArrayList<Talker>();
    	System.out.println("----- Starting conversation --- " );
    	for ( Person p : Person.values() ) {
    		Talker t = new Talker( p );
    		people.add(t);
    	}
    	for ( Talker t : people ) {
            t.start();
    	}

    	boolean talking = true;
    	while( talking ) {
    		talking = false;
    		try {
    			Thread.sleep(100);
    		} catch ( InterruptedException e ) {
    			break;
    		}
    		for ( Talker t : people ) {
    			if ( !t.isStopped() ) {
    				talking = true;
    				break;
    			}
    		}
    		
    	}
    	System.out.println("----- End of conversation --- " );
    }

}
