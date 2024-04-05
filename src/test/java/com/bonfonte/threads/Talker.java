package com.bonfonte.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Talker implements Runnable {

	public enum Person { 
		RABS(10, "See ya!"), ANDY(20, "I'm off"), LISA(15, "Whatever!"), MOM(32,"Good bye!" ), BECCA(20, "Ooouuuh!"), DAVID(25, "Becca!!");
		private int time;
		private String closer;
		private Person( int time, String close ) {
			this.time = time;
			closer = close;
		}
		public int getTime( ) { 
			return time;
		}
		public String getCloser() {
			return closer;
		}
	}

	private static Boolean busy = false;
	private static Map<Person, List<String>> sayings;
	private static Random rand = new Random();
	
	private Thread t;
	private Person whoAmI;
	private boolean stopped = false;

	public Talker( Person who ) {
		whoAmI = who;
		t = new Thread( this, who.name() ); 
	}
	
	private static void addSayings( HashMap<Person,List<String>> s, Person p, String... says ) {
		List<String> yak = s.get( p );
		if ( yak == null ) yak = new ArrayList<String>();
		for ( String str : says ) {
			yak.add( str );
		}
		s.put( p, yak );
	}
	
	private static void initSayings() {
		if ( sayings == null ) {
			HashMap<Person,List<String>> s = new HashMap<Person,List<String>>();
			addSayings( s, Person.RABS, "Grandy?", 
					 "What are you going to buy me?",
					 "Thwaiiinng .. and a mithhhp!!", 
					 "Yethpp?",
					 "Ain't I cute?", "Why wasn't I consulted?", 
					 "Evening MAM!",
					 "**** ***!!",
					 "Ooopth! my bad" );
			addSayings( s, Person.ANDY, "Yes Rabbi?", "Yes please She", "Can I have a weak coffee?", "No", "Maybe" );
			addSayings( s, Person.LISA, "Can we watch the game?", "I'm not trying to be rude but ...", "MOVE!!", "Rabbi!! That's inapprorpriate" );
			addSayings( s, Person.MOM,  "Would you like a hot drink?", "I'm just gonna ring Tina", "Your food's ready", "Have you applied to the Job yet?" );
			addSayings( s, Person.BECCA, "Merlin", "Leo", "I've got a migraine!", "Oh that's so gross", "Shhhhhhhhhh!",
					     "He's got a headache" );
			addSayings( s, Person.DAVID, "Zzzzzzzzzz", "Just thought I'd offer", "Pardon?", "Awesome", "Uuuuugh", "Can I bring some home for my mom?" );
			sayings = s;
		}
	}
	
	private void speak() {
		synchronized ( busy ) {
			if ( sayings == null ) {
				initSayings();
			}
			List<String> mySayings = sayings.get( whoAmI );
			int n = rand.nextInt(mySayings.size());
			String said = mySayings.get( n );
	    	System.out.println(whoAmI.name() + " : " + said );
		}
	}
	
	public void start() {
	    t.start();	
	}
	
	public void run() {
	   try {
	         while (!stopped) {
                speak();
	            // Let the thread sleep for a while.
            	Thread.yield();
	            Thread.sleep(100 * whoAmI.time);
	            if ( rand.nextInt( 20 ) == 17 ) {
	            	stopped = true;
	            } else {
	            }
	         }
	     } catch (InterruptedException e) {
	     }
     	 System.out.println(whoAmI + ": " + whoAmI.closer + " (leaves the room)");
	}
	public boolean isStopped() {
		return stopped;
	}
	public void forceStop() {
		stopped = true;
	}
	
	public enum Node {
		 A, B, C, D;
		 public Node next;
		 public static Node getBaseList() {
			 Node head = null;
			 Node prev = null;
			 for ( Node n : Node.values() ) {
				 n.next = null;
				 if ( prev != null ) { 
					 prev.next = n; 
				 }
				 prev = n;
				 if ( head == null ) head = n;
			 }
			 return head;
		 }
		 
		 public Node getNext() {
			 return next;
		 }
		 
		 public void setNext( Node n ) {
			 this.next = n;
		 }
	}
	
	public static Node reverse( Node list ) {
         Node previous = null;
         while ( list != null ) {
        	 Node temp = list.getNext();
        	 list.setNext(previous);
        	 previous = list;
        	 list = temp;
         }		
         return previous;
	}
	
	public static String display( Node list ) {
		StringBuffer b = new StringBuffer( "List" );
		String joint = " -> ";
		while ( list != null ) {
			b.append( joint );
			b.append( list.name() );
			list = list.getNext();
		}
		return b.toString();
	}
	
    public static void main( String[] args )    {
//    	Node list = Node.getBaseList();
//    	System.out.println( "Original " + display( list ) );
//    	System.out.println( "Reversed " + display( reverse( list ) ) );

     // See Conversation;
    	
    }
}
