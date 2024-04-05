package com.hack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.Test;


public class StreamTests {

	private static void log(String s) {
		System.out.println( s );
	}
	
	private static <T> void print(Stream<T> stream) {
		stream.forEach(o -> log(o == null ? "null" : o.toString()));
	}
	
	@Test
	public static void testNumberAndFileStreamFuncs() {
		
		// Creating integer and long streams:
		IntStream intStream = IntStream.range(1, 3);
		LongStream longStream = LongStream.rangeClosed(1, 3);
		intStream.forEach(i ->  log("int range(1, 3) : part " + i));
		longStream.forEach(k -> log("long rangeClosed(1, 3) : part " + k));
		// Creating a stream of lines from a file: 
		Path path = Paths.get("/Users/andy/bin/rotate");
		try {
			Stream<String> streamOfStrings = Files.lines(path);
			streamOfStrings.limit(10).forEach(s -> log("rotate:" + s));
			streamOfStrings.close();
		}catch(IOException e) {
			log("Error " + e.getMessage());
		}
	}
	
	@Test
	public static void testStreamReducedFuncs() {
		int reducedParams = Stream.of(1, 2, 3)
				  .reduce(10, (a, b) -> a + b,
						  (a, b) -> { log("combiner should not be called"); return a + b;} // Combiner not needed!
						  );		
		int reducedParallel = Arrays.asList(1, 2, 3).parallelStream()
			    .reduce(10, // Initial value
			    	(a, b) -> a + b, // Accumulator
			    	(a, b) -> { // Combiner
			       log("combiner was called");
			       return a + b;
			    });
		log("Totals for reducers: direct call = " + reducedParams + " v parallel call = " + reducedParallel);
		
	}

	private static List<String> getProductStrings(){
		// Using Stream.<type>builder() :
		Stream<String> streamBuilder =
				  Stream.<String>builder().add("Apples")
				  .add("Bananas")
				  .add("Carrots")
				  .add("Cabbages")
				  .add("Dandelions")
				  .add("Egg-plants")
				  .add("Fennel")
				  .add("A spoonful of sugar")
				  .build();
		return streamBuilder.collect(Collectors.toList());
	}

	@Test
	public static void testStreamJoinerFuncs() {
		Stream<String> stream = Stream.of("The", "cat", "sat", "on the", "mat");
		String sentence = stream.collect(Collectors.joining(" ", /* delimiter */
						  "{", "}")); /* Optional Prefix and Suffix */
		log(sentence);
	}
	
	
	@Test
	public static void testStreamBuildAndJoinerFuncs() {
		String listToString = getProductStrings().stream().map(s -> "\"p" + s.length() + "\":\"" + s + "\"")
				  .collect(Collectors.joining(
						  ",\n   ", // delimiter
						  "{",  // Prefix
						  "}"));// Suffix
		log(listToString);
		
	}
	
	@Test
	public static void testStreamsCollectMap() {
		String[] eng = "one two three four five six seven eight nine ten".split(" ");
		String[] fre = "un deux trois quatre cinq six sept huit neuf dix".split(" ");		
		Map<String, Integer> numbs = new LinkedHashMap<>();
		for(int i = 0; i < eng.length; i++) {
			numbs.put(eng[i], i+1);
		}
		Map<String, String> trans = numbs.keySet().stream()
				.collect(
						Collectors.toMap(Function.identity(), // KeyMapper
						key -> fre[numbs.get(key) - 1])); // ValueMapper
		for(String key : trans.keySet()) {
			log(numbs.get(key) + " -> " + trans.get(key));
		}
	}
	
	@Test 
	public static void testGroupingMapCollector() {
		Map<Integer, List<String>> map = getProductStrings().stream()
				  .collect(Collectors.groupingBy(String::length));
		for(Integer key : map.keySet()) {
			log("Products of length " + key + " = " + map.get(key));
		}
	}
	
	@Test
	public static void testPartitioning() {
		Map<Boolean, List<String>> mapPartitioned = getProductStrings().stream()
				.collect(Collectors.partitioningBy(element -> element.length() > 7));
		print(mapPartitioned.entrySet().stream());
	}
	
	
	@Test
	public static void testYourOwnCollector() {
		Collector<String, ?, LinkedList<String>> toLinkedList =
				  Collector.of(
						  LinkedList::new, // Supplier (assume initializer)
						  LinkedList::add, // Accumulator(
						  (first, second) -> { 
							  first.addAll(second); 
							  return first; 
				    });
				 
		LinkedList<String> linkedListOfProducts =
						getProductStrings().stream().collect(toLinkedList);
		String end = linkedListOfProducts.getLast();
		log("Last element of linked list is " + end);
	}

	
	@Test
	public static void testAsList() {
		String data = "The cat sat on the mat";
		List<Character> chars = new ArrayList<>();
		for(Character ch : data.toCharArray()) chars.add(ch);
	}
	
	
}
