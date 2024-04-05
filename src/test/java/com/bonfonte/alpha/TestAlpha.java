package com.bonfonte.alpha;

import com.bonfonte.experiment.alpha.Alpha;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
//  import com.bonfonte.alpha.Alpha;


public class TestAlpha{

	private static void log( String s ) {
		System.out.println( s );
	}

	private void testRun( String data, String coder, String expected ) throws Exception  {
		try {
			Alpha a = new Alpha( coder );
			String c = a.encode( data );
			String res = " encoding [" + data + "] with [" + coder + "] expecting " + expected + " got " + c;
			if ( !expected.equals( c ) ) {
				throw new Exception( "Failed " + res );
			}
		} catch ( Exception e ) {
			log( "Unexpected exception " + e.getMessage() );
			throw e;
		}
	}
	
	@Test
	public void test1() throws Exception {
		testRun( "Hello World", "3l5m", "rldsom48" );
	}

	@Test
	public void test2() throws Exception {
		testRun( "Hello World", "3l8m", "rldsom48som" );
	}

	@Test
	public void test3() throws Exception {
		testRun( "Hello World", "3aGl", "smeOte" );
	}

	@Test
	public void test4() throws Exception {
		testRun( "Hello World", "3aGnl", "smeOte05" );
	}

	@Test
	public void test5() throws Exception {
		testRun( "Hello World", "aAafFfNl", "sMeoCi87" );
	}
	
	@Test
	public void test6() throws Exception {
		testRun( "abcdef", "3aGnp", "cdeCho06" );
	}
	
	@Test
	public void test7() throws Exception {
		testRun( "abcdef", "3aGNp", "cdeCho93" );
	}

	@Test
	public void test8() throws Exception {
		testRun( "abcdef", "10Fl", "ECOLRHECOL" );
	}

	@Test
	public void test9() throws Exception {
		testRun( "abcdef", "4gl", "eltchooxtlph" );
	}



	int[] inputs = { 192, 43, 289, 17890 };
	int[] expect = { 12, 43, 18, 14 }; /* -> 8689 -> 558 -> 14 */
	@Test
	public void testCrossAdd() {
		for (int index = 0; index < inputs.length; index++){
			int actual = Alpha.get100( inputs[index] );
			Assert.assertEquals(expect[index], actual);
		}
	}

	@Test
	public void testQ() {
		String alphas = "AbcDeFghijKlmnoPqrStuvwxyz123";
		for(int index = 0; index < alphas.length(); index++){
			char c= alphas.charAt(index);
			String encoded = Alpha.getQ(c, 11, 17);
			// System.out.println( c + " -> " + encoded);
			switch (c) {
				case 'a': case 'A': Assert.assertEquals(encoded,  "28");
					break;
				case 'o': case 'O': Assert.assertEquals(encoded, "91");
					break;
			}
		}
	}

	private void runTest(String seed, String data, String expected){
		Alpha alp = new Alpha(seed);
		String actual = alp.encode(data);
		//	System.out.println("'" + seed + "' ~ '" + data + "' -> '" + actual + "'");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test3alGn(){
		runTest("3alGn", "Andrew", "sfxLph14");
	}

	@Test
	public void test3elFffN(){
		runTest("3elFffN", "Andrew", "itvLoe81");
		runTest("eeelF2fN", "Andrew", "itvLoe81");
	}

	@Test
	public void test3eoFffN(){
		runTest("3eoFffN", "Andrew", "solOch98");
	}

	@Test
	public void testsss(){
		runTest("sss", "fgh", "tcb");
	}

	@Test
	public void testttt(){
		runTest("ttt", "fgh", "pvb");
	}

	@Test
	public void test6u(){
		runTest("6u", "fgh", "pplppl");
	}

	@Test
	public void test5r(){
		runTest("5r", "abc", "zyxzy");
	}

	@Test
	public void test3q(){
		runTest("3q", "abc", "zrk");
	}

	@Test
	public void testhHh(){
		runTest("hHh", "ABC", "lOa");
	}

	@Test
	public void test6hp(){
		runTest("6hp", "talkowski", "ioxouo");
	}

	private void runParseTest(String seed, String data){
		Alpha alp = new Alpha(seed, true);
		String actual = alp.encode(data);
		Assert.assertEquals("t84mvrhin", actual);
		List<Alpha.Oper> opers = alp.parseInput();
		String[] expect = new String[] {
			"OP a", "OP N", "OP d", "OP r x 2", "OP e mods='+'", "CHAR w", "OP d mods='++'"
		};
		int i = 0;
		for(Alpha.Oper oper : opers) {
			Assert.assertEquals(expect[i++], oper.toString());
		}
	}

	@Test
	public void testParse(){
		runParseTest("aNd2re+wd2+", "Something");
	}
}
