package com.bonfonte.threads;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;


public class Concurr {
    private static Lock lock = new ReentrantLock();
	private static void sleep( int msec ) {
		try { 
			Thread.sleep( msec );
		} catch ( InterruptedException e ) {
			log( "Error: sleep interrupted" );
		}
	}

	public static void log( String s ) {
		System.out.println( System.currentTimeMillis() + ": " + s );
	}
	
	public static void log( int who, String s ) {
		System.out.println( System.currentTimeMillis() + ": " + who + " : " + s );
	}

	public static class Fighter implements Runnable {
        private Random rand;
        private int seed;
        private int life = 10;
    	private Thread t;
    	private Fight bout;
        private boolean ouched = false;
        
        public Fighter( Fight fight, int seed ) {
        	rand = new Random( seed * System.currentTimeMillis() );
            this.seed = seed;
            this.bout = fight;
            log( "Initialized fighter " + seed );
            t = new Thread( this, "" + seed );
        }
        
        private String getPunch() {
        	int n = rand.nextInt() & 7;
        	switch ( n ) {
        	case 0: return "Left hook";
        	case 1: return "Right";
        	case 2: return "Uppercut";
        	case 3: return "Jab";
        	case 4: return "Ducks";
        	case 5: return "Left";
        	default: return "Jab";
        	}
        }

        private void wackOpponent( int msec ) {
        	if ( lock.tryLock() ) {
        		log( seed, getPunch() );
				sleep( msec );
   		    	ouched = false;
				bout.hitter = seed;
        		lock.unlock();
        	} else {
        		if ( bout.hitter != seed ) {
        		    if ( ouched ) {
           		    	log( seed, "Weaves..." );
        		    	ouched = false;
        		    } else {
        		    	ouched = true;
           		    	log( seed, "Ouch!!" );
           		    	life--;
        		    }
        		}
				sleep( msec );
        	}
        }

        public void run() {
			while ( !bout.gameOver.get() ) {
				int msec = rand.nextInt( 200 );
				if ( life <= 0 ) {
					log( seed, "Game over for me" );
					bout.gameOver.set( true );
					break;
				} else {
					wackOpponent( msec );
				}
			}
		}
		
        public void start() {
            log( "Starting yarn " + seed );
        	t.start();
        }
	}
	
    public static class Fight {
    	int hitter = -1;
    	Fighter fighter1;
    	Fighter fighter2;
    	AtomicBoolean gameOver = new AtomicBoolean( false );

    	public Fight( int player1, int player2 ) {
        	fighter1 = new Fighter( this, player1 );
    		fighter2 = new Fighter( this, player2 );
    	}
    	
    	public int getWinner() {
    		if ( gameOver.get() ) {
    			return hitter;
    		}
    		return -1;
    	}
    	public void start() {
    		fighter1.start();
    		fighter2.start();
    	    while ( !gameOver.get() ) {
    	    	sleep( 1000 );
    	    }
    	}
    }
	
	@Test
	public void test2bout() {
		Fight f1 = new Fight( 1, 2 );
		f1.start();
		int winner1 = f1.getWinner();
		Fight f2 = new Fight( 3, 4 );
		f2.start();
		int winner2 = f2.getWinner();
		
        f1 = new Fight( winner1, winner2 );
        f1.start();
        log( "And your final winner is " + f1.getWinner() );
	}
	
	
}
