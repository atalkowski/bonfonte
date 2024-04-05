package com.hack.bestpath;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.hack.bestpath.Permute.Permutation;

public class Solution2 {

    static boolean dbgon = false;
    static void log(String s){
        System.out.println(s);
    }

    static void dbg(String s){
        if(dbgon) log(s);
    }
    
    private static <T> List<T> intersect(List<T> a, List<T> b){
    	return a.stream()
    			.filter(t -> b.contains(t))
    			.collect(Collectors.toList());
    }

    private static <T> List<T> remove(List<T> list, T t){
    	return list.stream().filter(s -> !s.equals(t))
    			.collect(Collectors.toList());
    }

    private static <T> List<T> remove(List<T> list, List<T> t){
    	return list.stream().filter(s -> !t.contains(s))
    			.collect(Collectors.toList());
    }

    static class Grid{
        char[][] data;
        int height;
        int width;
        public Grid(String[] data) {
        	this.data = new char[data.length][];
        	for(String line : data) {
        		this.data[height++] = line.toCharArray();
        	}
        	width = data[0].length();
        }
        
        public char charAt(int x, int y) {
        	return data[y][x];
        }
        
        public void putChar(int x, int y, char c) {
        	data[y][x] = c;
        }
        
        public String cross(int x, int y, int l) {
        	String s = "";
        	for(int xx = x; xx < x + l; xx++) {
        		s += charAt(xx, y);
        	}
        	return s;
        }

        public String column(int x) {
        	return down(x, 0, height);
        }
        
        public String row(int y) {
        	return cross(0, y, width);
        }
        
        public String down(int x, int y, int l) {
        	String s = "";
        	for(int yy = y; yy < y + l; yy++) {
        		s += charAt(x, yy);
        	}
        	return s;
        }
        
        public void putDown(String s, int x, int y) {
        	for(int i = 0; i < s.length(); i++) {
        		putChar(x, y+i, s.charAt(i));
        	}
        }

        public void putCross(String s, int x, int y) {
        	for(int i = 0; i < s.length(); i++) {
        		putChar(x+i, y, s.charAt(i));
        	}
        }
    }

    static class Word{
        int s; // Start from edge
        int e; // End 
        int len; // Length
        boolean down = false;
        int x, y;// Coords
        String answer;
        
        List<String> possibles = new ArrayList<>(); // A list of words that might fit
        Map<Word, Integer> intersects = new LinkedHashMap<>();
        Word(int s, int e){
            this.s = s;
            this.e = e;
            this.len = e - s;
        }
        
        @Override
        public String toString(){
            return "[" + x + "," + y + "(" + len + ")]"; 
        }

    }
    
    ///////// Node distance problem

    static class Node{
        long color;
        int id;
        List<Node> links = new ArrayList<>();
        public Node(int id, long color){
            this.id = id;
            this.color = color;
        }
        public void connect(Node link){
            links.add(link);
        }
        
        public Path initPath() {
            return new Path(this);
        }
     }

     static class Path{
         int edges = 0;
         Node origin;
         Node node;
         
         public Path(Node o) {
             this.origin = o;
             this.node = o;
         }

         public Path(Path o, Node n) {
             this.origin = o.origin;
             this.node = n;
             this.edges = o.edges + 1;
         }

         public int add(Node node) {
             this.node = node;
             edges++;
             return edges;
         }
         
         
     }
     
     static int findShortest(List<Node> colors, Node root, long color) {
         if(colors == null || colors.size() <= 1) {
             return -1;
         }
         int distance = -1;
         List<Integer> rootDistances = new ArrayList<>();

         Path rootPath = root.initPath();
         List<Path> paths = new ArrayList<>();
         paths.add(rootPath);
         for(Node col : colors){
             paths.add(col.initPath());
         }

         while(paths.size() > 0) {
             List<Path> next = new ArrayList<>();
             for(Path path : paths) {
                 Node node = path.node;
                 for(Node link : node.links){
                     if(link.color == color) {
                         int dist = path.edges + 1;
                         if(node.color != color){
                             if(rootDistances.size() < 2){
                                 rootDistances.add(dist);
                             }
                         }else{
                             if(distance == -1 || dist < distance) {
                                 distance = dist;
                                 if(distance == 1) return distance;
                             }
                         }
                     }
                     Path linkPath = new Path(path, link);
                     next.add(linkPath);
                 }
             }
             paths = next;
         }
         if(distance == -1 && rootDistances.size() > 1){
             return rootDistances.get(0) + rootDistances.get(1);
         }
         return distance;
     }

     static int findShortest(int graphNodes, int[] graphFrom, int[] graphTo, long[] ids, int val) {
        List<Node> nodes = new ArrayList<>(graphNodes);
        List<Node> colors = new ArrayList<>();
        long target = val + 0L; 
        for(int i = 0; i < graphNodes; i++){
            Long color = ids[i];
            Node node = new Node(i+1, color);
            nodes.add(node);
            if(color != target){
                continue;
            }
            colors.add(node);
        }
        
        for(int i = 0; i < graphFrom.length; i++){
            Node from = nodes.get(graphFrom[i] - 1);
            Node to = nodes.get(graphTo[i] - 1);
            from.connect(to);
        }
        int dist = findShortest(colors, nodes.get(0), val + 0L);
        log("Found distance = " + dist);
        return dist;
    }

    static class Puzzle{
        List<Word> acc = new ArrayList<>();
        List<Word> dwn = new ArrayList<>();
        List<Word> all = new ArrayList<>();
        List<String> words;

        Grid grid;
        
        public Puzzle(String[] crossword, String words){
            grid = new Grid(crossword);
            this.words = new ArrayList<>();
            for(String word : words.split(";")){
                this.words.add(word);
            }
            buildWordsLocations();
            buildIntersections();
        }

        private List<Word> findWords(String line, int x, int y){
            List<Word> result = new ArrayList<>();
            int pos = line.indexOf('-');
//            dbg("Findwords");
            while(pos >= 0 && pos < line.length()){
                int endPos = pos+1;
                while(endPos < line.length() && line.charAt(endPos) == '-') endPos++;
                Word word = new Word(pos, endPos);
                if(word.len > 1){
                    result.add(word);
                    if(x < 0){
                        word.x = pos;
                        word.y = y;
                    }else{
                        word.x = x;
                        word.y = pos;
                        word.down = true;
                    }
                    // Add in a list of words that might work here;
                    for(String poss : this.words) {
                    	if(poss.length() == word.len) {
                    		word.possibles.add(poss);
                    	}
                    }
                }
                if(endPos >= line.length()) break;
                pos = line.indexOf('-', endPos);
            }
//            dbg("Found words :" + result);
            return result;
        }

        private List<Word> findDownWords(int pos){
            String line = grid.column(pos);
//            dbg("Down line " + pos + " = " + line);
            return findWords(line, pos, -1);
        }

        private List<Word> findCrossWords(int pos){
            String line = grid.row(pos);
//            dbg("Cross line " + pos + " = " + line);
            return findWords(line, -1, pos);
        }

        private void buildWordsLocations(){
            acc = new ArrayList<>();
            dwn = new ArrayList<>();
            for(int pos = 0; pos < grid.height; pos++ ){
                acc.addAll(findCrossWords(pos));
            }
            for(int pos = 0; pos < grid.width; pos++ ){
                dwn.addAll(findDownWords(pos));
            }
            all.addAll(acc);
            all.addAll(dwn);
 //           log("Cross word locs:" + acc);
 //           log("Down word locs:" + dwn);   
        }
        
        private void buildIntersections() {
        	for(Word word : acc) {
        		int left = word.x;
        		int right = word.x + word.len - 1;
        		for(Word down : dwn) {
        			if(left <= down.x &&  down.x <= right
        					&& down.y <= word.y && word.y <= down.y + down.len - 1) {
        				int wordchpos =  down.x - word.x;
        				int downchpos = word.y - down.y;
        				word.intersects.put(down, wordchpos); 
        				down.intersects.put(word, downchpos);
 //       				log(" ... cross " + word + " intersects " + down + " at " + wordchpos);
 //       				log(" ...  down " + down + " intersects " + word + " at " + downchpos);
        			}
        		}
        	}
        }

        private void updateGrid(Word word, String s) {
        	if(s == null) {
        		s = "-";
        		while(s.length() < word.len) s += "-";
        	}
        	if(word.down) {
//        		log("Processing down word " + word);
        		grid.putDown(s, word.x, word.y);
        	}else {
//        		log("Processing cross word " + word);
        		grid.putCross(s, word.x, word.y);
        	}
        }
        
        private void clearGrid() {
        	for(Word word : acc) updateGrid(word, null);
        	for(Word word : dwn) updateGrid(word, null);
        }

        private void populateGrid() {
        	for(Word word : acc) updateGrid(word, word.answer);
        	for(Word word : dwn) updateGrid(word, word.answer);
        }

        private List<List<String>> findDownSolutions(Word cross, List<String> remaining){
       		List<List<String>> solutions = new ArrayList<>();
       		List<String> allowedWords = cross.intersects.keySet().stream()
       				.map(word -> word.possibles)
       				.flatMap(s -> s.stream())
       				.filter(text -> remaining.contains(text))
       				.collect(Collectors.toList());
       		
       		List<Word> downs = cross.intersects.keySet().stream().collect(Collectors.toList());
       		List<String> answers = downs.stream().map(w -> w.answer)
       				.filter(Objects::nonNull)
       				.collect(Collectors.toList());
       		if(answers.size() == downs.size()) {
//       			log("All down solutions already solved " + answers);
       			solutions.add(answers);
       			return solutions;
       		}

       		List<Word> unsolvedDowns = downs.stream().filter(w -> w.answer == null)
       				.collect(Collectors.toList());
       		Permutation<String> permutes = new Permutation<>(allowedWords, unsolvedDowns.size());
       		while(permutes.hasNext()){
       			List<String> permute = permutes.next()
       					.stream().limit(downs.size())
       					.collect(Collectors.toList());
       			List<String> allow = new ArrayList<>(remaining);
       			int index = 0;
       			while(index < unsolvedDowns.size()) {
       				Word down = unsolvedDowns.get(index);
       				String solution = permute.get(index);
       				if(allow.contains(solution)) {
       	    			int wordPos = cross.intersects.get(down);
       	    			char ch = cross.answer.charAt(wordPos);
       	    			int downPos = down.intersects.get(cross);
       	    			char downCh = solution.charAt(downPos);
       	    			if (ch == downCh) {
       	    				index++;
//       	    				log("Bingo - found solution " + solution + " for down " + down);
           					allow.remove(solution);
       	    				continue;
       	    			}
       				}
       				break; // This permutation does not work... try another
       			}
       			if(index == unsolvedDowns.size()) {
//          			log("Bingo found plausible solution for unsolved  " + unsolvedDowns.size() + " downs:" + permute);
           			List<String> solved = new ArrayList<>();
           			index = 0;
           			for(Word word : downs) {
           				if(word.answer == null) {
           					solved.add(permute.get(index++));
           				}else {
           					solved.add(word.answer);
           				}
           			}
//           			log("Adding part solution " + solved);
           			solutions.add(solved);
       			}
    		}
       		return solutions;
        }
        
        private void populateDownsWithWords(List<String> soln, Word cross) {
       		List<Word> downs = cross.intersects.keySet().stream().collect(Collectors.toList());
       		int index = 0;
       		for(Word down : downs) {
       			down.answer = soln == null ? null : soln.get(index++);
//       			log("Setting down " + down + " with word " + down.answer);
       		}
        }
        
        private boolean solve(List<Word> crosses, int pos, List<String> available) {
//        	log("\n\nSolve cross item " + pos + " using one of " + available);
        	if(pos >= crosses.size()) return true;
        	Word cross = crosses.get(pos);
        	List<String> maybes = intersect(cross.possibles, available);
        	for(String maybe : maybes) {
        		cross.answer = maybe;
//        		log("Possible answer at " + cross + " = " + maybe);
        		List<String> remaining = remove(available, maybe);
        		List<List<String>> solns = findDownSolutions(cross, remaining);
        		for(List<String> soln : solns) {
        			populateDownsWithWords(soln, cross);
        			List<String> remainder = remove(remaining, soln);
        			if(solve(crosses, pos+1, remainder)) return true;
        			populateDownsWithWords(null, cross);
        		}
         		cross.answer = null;
        	}
        	return false;
        }
        
        public String[] solve(){
//        	log("Here is the grid initially");
//        	print(true);
        	clearGrid();
    		if(solve(acc, 0, this.words)){
    			log("Solved OK!");
    			populateGrid();
    		}else {
    			log("Failed to solve!!");
    		}
            return print(true);
        }

        public String[] print(boolean display){
            String[] result = new String[grid.height]; 
            for(int y = 0; y < grid.height; y++) {
            	result[y] = grid.row(y);
            	if(display)
            		log(result[y]);
            }
            return result;
        }
    }


    // Complete the crosswordPuzzle function below.
    static String[] crosswordPuzzle(String[] crossword, String words) {
        Puzzle puz = new Puzzle(crossword, words);
        return puz.solve();
    }
    
    private static String input1 = "+-++++++++\n" + 
    		"+-++++++++\n" + 
    		"+-++++++++\n" + 
    		"+-----++++\n" + 
    		"+-+++-++++\n" + 
    		"+-+++-++++\n" + 
    		"+++++-++++\n" + 
    		"++------++\n" + 
    		"+++++-++++\n" + 
    		"+++++-++++\n" + 
    		"LONDON;DELHI;ICELAND;ANKARA" ;
    
    private static String input = "XXXXXX-XXX\n" + 
    		"XX------XX\n" + 
    		"XXXXXX-XXX\n" + 
    		"XXXXXX-XXX\n" + 
    		"XXX------X\n" + 
    		"XXXXXX-X-X\n" + 
    		"XXXXXX-X-X\n" + 
    		"XXXXXXXX-X\n" + 
    		"XXXXXXXX-X\n" + 
    		"XXXXXXXX-X\n" + 
    		"ICELAND;MEXICO;PANAMA;ALMATY";

    private static Scanner scanner = //new Scanner(System.in);
    		new Scanner(new ByteArrayInputStream(input.getBytes()));
    
    public static void main3(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/tmp/crossword.out"));

        String[] crossword = new String[10];

        for (int i = 0; i < 10; i++) {
            String crosswordItem = scanner.nextLine();
            crossword[i] = crosswordItem;
        }

        String words = scanner.nextLine();

        String[] result = crosswordPuzzle(crossword, words);

        for (int i = 0; i < result.length; i++) {
            bufferedWriter.write(result[i]);

            if (i != result.length - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
    
    public static void main2(String[] args){
        for(int n = 2; n <= 4; n++) {
        	int[] chooses = { 0, 2 };
        	for (int choose : chooses) {
	        	Permute p = new Permute(n, choose);
	        	log("\nPrinting permute for " + n + " Choose " + choose);
	        	int total = 0;
		        while(p.hasNext()) {
		        	List<Integer> list = p.next();
		        	total ++;
		        	log(total + " permute = " + list);
		        }
        	} 
        }  
    }
    
    //private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/tmp/pathDistance.out"));
        String input1 = "5 4\n" + 
        		"1 2\n" + 
        		"1 3\n" + 
        		"2 4\n" + 
        		"3 5\n" + 
        		"1 2 3 3 2\n" + 
        		"2";
        
        String input2 = "4 3\n" + 
        		"1 2\n" + 
        		"1 3\n" + 
        		"4 2\n" + 
        		"1 2 1 1 \n" + 
        		"1";
        InputStream fromString = new ByteArrayInputStream(input.getBytes()); // When needed
        
        scanner.close();
        File initialFile = new File("/Users/andy/wspaces/data/distance.txt");
        InputStream input = new FileInputStream(initialFile);
        
        scanner = new Scanner(input);
        String[] graphNodesEdges = scanner.nextLine().split(" ");
        int graphNodes = Integer.parseInt(graphNodesEdges[0].trim());
        int graphEdges = Integer.parseInt(graphNodesEdges[1].trim());

        int[] graphFrom = new int[graphEdges];
        int[] graphTo = new int[graphEdges];

        for (int i = 0; i < graphEdges; i++) {
            String[] graphFromTo = scanner.nextLine().split(" ");
            graphFrom[i] = Integer.parseInt(graphFromTo[0].trim());
            graphTo[i] = Integer.parseInt(graphFromTo[1].trim());
        }

        long[] ids = new long[graphNodes];

        String[] idsItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < graphNodes; i++) {
            long idsItem = Long.parseLong(idsItems[i]);
            ids[i] = idsItem;
        }

        int val = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int ans = findShortest(graphNodes, graphFrom, graphTo, ids, val);

        bufferedWriter.write(String.valueOf(ans));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
