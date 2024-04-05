package com.bonfonte.wealth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HeapStuff {
    static void log(String s){
        System.out.println(s);
    }


    static class Heap{
        Heap l, r;
        String data;
        int count = 1;
        public Heap(String d){
            this.data = d;
        }

        public void push(String s){
            count++;
            int comp = data.compareTo(s);
            if(comp <= 0){ // Figure out which item stays on top:
                String temp = data;
                data = s;
                s = temp;
            }
            if(l == null) {
                l = new Heap(s);
                return;
            }
            if(r == null) {
                r = new Heap(s);
                return;
            }
            if(l.count > r.count){
                r.push(s);
                return;
            }
            l.push(s);
        }
        
        public List<String> asList(){
            List<String> values = new ArrayList<>();
            Stack<Heap> s = new Stack<>();
            s.add(this);
            while(s.size() > 0) {
                Heap h = s.pop();
                values.add(h.data);
                if(h.l != null) s.push(h.l);
                if(h.r != null) s.push(h.r);
            }
            return values;
        }
        
        public List<String> asSortedList(){
            List<String> values = asList();
            Collections.sort(values);
            return values;
        }

        private int status() {
            int res = 0;
            if(l != null) res += 1;
            if(r != null) res += 2;
            return res;
        }
        
        private void leftPop() {
            this.data = l.pop();
            if(l.count == 0) {
                this.l = null;
            }
        }

        private void rightPop() {
            this.data = r.pop();
            if(r.count == 0) {
                this.r = null;
            }
        }
        
        public String pop(){
            // This should remove the head and adjust the heap so that it is 
            // remains a heap;
            String res = this.data;
       //     log("Popping from [" + data + "] count=" + count + " and status=" + status());
            switch(status()){
            case 3: 
                if(r.data.compareTo(l.data) < 0) {
                    // Promote L;
                    leftPop();
                }else {
                    rightPop();
                }
                break;
            case 2:
                rightPop();
                break;
            case 1:
                leftPop();
                break;
            default:
                break; // Do nothing
            }
            count--;
            return res;
        }

        public void pushpop(String s){
            if(lessThanOrEqual(s)){
                return; // No point pushing this ... it would be popped anyway
            }
            push(s);
            pop();
        }

        public boolean lessThanOrEqual(String s){
            return data.compareTo(s) <= 0;  
        }

        public boolean greaterThan(String s){
            return data.compareTo(s) > 0;  
        }
        
        private String toString(String name, int depth) {
            String res = "";
            for(int p = 0; p < depth; p++) {
                res += "   ";
            }
            res += name + ":count=" + count + "  [" + data + "]";
            if(l != null) {
                res += "\n";
                res += l.toString("--left", depth + 1);
            }
               if(r != null) {
                res += "\n";
                res += r.toString("-right", depth + 1);
            }
               return res;
        }
        
        public String toString() {
            return toString("root", 0);                  
        }
    }

    static int[] initResult(int[] queries){
        int[] result = new int[queries.length];
        for(int i = 0; i < queries.length; i++){
            result[i] = -1;
        }
        return result;
    }

    static List<String> sortPalis(List<String> list){
    	Map<String,Integer> map = new TreeMap<>();
    	for(String s : list) {
    		Integer tot = map.computeIfAbsent(s, t -> 0) + 1;
    		map.put(s, tot);
    	}
    	List<String> res = new ArrayList<>();
    	for(Entry<String, Integer> e : map.entrySet()) {
    		for(int i = 1; i <= e.getValue(); i++) {
    			res.add(e.getKey());
    		}
    	}
    	return res;
    }
    
    static List<String> findPalisAtPos(String s, int pos){
  //  	log("Searching for palis in [" + s + "] at pos " + pos);
        List<String> res = new ArrayList<>();
        int lo = pos;
        int hi = pos;
        while(lo >= 0 && hi < s.length()){
            if(s.charAt(lo) == s.charAt(hi)){
                res.add(s.substring(lo--, 1 + hi++));
            } else break;
        }
        lo = pos;
        hi = pos + 1;
        while(lo >= 0 && hi < s.length()){
            if(s.charAt(lo) == s.charAt(hi)){
                res.add(s.substring(lo--, 1 + hi++));
            } else break;
        }
        return res;
    }

    static List<String> findPalis(String s){
        List<String> res = new ArrayList<>();
        for(int i = 0; i < s.length(); i++){
            res.addAll(findPalisAtPos(s, i));
        }
        return res;
    }

    static List<String> findAllPalis(String s, int max){
        List<String> palis = findPalis(s);
        if(palis.isEmpty()) return palis;
//        Heap heap = new Heap(palis.get(0));
//
//        for(int i = 1; i < palis.size(); i++){
//            String pali = palis.get(i);
//            if(heap.count < max){
//                heap.push(pali);
//            }else{
//                heap.pushpop(pali);
//            }
//        }
//        return heap.asSortedList();
        // Collections.sort(palis);
        return sortPalis(palis);
    }

    static int findMax(int[] arr){
        if(arr.length == 0) return 0;
        int max = arr[0];
        for(int i = 1; i < arr.length; i++){
            if(arr[i] > max) max = arr[i];
        }
        return max;
    }

    static int wconvert(String s){
        if(s == null || s.length() == 0) return -1;
        long mult = 1;
        long mod = 1000000007L;
        long total = 0L;
        for(int i = 0; i < s.length(); i++){
            total = mult * ((long) s.charAt(i)) + total; 
            if(total > mod){
                total = total % mod;
            }
            mult = mult * 100001L;
            if(mult > mod) {
            	mult = mult % mod;
            }
        }
        return (int)total;
    }

    // Complete the solve function below.
    static int[] solve(String s, int[] queries) {
        int[] result = initResult(queries);
        if(s == null || s.length() == 0 || queries.length == 0) return result;
        List<String> shortList = findAllPalis(s, findMax(queries));
        List singles = shortList.stream().filter(foo -> foo.length() == 1).collect(Collectors.toList());
        log("Found " + shortList.size() + " palis and " + singles.size() + " single letter variants");
        
        String pals = "";
        for(int j = 0; j < shortList.size(); j++) {
        	pals = pals + " (" + j + ")" + shortList.get(j);
        	if(j % 10 == 9) {
//        		log(pals);
        		pals = "";
        	}
        }
//        log(pals);
        Map<String, Integer> map = new HashMap<>();
        for(int inx = 0; inx < queries.length; inx++){
        	int query = queries[inx];
        	int q = query - 1;
            if(q < shortList.size() && q >= 0){
                // TODO optimize with saved map:
                result[inx] = wconvert(shortList.get(q)); 
            }
        }
        return result;
    }

    
//    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
   //     BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
  
    	String input1 = "5 7\n" + 
    			"abcba\n" + 
    			"1\n" + 
    			"2\n" + 
    			"3\n" + 
    			"4\n" + 
    			"6\n" + 
    			"7\n" + 
    			"8";
		File file = new File("/Users/andy/wspaces/data/queries6.txt");
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		}catch(Exception e) {
			log("Ooops .. failed to process file " + e.getMessage());
			return;
		}
		
//    	Scanner scanner = new Scanner(new ByteArrayInputStream(input1.getBytes()));
    	Scanner scanner = new Scanner(input);

        String[] nq = scanner.nextLine().split(" ");

        int n = Integer.parseInt(nq[0]);

        int q = Integer.parseInt(nq[1]);

        String s = scanner.nextLine();

        int[] queries = new int[q];

        for (int queriesItr = 0; queriesItr < q; queriesItr++) {
            long queriesItemLong = scanner.nextLong();
            int queriesItem = (int)queriesItemLong;
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
            queries[queriesItr] = queriesItem;
        }

        int[] result = solve(s, queries);

        for (int resultItr = 0; resultItr < result.length; resultItr++) {
            //bufferedWriter.write(String.valueOf(result[resultItr]));
        	log(String.valueOf(result[resultItr]));
            if (resultItr != result.length - 1) {
                //bufferedWriter.write("\n");
        //    	log("");
            }
        }

//        bufferedWriter.newLine();

//        bufferedWriter.close();
        if(input != null) input.close();
        scanner.close();
//        for(char ch = 'a'; ch < 'z'; ch++) {
//        	log("Convert " + ch + "=" + wconvert("" + ch));
//        }
    }
}
