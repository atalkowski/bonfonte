package com.bonfonte.alpha;

import java.util.ArrayList;
import java.util.List;

public class Alpha {
	private static final String somecode = "som48";
	private static final String sources = "bclomp";
	private static final String encoders = "adefghinqrstuxyADEFGHINQRSTUXY";
	private static final String digit = "0123456789";
	private static final String digal = "oizeasbjbg";
	private static final String alpha = "abcdefghijklmnopqrstuvwxyz";
	private static final String vowel = "aeiou";
	private static final String conso = "bcdfghjklmnpqrstvwxyz";
	private static final String hail = "hmfogtliwtbatawbftwjhmmogp";
	private static final String[] elements = { 
		"aluminium", "boron", "carbon", "dysprosium", "europium", "fluorine", "gallium", "hydrogen", 
		"iodine", "joule", "potassium", "lithium", "magnesium", "nitrogen", "oxygen", "phosphorus",
		"quark", "radon", "sulphur", "tungsten", "uranium", "vanadium", "wolfram", "xenon", "ytterbium", "zinc" 
	};

	private static final String[] hours = { "kink", "bald", "aroma", "max", "card", "tuber", "claim",
			"bystander", "port", "joke", "sailor", "more" };
	private static final String[] minutes = { "man", "crow", "flame", "pixie", "dopey", "trial", "person", "vax",
			"bubble", "lamp"};
	private static final String[] seconds = { "top", "north", "grant", "wild", "dosile", "ramp", "private", "party",
			"light", "foreign" };
	
	private static final String[] foxes = { 
		"alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", 
		"india", "juliet", "kiwi", "lima", "mike", "november", "oscar", "papa",
		"quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu" 
	};

	private boolean debug;	
	private String seed;
	private String input;
	private StringBuilder output;
	private int iChs;
	private int oSeq;
	private int lSeq;
	private int pSeq;
	private int cSeq;
	private int mSeq;
	private int iPos;
	private int sPos;
	private int pos = 0;
	
	public static class Oper{
		Cat cat;
		char code;
		int mult;
		String modifiers = "";
		public Oper(Cat cat, char code, int mult){
			this.cat = cat;
			this.code = code;
			this.mult = mult;
		}
		public String toString(){
			String result = cat.name() + " " + code;
			if(mult != 1){
				result += " x " + mult;
			}
			if(modifiers.length() != 0){
				result += " mods=\'" + modifiers + "\'";
			}
			return result;
		}
	}
	
	private enum Cat{
		OP, SRC, NUMB, MOD, CHAR, LPAREN, RPAREN, DOT, ESC;
		public static Cat of(char c){
			if(encoders.indexOf(c) >= 0){
				return OP;
			}
			if(sources.indexOf(c) >= 0){
				return SRC;
			}
			if('0' <= c && c <= '9'){
				return NUMB;
			}
			switch(c){
			case '\\': return ESC;
			case '(': return LPAREN;
			case ')': return RPAREN;
			case '+': case '-': case '_': case '*': case '^':
				return MOD;
			case '.': return DOT;
			default: return CHAR;
			}
		}
	}
	
	private int readNum(char initCh){
		int result = initCh - '0';
		while(pos < seed.length()) {
			char ch = seed.charAt(pos);
			if(Cat.of(ch) != Cat.NUMB){
				break;
			}
			result = result * 10 + (ch - '0');
			pos++;
		}
		if (result < 0 || result > 100){
			result = 1; // Sorry ... duff numbers are not allowed.
		}
		return result;
	}
	
	private String readModifiers(){
		int currentMult = 1;
		int savPos = pos;
		StringBuilder modifiers = new StringBuilder();
		while (pos < seed.length()){
			char c = seed.charAt(pos++);
			Cat type = Cat.of(c);
			if(type == Cat.NUMB){
				currentMult = readNum(c);
				continue;
			}
			if(type != Cat.MOD){
				pos = savPos;
				break;
			}
			for(int i=0; i < currentMult; i++){
				modifiers.append(c);
			}
			currentMult = 1;
			savPos = pos;
		}
		return modifiers.toString();
	}
	
	private Oper readBasicOp(){
		Oper result = null;
		int currentMult = 1;
		while(pos < seed.length()){
			char c = seed.charAt(pos++);
			Cat type = Cat.of(c);
			if(type == Cat.NUMB){
				currentMult = readNum(c);
				continue;
			}
			result = new Oper(type,  c, currentMult);
			break;
		}
		if (result != null) {
			result.modifiers = readModifiers();
		}
		return result;
	}
	
	public List<Oper> parseInput(){
		List<Oper> result = new ArrayList<>();
		while(pos < seed.length()){
			Oper op = readBasicOp();
			if(op == null){
				break;
			}
			result.add(op);
		}
		return result;
	}
	
	public Alpha( String seed, boolean noExpand ) {
		if ( seed == null || seed.length() == 0) {
			seed = "3l5m";
		}
		if(noExpand){
			this.seed = seed;
			return;
		}
		expandNumbersAndInitSeed( seed );
	}

	public Alpha( String seed ) {
		this(seed, false);
	}
	
	private boolean isDigit( char c ) {
		return digit.indexOf( c ) >= 0;
	}
	
	private void expandNumbersAndInitSeed( String rawseed ) {
		StringBuilder sb = new StringBuilder();
		int mult = 0;
		for ( int i = 0; i < rawseed.length(); i++ ) {
			char c = rawseed.charAt( i );
			if ( isDigit( c ) ) {
				mult = mult * 10 + ( c - '0' );
			} else {
				if ( mult == 0 ) mult = 1;
				while ( mult > 0 ) {
					sb.append( c );
					mult--;
				}
			}
		}
		seed = sb.toString();
		log( "Setting initial seed to " + seed );
	}

	private void log( String s ) {
		if (debug) {
			System.out.println( s );
		}
	}
	
	public void setDebug(String s){
		this.debug = "-dptdotlhs".equals(s);
	}

	public void setDebug(boolean on){
		this.debug = on;
	}
	
	private String normalize( String inp ) {
		StringBuilder res = new StringBuilder();
		if ( inp != null ) {
			String inpLower = inp.toLowerCase();
			for ( int i = 0; i < inpLower.length(); i++ ) {
				char c = inpLower.charAt( i );
				if ( c >= 'a' && c <= 'z' ) {
					res.append( c );
				} else {
					if ( c >= '0' && c <= '9' ) {
						res.append( digal.charAt( c - '0' ) );
					}
				}
			}
		}
		log( "Setting input to be " + res.toString() );
		return res.toString();
	}
	
	private String initInput( String inp ) {
		int pPos = -1;
		int cPos = -1;
		iPos = 0;
		sPos = 0;
		oSeq = -1;
		pSeq = -1;
		cSeq = -1;
		mSeq = -1;
		input = normalize( inp );
		output = new StringBuilder();
		iChs = input.length();
		lSeq = iChs - 4;
		for ( int i = 0; i < iChs; i++ ) {
			char ch = input.charAt( i );
			int	p = vowel.indexOf( ch );
			int c = conso.indexOf( ch );
			if ( c >= 0 ) {
				if ( c > cPos ) {
					cSeq = i;
					cPos = c;
				}
			} else {
				if ( p >= 0 ) {
					if ( p < pPos || pPos < 0 ) {
						pPos = p;
						pSeq = i;
					}
				}
			}
		}
		log( "Setting input=" + input + "; iPos=" + iPos + " lSeq=" + lSeq
				+ " pSeq=" + pSeq
				+ " oSeq=" + oSeq
				+ " cSeq=" + cSeq
				+ " mSeq=" + mSeq );
		return input;
	}
	
	private int normSeqNo( int seq ) {
		if ( seq >= iChs ) {
			return seq%iChs;
		}
		while ( seq < 0 ) {
			seq += iChs;
		}
		return seq;
	}
	
	private int nextO() {
		oSeq = normSeqNo( oSeq + 1 );
		return oSeq;
	}
	
	private int nextP() {
		pSeq = normSeqNo( pSeq + 1 );
		return pSeq;
	}

	private int nextC() {
		cSeq = normSeqNo( cSeq - 1 );
		return cSeq;
	}

	private int nextL() {
		lSeq = normSeqNo( lSeq + 1 );
		return lSeq;
	}

	private int nextM() {
		mSeq = (mSeq + 1)%(somecode.length());
		return mSeq;
	}

	private char nextIn( char seq ) {
		int pos = 0;
		boolean upper = false;
		String in = input;
		switch ( seq ) {
		case 'L': upper = true;
		case 'l': pos = nextL(); break;
		case 'C': upper = true;
		case 'c': pos = nextC(); break;
		case 'M': upper = true;
		case 'm': in = somecode; pos = nextM(); break;
		case 'P': upper = true;
		case 'p': pos = nextP();	break;
		case 'O': upper = true;
		case 'o': 
		default: 
	   		pos = nextO();
   		break;
		}
		String s = "" + in.charAt( pos );	
		if ( upper ) {
			s = s.toUpperCase(); 
		}
		return s.charAt( 0 );
	}
	
	private String c2s( char c ) {
		StringBuilder sb = new StringBuilder();
		sb.append( c );
		return sb.toString();
	}

	private String getH( char in, int uppers, int leng ) {
		return getFromNames( in, uppers, leng, elements );
	}

	private String getR(char in, boolean upper ) {
		int c = alpha.length() - (in - 'a') - 1;
		if(c>=0 && c<alpha.length()){
			return "" + alpha.charAt(c);
		}else{
			return "" + in;
		}
	}

	private String getF( char in, int uppers, int leng ) {
		return getFromNames( in, uppers, leng, foxes );
	}

	private String getFromNames( char in, int uppers, int leng, String[] names ) {
		return getFromNames0(in, 0, 1, uppers, leng, names);
		
	}
	private String getFromNames0(char in, int charOffset, int getCharAt, int uppers, int leng, String[] names ) {
		String res;
		int index = in - 'a' + charOffset;
		log("getFromNames0('" + in + "'=A[" + index + "] : getCharAt=" + getCharAt
				+ " upp=" + uppers + " leng=" + leng 
				+ " names=[" + names[0] + ", ...])");

		String nameToUse = names[ index % names.length ];
		while(nameToUse.length() < leng + getCharAt){
			nameToUse = nameToUse + nameToUse;
		}
		if ( index < 0 ) {
			log( "Error in input normalization: unexpected character " + in );
			res = c2s( in );
		} else {
			res = nameToUse.substring( getCharAt, getCharAt+leng );
		}
		if ( uppers > 0 ) {
			if ( uppers > res.length() ) {
				uppers = res.length();
			}
			res = res.substring(0,uppers).toUpperCase() + res.substring(uppers);
		}
		return res.substring(0, leng);
	}

	private String getE( char in, boolean upper ) {
		String e = "etrinoasdlchfupmygwvbkqjxz";
		return getChoff( in, upper, e, 1 );  
	}

	private String getQ( char in, boolean upper ) {
		String e = "thequickbrownfxjmpsvlazydg";
		return getChoff( in, upper, e, 1 );  
	}

	private String getChoff( char in, boolean upper, String coder, int offset ) {
		int pos = ( coder.indexOf( in ) + offset )%( coder.length() );
		String res = coder.substring( pos, pos+1 );
		return upper ? res.toUpperCase() : res;
	}
	
	private int get9( int n ) {
		int t = 100;
		while ( t < n ) t = t * 10;
		return t - n - 1;
	}
	
	private int getN( char in, boolean upper ) {
		int r = alpha.indexOf( in ) + 1;
		return upper ? get9( r ) : r;
	}
	
	private String getA( char in, boolean upper ) {
		return getChoff( in, upper, alpha, 1 );
	}
	
	private void encodeOps( String ops, char seq ) {
		log("encodeOps(" + ops + "," + seq +")");
		if ( ops.length() == 0 ) {
			ops = "i";
		}
		
		int i = 0;
		while(i < ops.length()) {
			char op = ops.charAt( i++ );
			
			char in = nextIn( seq );
			String out = "";
			boolean upper = (op >= 'A' && op <= 'Z');
			switch ( op ) {
			case 'A': case 'a': out = getA( in, upper ); break;
			case 'G': case 'g': out = getF( in, upper ? 1 : 0, 3 ); break;
			case 'F': case 'f': out = getF( in, upper ? 1 : 0, 1 ); break;
			case 'E': case 'e': out = getE( in, upper ); break;
			case 'H': case 'h': out = getH( in, upper ? 1 : 0, 1 ); break;
			case 'Q': case 'q': out = getQ( in, upper ); break;
			case 'R': case 'r': out = getR( in, upper ); break;
			case 'S': case 's': out = getFromNames0(in, 0, 0, 0, 1, hours); break;
			case 'T': case 't': out = getFromNames0(in, 1, 0, 0, 1, minutes); break;
			case 'U': case 'u': out = getFromNames0(in, 1, 0, 0, 1, seconds); break;
			case 'N': case 'n': 
				out = "" + getN( in, upper );
				if ( out.length() < 2 ) {
					out = "0" + out;
				}
				break;
			default:
				out = c2s( in );
				break;
			}
			log( "Op=" + op + " in=" + in + " -> " + out );
			output.append( out );
		}
	}
	
	private char nextSeedCh() {
		if ( sPos >= seed.length() ) {
			return 0;
		}
		return seed.charAt( sPos++ );
	}

	public static String getQ( char c, int multi, int add ) {
		int charVal = getValueForAlpha(c);
		int val = charVal * multi + add;
		String output = "" + get100( val );
		if (output.length() < 2){
			output = "0" + output;
		}
		return output;
	}
	
	public static int get100( int val ){
		while (val > 99) {
			val = getCrossAdd( val );
		}
		return val;
	}
	
	public static int getCrossAdd( int input ) {
		if (input < 10) {
			return input;
		}
		List<Integer> values = new ArrayList<>();
		while (input > 0) {
			values.add( input % 10);
			input = input / 10;
		}
		int output = 0;
		int size = values.size();
		while (size > 1) {
			size = size - 1;
			int tens = values.get(size);
			int unit = values.get(size-1);
			int newUnit = getCrossAdd(tens + unit);
			output = output * 10 + newUnit;
		}
		return output;
	}
	
	private static int getValueForAlpha( char x ) {
		if ('A' <= x && x <= 'Z') {
			return x - 'A' + 1;
		}
		if ('a' <= x && x <= 'z') {
			return x - 'a' + 1;
		}
		return 0;
	}
	
	private String getX( char x ) {
		char s1 = nextSeedCh();
		char s2 = nextSeedCh();
		char in1 = nextIn( s1 );
		char in2 = nextIn( s2 );
		int i1 = getN( in1, false );
		int i2 = getN( in2, false );
		int r1 = i1 * i2;
		int r2 = x == 'x' ? get9( r1 ) : r1;
		log( "getX:\n s1=" + s1 + "->" + in1 + "->" + i1
				+ "\n s2=" + s2 + "->" + in2 + "->" + i2 
				+ "\n " + x + "-> " + r1 + "->" + r2 );
		return "" + r2;
	}
	
	private void encode() {
		String mappers = "";
		char seq = 'l';
		while ( true ) {
			char op = nextSeedCh();
			if ( op == '\0' ) break;
			if ( encoders.indexOf( op ) >= 0 ) {
				mappers += op;
				continue;
			} else {
				seq = op;
				switch ( seq ) {
				case 'X':
				case 'x':
					output.append( getX( seq ) );
					break;
				default:
					encodeOps( mappers, seq );
				}
				mappers = "";
			}
		}
		if (mappers.length() > 0) {
			encodeOps( mappers, seq );
		}
	}
	
	public synchronized String encode( String s ) {
		initInput( s );
		encode();
		return output.toString();
	}
	private static void blurb() {
		System.out.println( "Usage:\n"
				+ "\nalpha pattern value"
				+ "\nor\nalpha -r"
				+ "\nwhere pattern is of form '[<mappers><source>]*"
				+ "\n  <mappers> ::= <mapper><mappers> | <multimapper><mappers>"
				+ "\n  <source> ::= l | m | c | p | o"
				+ "\n  <mapper> ::= one of " + encoders
				+ "\n and [n] is just a multiplier for the following element."
				+ "\n Use the -r to generate a random sample pattern"
				);
	}
	
	private static String genRandom1() {
		long now = (long) ( Math.random() * System.currentTimeMillis() );
		int mapperRand = (int) (now%107);
		int sourceRand = (int) (now%197);
		long n = now % 3;
		char mapper = encoders.charAt(mapperRand % encoders.length());
		char source = sources.charAt(sourceRand % sources.length());
		return n + "" + mapper + "" + source;
	}

	private static void genRandom() {
		System.out.println( genRandom1() + genRandom1() );
	}
	
	public static void main( String[] args ) {
		String seed = null;
		String data = null;
		String debg = null;
		int i = 0;
		while ( i < args.length ) {
			String arg = args[i++];
			if ( arg.startsWith( "-r" ) ) {
				genRandom();
			} else 
			if ( arg.startsWith( "-h" ) ) {
				blurb();
			} else 
			if ( arg.startsWith( "-d" ) ) {
				debg = arg;
			} else {
				if ( seed == null ) {
					seed = arg;
				} else {
					data = arg;
				}
			}
		}
		if ( data != null ) {
			Alpha a = new Alpha( seed );
			a.setDebug(debg);
			String output = a.encode( data );
			System.out.println(output);
		}
	}
}
