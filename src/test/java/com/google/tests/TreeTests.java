package com.google.tests;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;


public class TreeTests {
	public enum WordState { FAIL, PARTIAL, WORD };
	
	public static abstract class Tries {
		private Map<String,Tries> kids = new TreeMap<String,Tries>();
		private Set<String> endings = new TreeSet<String>();
		protected abstract int getSize();
		protected abstract Tries getChildInstance();
		public void add( String child ) {
			if (child == null || child.length() == 0) {
				return;
			}
			addWord(child.toLowerCase());
		} 
		
		private void addWord( String child ) {
			if (child.length() <= getSize()) {
				this.endings.add(child);
			} else {
				String tail = child.substring(getSize());
				String head = child.substring(0, getSize());
				Tries kid = kids.get(head);
				if (kid == null) { 
					kid = getChildInstance();
					kids.put(head, kid);
				}
				kid.addWord(tail);;
			}
		}
		
		public boolean isWord( String word ) {
			if (word == null) return false;
			return findWord(word.toLowerCase());
		}
		
		private boolean findWord( String word ) {
			if ( word.length() <= getSize() ) {
				return  endings.contains( word );
			}
			String head = word.substring(0,getSize());
			Tries kid = kids.get(head);
			if (kid != null) {
				return kid.findWord( word.substring(getSize()) );
			}
			return false;
		}

		public WordState getState( String word ) {
			if (word == null || word.length() == 0) return WordState.FAIL;
			return findState(word.toLowerCase());
		}

		private WordState findState( String word ) {
			if ( word.length() <= getSize() ) {
				if (endings.contains( word )) {;
					return WordState.WORD;
				}
				if (word.length() < getSize()) {
					for (String key : endings) {
						if (key.startsWith(word)) {
							return WordState.PARTIAL;
						}
					}
				}
				for (String key : kids.keySet()) {
					if (key.startsWith(word)) {
						return WordState.PARTIAL;
					}
				}
				return WordState.FAIL;
			}
			String head = word.substring(0,getSize());
			Tries kid = kids.get(head);
			if (kid == null) {
				return WordState.FAIL;
			}
			return kid.findState( word.substring(getSize()) );
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			toString(0, sb);
			return sb.toString();
		}
		
		private StringBuilder newline( int pad, StringBuilder sb ) {
			sb.append( "\n" );
			while (pad-- > 0) sb.append( ' ' );
			return sb;
		}
		
		public void toString(int depth, StringBuilder sb) {
			newline( depth, sb ).append( "endings:[");
			for (String ending : endings) {
				newline( depth + 4, sb).append( " " ).append( ending );
			}
			sb.append( "]" );
			newline( depth, sb ).append( "subtrees:" );
			for (String key : this.kids.keySet()) {
				Tries tries = this.kids.get( key );
				newline( depth, sb ).append( "{ ").append( key );
				tries.toString(depth+4,sb);
				newline( depth, sb ).append( "}" );
			}
		}
	}

	public static class Tries1 extends Tries {
		@Override protected int getSize() { return 1; }
		@Override protected Tries getChildInstance() { return new Tries1B(); }
	}

	public static class Tries1B extends Tries {
		@Override protected int getSize() { return 1; }
		@Override protected Tries getChildInstance() { return new Tries2(); }
	}
	
	public static class Tries2 extends Tries {
		@Override protected int getSize() { return 2; }
		@Override protected Tries getChildInstance() { return new TriesLeaf(); }
	}

//	public static class Tries3 extends Tries { 
//		@Override protected int getSize() { return 3; }
//		@Override protected Tries getChildInstance() { return new TriesLeaf();}
//	}

	public static class TriesLeaf extends Tries { 
		@Override protected int getSize() { return 20; }
		@Override protected Tries getChildInstance() { return new TriesLeaf();}
	}

	public static Tries createRoot() {
		return new Tries1();
	}
	
	public static class TriesTests {
		private static final String[] WORDS = {
				"a", "I", "am", "and", "ago", "amber", "bat", "bath", "bathing", "bather", "constitute",
				"constitution", "be", "pea", "peanut", "but", "butte", "butter", "buttery", "buttered", "buttering",
				"supercallefragillisticexpeallidocious", "super",
				"word", "word", "WORD", // Dupes are fine
				"butters", "coin", "cob", "cobalt", "cocoa" };
		private static final Tries DICTIONARY = buildDictionary();

		public static Tries buildDictionary() {
			Tries root = createRoot();
			for (String word : WORDS) {
				root.add( word );
			}
			return root;
		}
		
		@Test
		public void showDictionaryTest() {
			System.out.println(DICTIONARY);
		}
		
		private void checkWholeWord( Tries tries, String word, boolean expected ) {
			boolean actual = tries.isWord(word);
			String msg = "Dictionary says '" + word + "' is " + (actual ? "" : "not ") + "present";
			System.out.println( msg ); 
			Assert.assertTrue(msg, actual == expected);
		}

		private void checkWordState( Tries tries, String word, WordState... expected ) {
			WordState actual = tries.getState(word);
			String msg = "Dictionary says '" + word + "' is " + actual;
			System.out.println( msg );
			boolean valid = false;
			for (WordState state : expected) {
				if (state == actual) {
					valid = true;
				}
			}
			Assert.assertTrue(msg, valid);
		}

		@Test
		public void checkWholeLogic() {
			Tries dct = DICTIONARY;
			checkWholeWord( dct, "a", true );
			checkWholeWord( dct, "", false );
			checkWholeWord( dct, "A", true );
			checkWholeWord( dct, "Bather", true );
			checkWholeWord( dct, "butte", true );
			checkWholeWord( dct, "Constitution", true );
			checkWholeWord( dct, "constitutione", false );
		}

		@Test
		public void checkStateLogic() {
			Tries dct = DICTIONARY;
			checkWordState( dct, "a", WordState.WORD );
			checkWordState( dct, "", WordState.FAIL );
			checkWordState( dct, "A", WordState.WORD );
			checkWordState( dct, "Bather",WordState.WORD );
			checkWordState( dct, "butte", WordState.WORD );
			checkWordState( dct, "bt", WordState.FAIL );
			checkWordState( dct, "bu", WordState.PARTIAL );
			checkWordState( dct, "Constitution", WordState.WORD );
			checkWordState( dct, "Constituti", WordState.PARTIAL );
			checkWordState( dct, "constitue", WordState.FAIL );
		}

		@Test
		public void checkStateLogicOverAll() {
			Tries dct = DICTIONARY;
			String[] words = WORDS;
			for ( String word : words ) {
				for (int i = 1; i <= word.length(); i++ ) {
					String sub = word.substring(0,  i);
					checkWordState( dct, sub, WordState.PARTIAL, WordState.WORD);
				}
			}
		}
	}
}
