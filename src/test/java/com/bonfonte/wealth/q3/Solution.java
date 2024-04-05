package com.bonfonte.wealth.q3;
/*
  Problem Question 3:
  Find interesting clock times needing only two digits to display between two given 24 hour clock times.
  Issues to take care of or ignored:
 - S and T are valid and S and T are on the same day S <= T.
   
*/

import java.util.HashSet;
import java.util.Set;

public class Solution {
    
	static void log(String s) {
		System.out.println(s);
	}
	
	static class ClockTime{
		int hh, mm, ss;
		
		public ClockTime(String s) {
			String[] hms = s.split(":");
			hh = Integer.parseInt(hms[0]);
			mm = Integer.parseInt(hms[1]);
			ss = Integer.parseInt(hms[2]);
		}
		
		private static String format(int n) {
			return (n < 10 ? "0" : "" ) + n;
 		}
		
		public int getDigits() {
			char[] display = (format(hh) + format(mm) + format(ss)).toCharArray(); 
			Set<Character> set = new HashSet<>();
			for(char ch : display) set.add(ch);
			return set.size();
		}

		@Override
		public String toString() {
			return format(hh) + ":" + format(mm) + ":" + format(ss);
		}
		
		public void next() {
			if(ss < 59) {
				ss++;
				return;
			}
			ss = 0;
			if(mm < 59) {
				mm++;
				return;
			}
			mm = 0;
			hh++;
		}
		
		public boolean isInteresting() {
			return getDigits() <= 2;
		}
	}

	public static int solution(String S, String T) {
		int interesting = 0;
		ClockTime sc = new ClockTime(S);
		for(;;) {
			if(sc.isInteresting()) {
				interesting++;
			}
			if(sc.toString().equals(T)) break;
			sc.next();
		}		
		return interesting;
	}

	private static void runTest(String S, String T) {
		int interesting = solution(S, T);
		log("There are " + interesting + " times between " + S + " and " + T);
	}
	
	public static void main(String[] args){
		runTest("15:15:00", "15:15:12");
		runTest("22:22:21", "22:22:23");
		runTest("00:00:00", "23:59:59");
	}
}