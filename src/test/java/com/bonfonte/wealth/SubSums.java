package com.bonfonte.wealth;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
public class SubSums {
	
	static void log(String s) {
		System.out.println(s);
	}

    static int findLowBound(int[] arr) {
    	if(arr.length == 0) return 0;
    	Integer low = arr[0];
        for(int j = 0; j < arr.length; j++){
            if(arr[j] >= 0){
                return 0;
            }
            if(arr[j] > low){
                low = arr[j];
            }
        }
        return low;
    }

    static class PosVal{
        int pos;
        int val;
        public PosVal(int pos, int v){
            this.pos = pos;
            this.val = v;
        }
        
        @Override
        public String toString() {
        	return "[" + pos + ":" + val + "]";
        }
    }

    static boolean areApart(PosVal p1, PosVal p2){
        return p1 == null ? true : Math.abs(p2.pos - p1.pos) > 1;  
    }

    static List<PosVal> listPosVals(int[] arr, int lowBound){
        List<PosVal> vals = new ArrayList<>(); 
        log("lowBound is " + lowBound);
        for(int j = 0; j < arr.length; j++){
            if(arr[j] <= lowBound) continue;
            PosVal val = new PosVal(j, arr[j]);
            vals.add(val);
        }
        return vals;
    }
    
    static BitSet lowerSet(String s) {
    	BitSet bits = new BitSet(s.length());
    	int index = 0;
    	for(char a : s.toCharArray()) {
    		bits.set(index++, 'a' <= a && a <= 'z' );
    	}
    	return bits;
    }

    static Map<Integer, PosVal> getIsolated(List<PosVal> sorted){
        Map<Integer, PosVal> isolated = new HashMap<>();
        PosVal prev = null;
        PosVal prev2 = null;

        // e.g. [ 1, 3, 4, 6, 8, 9 ] only 1 and 6 are isolated - they can be added to any subset.
        for(int j = 0; j < sorted.size(); j++){
        		
        	PosVal val = sorted.get(j);
            if(prev == null ){
                prev = val;
                continue;
            }
            if(areApart(prev2, prev) && areApart(prev, val)){
                isolated.put(prev.pos, prev);
            }
            prev2 = prev;
            prev = val;
        }
        if(areApart(prev2, prev)) {
        	isolated.put(prev.pos, prev);
        }
        return isolated; 
    }
    
    
    // Assumes the vals are in sort order by position.
    static List<List<PosVal>> getGroups(List<PosVal> vals){
    	List<List<PosVal>> res = new ArrayList<>();
    	if(vals.isEmpty()) return res;
    	PosVal prev = vals.get(0);
    	List<PosVal> current = new ArrayList<>();
    	for(PosVal val : vals) {
    		if(areApart(val, prev)) {
    			res.add(current);
    			current = new ArrayList<>();
    		}
    		current.add(val);
    		prev = val;
    	}
    	if(current.size() > 0) {
    		res.add(current);
    	}
    	return res;
    }

    static int getMaxFromGroup(List<PosVal> vals, int from) {
        if(from >= vals.size()) return 0;
        int here = vals.get(from).val;
        int max1 = here + getMaxFromGroup(vals, from+2);
        int max2 = here + getMaxFromGroup(vals, from+3);
        if(from == 0) {
            int max3 = getMaxFromGroup(vals, from+1);
            max2 = Math.max(max3, max2);
        }
        return Math.max(max1, max2);
    }
    
    // Complete the maxSubsetSum function below.
    static int maxSubsetSum(int[] arr) {
        int lowBound = findLowBound(arr);
        if(lowBound < 0) return lowBound; // These can never addup to more than lowbound
        List<PosVal> vals = listPosVals(arr, lowBound);
        log("Original arraysize: total = " + arr.length);
        log("Initial       vals: total = " + vals.size() + " -> " + vals);
        Map<Integer, PosVal> isolated = getIsolated(vals);
        vals = vals.stream().filter(v -> !isolated.containsKey(v.pos))
            .collect(Collectors.toList());
        int isoTotal = 0;
        for(PosVal pv : isolated.values()) {
        	isoTotal += pv.val;
        }
        log("Connected     vals: total = " + vals.size() + " -> " + vals);
        log("Isolated      vals: total = " + isolated.size() + " -> total value = " + isoTotal + ": " + isolated.values());
        
        List<List<PosVal>> groups = getGroups(vals);
        int count = 0;
        for(List<PosVal>group : groups) {
        	count += group.size();
        }
        log("Groups        vals: total = " + count + " (from " + groups.size() + " groups)");
        
        int total = isoTotal;
        for(List<PosVal>group : groups) {
        	total += getMaxFromGroup(group, 0);
        }
        return total;
    }
    
    public static void runFileTest(String name, int expected) {
		File file = new File("/Users/andy/wspaces/data/" + name);
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			runTest(input, expected);
		}catch(Exception e) {
			log("Ooops .. failed to process file " + e.getMessage());
		}
		try {
			if(input != null) input.close();
		}catch(Exception e) {
			log("Ooops .. failed to close input " + e.getMessage());
		}
    }

    public static void runStringTest(String arg, int expected) throws IOException {
        // BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
    	String[] data = arg.split(" ");
		String input = data.length + "\n" + arg;
		InputStream stream = new ByteArrayInputStream(input.getBytes());
		runTest(stream, expected);
    }

    public static void runTest(InputStream stream, int expected) throws IOException {
		Scanner scanner = new Scanner(stream);
		
		int n = scanner.nextInt();
		scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
		
		int[] arr = new int[n];
		
		String[] arrItems = scanner.nextLine().split(" ");
		scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
		
		for (int i = 0; i < n; i++) {
		    int arrItem = Integer.parseInt(arrItems[i]);
		    arr[i] = arrItem;
		}
		
		int res = maxSubsetSum(arr);
		
		log(String.valueOf(res) + " " + (res == expected ? "SUCCESS" : ("FAILED - EXPECTED " + expected)));
		scanner.close();
    }

    
    public static void main(String[] args) throws IOException {
//    	runStringTest("1", 1);
//    	runStringTest("1 2", 2);
//    	runStringTest("1 1 1", 2);
    	runStringTest("7 1 3 6 2 -1 8", 21);
//    	runFileTest("subsum.input", 151598486);
    }
}
