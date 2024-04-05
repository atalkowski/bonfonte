package com.hack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.Test;



public class SampleTests {

	private static String CHAOS = "Too chaotic";
	private static void log(String s, Object... objs) {
		StringBuilder sb = new StringBuilder();
		int objInx = 0;
		for(int inx = 0; inx < s.length(); inx++) {
			char c = s.charAt(inx);
			if(c == '{' && objInx < objs.length 
					&& inx + 1 < s.length() && s.charAt(inx+1) == '}'){
				inx++;
				Object obj = objs[objInx++];
				if(obj == null) {
					continue;
				}
				sb.append(obj.toString());
			}else {
				sb.append(c);
			}
		}
		System.out.println( sb.toString() );
	}
	
	private static <T> void print(Stream<T> stream) {
		stream.forEach(o -> log(o == null ? "null" : o.toString()));
	}
	
    static String sortString(String inputString){ 
        char tempArray[] = inputString.toCharArray(); 
        Arrays.sort(tempArray); 
        return new String(tempArray); 
    } 
    
    private static class Anagram{
    	public String input;
    	private char[] array;
    	private int pos = 0;
    	
    	public Anagram(String input){
    		this.input = input;
    		this.array = input.toCharArray();
    		Arrays.sort(this.array);
    	}
    	
    	public int current(){
    		if(hasNext()){
    			return array[pos];
    		}
    		return Integer.MAX_VALUE;
    	}
    	
    	public int next(){
    		pos = pos + 1;
    		return current();
    	}
    	
    	public boolean hasNext(){
    		return pos < array.length;
    	}
    	
    	public int remaining(){
    		if(pos >= array.length) return 0;
    		return array.length - pos;
    	}
    }

    static int makeAnagram(String a, String b) {
    	if(b.length() < a.length()){
    		return makeAnagram(b, a);
    	}
    	Anagram A = new Anagram(a);
    	Anagram B = new Anagram(b);
    	int deletes = 0;
    	while(A.hasNext() && B.hasNext()){
    		int ach = A.current();
    		int bch = B.current();
    		if(ach == bch){
    			A.next();
    			B.next();
    			continue;
    		}
			deletes++;
    		if(ach < bch){
    			A.next();
    		}else{
    			B.next();
    		}
    	}
    	return deletes + A.remaining() + B.remaining();
    }
    
    static boolean isPalindrome(String s, int pos, int length){
    	int end = pos + length;
    	if(end > s.length()){
    		System.out.println("Warn buggy palindrome call " + s + " " + pos + " " + length);
    		return false;
    	}
    	String palin = s.substring(pos, pos + length);
    	for(int i = 0; i < length/2; i++){
    		if(palin.charAt(i) != palin.charAt(length-i-1)){
    			return false;
    		}
    	}
    	return true;
    }
    
    static long substrCount(int n, String s) {
    	if(s.length() > n) s = s.substring(0,n);
    	long total = s.length();
    	for(int size = 2; size <= s.length(); size++){
    		for(int pos = 0; pos + size <= s.length(); pos++){
    			if(isPalindrome(s, pos, size)){
    				total++;
    			}
    		}
    	}
    	return total;
    }

    private void runPalin(String s){
    	long total = substrCount(s.length(), s);
    	System.out.println("Total palindromes for [" + s + "] = " + total);
    }
    
    @Test
    public void testPalin(){
    	runPalin("aaaa");
    	runPalin("abab");
    	runPalin("abc");
    	runPalin("the cat sat on the mat");
    }
    
    static int makeAnagram1(String a, String b) {
    	if(b.length() < a.length()){
    		return makeAnagram(b, a);
    	}
    	char arrA[] = a.toLowerCase().toCharArray();
    	char arrB[] = b.toLowerCase().toCharArray();
    	Arrays.sort(arrA);
    	Arrays.sort(arrB);
    	int indexA = 0;
    	int indexB = 0;
    	int deletes = 0;
    	while(indexA < arrA.length || indexB < arrB.length){
    		String aData = "A[" + indexA + "] =" + (indexA < arrA.length ? arrA[indexA] : "null");
       		String bData = "B[" + indexB + "] =" + (indexB < arrB.length ? arrB[indexB] : "null");
    		System.out.println("Examining chars at " + aData + " and " + bData + "; deletes=" + deletes);
    		if(indexA >= arrA.length){
    			deletes += arrB.length - indexB;
    			break;
    		}
    		if(indexB >= arrB.length){
    			deletes += arrA.length - indexA;
    			break;
    		}
    		if(arrA[indexA] == arrB[indexB]){
    			indexA++;
    			indexB++;
    			continue;
    		}
    		boolean foundMismatch = false;
    		while(indexA + 1 < arrA.length
    				&& Integer.compare(arrA[indexA+1], arrB[indexB]) < 0){
    			indexA++;
    			foundMismatch = true;
    			deletes++; // Deleting mismatching A characters
    		};
    		if(foundMismatch){
    			continue;
    		}
    		while(indexB + 1 < arrB.length 
    				&& Integer.compare(arrB[indexB+1], arrA[indexA]) < 0){
    			indexB++;
    			deletes++; // Deleting mismatching B characters
    			foundMismatch = true;
    		};
    		
    		if(!foundMismatch){ // Then chars at both positions must be deleted
				indexA++;
				deletes++;
				indexB++;
				deletes++;
    		}
    	}
    	return deletes;
    }    
    
    private void testAnagramDeletes(String a, String b){
    	int deletes = makeAnagram(a, b);
    	System.out.println("Total = " + deletes + " for String [" + a + "] v [" + b + "]");
    }

    
    @Test
    public void testAnag1(){
    	testAnagramDeletes("hello", "world");
//       	testAnagramDeletes("I've got a lovely", "bunch of coconuts");
    }
    
    static int sockMerchant(int n, int[] ar) {
        Set<Integer> pairs = new HashSet<>();
        int total = 0;
        while(n > 0){
            n--;
            int sock = ar[n];
            if(pairs.contains(sock)){
                total++;
                pairs.remove(sock);
            }else{
                pairs.add(sock);
            }
        }
        return total;
    }
    
    static int countingValleys(int n, String s) {
        int depth = 0;
        int valleys = 0;
        for(int index = 0; index < n && index < s.length(); index++){
        	char step = s.charAt(index);
        	switch(step){
        	case 'U':
        		depth++;
        		if(depth == 0){
        			valleys++;
        		}
        		break;
        	case 'D':
        		depth--;
        		break;
        	default: continue;
        	}
        }
        return valleys;
    }
    
    static int jumpingOnClouds(int[] c) {
        int jumps = 0;
        int index = 0;
        int maxpos = c.length - 1;
        while(index < maxpos){
            if(index + 2 <= maxpos && c[index + 2] == 0){
                index++;
            }
            index++; // Must jump at least 1 cloud
            jumps++;
        }
        return jumps;
    }
  
    static long occurs(String s, long n){
    	int count = 0;
    	int pos = 0;
    	while(pos < n){
    		int nxt = s.indexOf('a', pos);
    		if(nxt++ >= 0){
    			count++;
    			pos = nxt;
    		}
    	}
    	return count;
    }
 
    static String twoStrings(String s1, String s2) {
    	if(s1.length() > s2.length()){
    		return twoStrings(s2, s1);
    	}
        for(int index = 0; index < s1.length(); index++){
            if(s2.indexOf(s1.substring(index,index+1)) >= 0) return "YES";            
        }
        return "NO";
    }

    static String twoStringsFast(String s1, String s2) {
    	if(s1.length() > s2.length()){
    		return twoStrings(s2, s1);
    	}
    	Set<Integer> s1Set = new HashSet<>();
    	for(int index = 0; index < s1.length(); index++){
    		s1Set.add(0 + s1.charAt(index));
    	}
        for(int index = 0; index < s2.length(); index++){
            if(s1Set.contains(0 + s2.charAt(index))) return "YES";            
        }
        return "NO";
    }
    
    static String getDuplicates(String s){
    	Character last = null;
    	s = sortString(s);
    	StringBuilder result = new StringBuilder();
    	for(char ch : s.toCharArray()){
    		if(last != null){
    			last = ch;
    		}else{
    			result.append(ch);
    		}
    	}
    	return result.toString();
    }

    static boolean isAnagram(String sorted, String s2){
    	System.out.println("Comparing [" + sorted + "] and [" + s2 + "]");
    	if(sorted.length() != s2.length()) return false;
    	return sorted.equals(sortString(s2));
    }
    
    static int sherlockAndAnagrams(String s) {
    	// Example : ifaicd has substrings [i,i] and [ifa,fai] so answer is 2.    	
    	//  low                             last low
    	//   V-->leng                        V  
    	// asdrbitrarystrbidexxxxrbiexxxxxxxxx
    	int total = 0;
    	
    	for(int low = 0; low < s.length(); low++){
    		for(int len = 1; len < s.length() - low; len++){
    			String substr = s.substring(low,low+len);
    			String sorted = sortString(substr);
    			System.out.println("Basic string at " + low + "=[" + sorted + "]");
    			for(int pos = low+1; pos < s.length() - sorted.length() + 1; pos++){
    				String cand2 = s.substring(pos, pos+len);
    				if(isAnagram(sorted, cand2)){
    					total++;
    					System.out.println("   Found match [" + substr + "]~[" + cand2 + "]");
    				}
    			}
    		}
    	}
    	return total;
    }
    
    private static void showIt(String s){
    	long time = System.currentTimeMillis();
    	int total = sherlockAndAnagrams(s);
    	time = System.currentTimeMillis() - time;
    	System.out.println("Total for [" + s + "] is " + total + " in " + time + "ms");
    }
    
    @Test
    public void testSherlocks(){
    	showIt("hello wold");
    }
    
    static void checkMagazine(String[] magazine, String[] note) {
    	Map<String,Integer> magMap = new HashMap<>();
    	// Build map of magazine words.
    	for(String word : magazine){
    		Integer count = magMap.get(word);
    		if(count == null){
    			magMap.put(word, 1);
    		}else{
    			magMap.put(word, count+1);
    		}
    	}
    	for(String word : note){
    		Integer count = magMap.get(word);
    		if(count == null || count == 0){
    			System.out.println("No=");
    			return;
    		}
    		magMap.put(word, count - 1);
    	}
    	System.out.println("Yes");
    }
    
    static long repeatedString(String s, long n) {
    	if(s.length() == 0 || n <= 0){
    		return 0;
    	}
    	long totalInS = occurs(s, s.length() + 0L);
    	long rem = n % s.length();
    	long occs = n / s.length();
    	return totalInS * occs + occurs(s, rem); 
    }
    
    // Complete the activityNotifications function below.
    static long getMedian2(int[] exp, int begin, int end) {
        List<Integer> actual = new ArrayList<>();
        for(int inx = begin; inx < end; inx++) {
            actual.add(exp[inx]);
        }
        Collections.sort(actual);
        int mid = exp.length / 2;
        if ((actual.size() & 1) == 1) {
            return actual.get(mid) + actual.get(mid);
        }
        return actual.get(mid - 1) + actual.get(mid);
    }

    static int activityNotifications2(int[] expenditure, int d) {
        int total = 0;
        Long minspend = null;
        for (int index = d; index < expenditure.length; index++) {
            long spend = expenditure[index];
            if(minspend == null){
                minspend = spend;
            } 
            if(spend < minspend){
                minspend = spend;
                continue;
            }
            long median2 = getMedian2(expenditure, index - d, index);
            if (spend >= median2)
                total++;
        }
        return total;
    }  

    
    static int findIndex(List<Integer> values, int val){
    	int lo = 0; 
    	int hi = values.size();
    	
    	while(lo < hi){
    		int mid = (lo + hi) / 2;
    		int midVal = values.get(mid);
    		if(midVal == val) {
    			return mid;
    		}
    		if(midVal > val){
    			hi = mid;
    		}else {
    			lo = mid + 1;
    		}
    	}
    	return lo;
    }

    static void replaceValue(LinkedList<Integer> values, int oldVal, int newVal){
    	if(oldVal == newVal) {
    		return; // No change
    	}
    	
    	int pos = findIndex(values, oldVal);
    	
    	if(values.get(pos) == oldVal) {
    		values.remove(pos);
    	}else{
    		throw new RuntimeException("Did not find old value " + oldVal);
    	}
    	pos = findIndex(values, newVal);
    	if(pos >= values.size()){
    		values.add(newVal);
    	}else {
	    	if(values.get(pos) <= newVal) {
	    		values.add(pos, newVal);
	    	}else {
	    		values.add(pos+1, newVal);
	    	}
    	}
    }
    
    static int activityNotifications0(int[] expenditure, int d) {
        int total = 0;   
        LinkedList<Integer> values = new LinkedList<>();
        for(int inx = 0; inx < d; inx++) {
            values.add(expenditure[inx]);
        }
        Collections.sort(values);
        int mid = d/2;
        boolean update = false;
        for (int index = d; index < expenditure.length; index++) {
        	if(update){
        		int oldVal = expenditure[index - d -1];
        		int newVal = expenditure[index - 1];
        		replaceValue(values, oldVal, newVal);
        	}
        	update = true;
            long spend = expenditure[index];
            long median2 = ((d & 1) == 1) ? values.get(mid) * 2 :
            	values.get(mid) + values.get(mid + 1);
            if (spend >= median2)
                total++;
        }
        return total;
    }  


    static void replaceVal(TreeSet<Integer> values, int oldVal, int newVal){
    	if(oldVal == newVal) {
    		return; // No change
    	}
    	values.remove(oldVal);
    	values.add(newVal);
    }

    static int getMedian(TreeSet<Integer> set, int beg, boolean next) {
		Iterator<Integer> it = set.iterator();
		for(int inx = 0; inx < beg; inx++) {
			it.next();
		}
		int result = it.next();
		if(next) {
			return result + it.next();
		}
		return result + result;
    }
    
    
   // @Test
   public void testMedian() {
	   int[] values = { 1, 4, 5, 6, 3, 6, 2, 5 };
	   for(int index = 3; index <= values.length; index++) {
		   double d = getMedian2(values, index - 3, index-1);
		   log("Median for " + index + " is " + d/2);
	   }
   }
    
   static class TreeList{
    	private TreeMap<Integer, Integer> map = new TreeMap<>();
    	private int size = 0;
    	
    	public void add(Integer value) {
    		int count = map.computeIfAbsent(value, k -> 0) + 1;
    		map.put(value, count);
    		size++;
    	}
    	
    	public void remove(Integer value) {
    		int count = map.computeIfAbsent(value, k -> 0);
    		if(count == 1){
    			map.remove(value);
    		}else {
        		map.put(value, count-1);
    		}
    		size--;
    	}
    	
    	public int first() {
    		return map.firstKey();
    	}

    	public int last() {
    		return map.lastKey();
    	}
    	
    	public int size() {
    		return size;
    	}
    	
    	@Override
    	public String toString(){
    		StringBuilder sb = new StringBuilder();
    		for(Integer key : map.keySet()) {
    			int count = map.get(key);
    			while(count-- > 0) {
    				if(sb.length() > 0) {
    					sb.append(",");
    				}
    				sb.append(key);
    			}
    		
    		}
    		return "[" + sb.toString() + "]";
    	}
    }
    
    static class Median{
    	private int lowSize;
    	private int highSize;
    	private boolean odd;
    	private int median2;
    	
    	private TreeList lows = new TreeList();
    	private TreeList highs = new TreeList();

    	public Median(int[] values, int size) {
    		this.odd = (size & 1) == 1;
    		this.lowSize = size / 2;
    		this.highSize = size - lowSize;
    		for(int inx = 0; inx < size; inx++) {
    			lows.add(values[inx]);
    		}
    		balance();
    	}
    	
    	private void balance() {
    		while(lows.size() > lowSize) {
        		int top = lows.last();
    			lows.remove(top);
    			highs.add(top);
    		}
    		while(highs.size() > highSize) {
    			int bot = highs.first();
    			highs.remove(bot);
    			lows.add(bot);
    		}
    		if(highs.size() != highSize || lows.size() != lowSize) {
    			throw new RuntimeException("Median sizes not balanced");
    		}
    		this.median2 = highs.first() + (odd ? highs.first() : lows.last());
    	}
    	
    	public void update(int oldValue, int newValue){
    		if(oldValue == newValue) {
    			return;
    		}
    		if(lows.size() > 0 && oldValue <= lows.last()) {
        		lows.remove(oldValue);
    		}else {
    			highs.remove(oldValue);
    		}
     		if(lows.size() > 0 && newValue <= lows.last()) {
        		lows.add(newValue);
    		}else{
    			highs.add(newValue);
    		}
    		balance();
    	}
    	
    	public int getMedian2(){
    		return median2;
    	}
    	
    	public double getMedian() {
    		return median2 / 2.0;
    	}
    	
    	public String show() {
    		return lows.toString() + ":" + highs.toString() + " -> " + getMedian();
    	}
    }

    
    static int activityNotifications(int[] expenditure, int d) {
        int total = 0; 
        Median med = new Median(expenditure, d);
        for (int index = d; index < expenditure.length; index++) {
        	log("Median = " + med.show());
            long spend = expenditure[index];
            long median2 = med.getMedian2();
            if (spend >= median2)
                total++;
            med.update(expenditure[index-d], expenditure[index]);
        }
        return total;
    }  

    @Test
    public void testActivity() {
    	int v[] = {2, 3, 4, 2, 3, 6, 8, 4, 5};
		int acts = activityNotifications(v, 5);
		log("Acts for {} is {}", 5, acts);
    }

    @Test
    public void testActivity2() {
    	int v[] = {10, 20, 30, 40, 50};
		int acts = activityNotifications(v, 3);
		log("Acts for {} is {}", 3, acts);

    }

}
