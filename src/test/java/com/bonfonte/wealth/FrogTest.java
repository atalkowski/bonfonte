package com.bonfonte.wealth;

import java.util.Random;

public abstract class FrogTest {
	static Random RANDOM = new Random(10101L); 
	static final String TEST0 = "2 2 0\n" + 
			"O*\n" + 
			"A%";
	static final String TEST1 = "3 6 1\n" + 
			"###*OO\n" + 
			"O#OA%O\n" + 
			"###*OO\n" + 
			"2 3 2 1";
	static final String TEST2 = "3 6 1\n" + 
			"###OOO\n" + 
			"O#OA%O\n" + 
			"###*OO\n" + 
			"2 3 2 1";
	
	static final String TEST3 = "7 7 2\n" + 
			"O**%**O\n" + 
			"OOOOOOO\n" + 
			"OOO*OOO\n" + 
			"**OA###\n" + 
			"OOOO#OO\n" + 
			"O*OO#O%\n" + 
			"OOOO#OO\n" + 
			"1 1 7 7\n" + 
			"6 4 6 6";
	
	static final String TEST4 = "2 3 0\n" + 
			"OO*\n" + 
			"AO%";

    String test0() {
		test(TEST0);
		return TEST0;
    }
    
	String test1() {
		test(TEST1);
		return TEST1;
	}

	String test2() {
		test(TEST2);
		return TEST2;
	}

	String test3() {
    	test(TEST3);
    	return TEST3;
	}


    String test4() {
    	test(TEST4);
    	return TEST4;
    }


	public abstract double test(String input);
	

}
