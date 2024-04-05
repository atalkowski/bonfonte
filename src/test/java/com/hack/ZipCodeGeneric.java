package com.hack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class ZipCodeGeneric {
	
/*
 * Background of PROBLEM
 * Sometimes items cannot be shipped to certain zip codes, and the rules for these restrictions
 * are stored as a series of ranges of 5 digit codes. For example if the ranges are:
 *   [94133,94133] [94200,94299] [94600,94699]
 *   Then the item can be shipped to zip code 94199, 94300, and 65532,
 *   but cannot be shipped to 94133, 94650, 94230, 94600, or 94299.
 * Given a collection of 5-digit ZIP code ranges (each range includes both their upper
 * and lower bounds), ..... 
 * >>>>> provide an algorithm that produces the minimum number of ranges
 * >>>>> required to represent the same restrictions as the input.
 */
	
	
 /**	
  * NOTE ON THIS VERSION 2 SOLUTION by Andy Talkowski using generic Range<T> logic.
 ******************************************************************************************
 *  So I was trying to work out a generic solution for type T extends Comparable<T>       *
 *  However, the flaw in that design is that we don't have an easy way to check when a    *
 *  range "precedes" another range (e.g. [1,3] precedes [4,8] and can be joined together! *
 *  But this is hard to do for a generic type T for example when type T is String:        *
 *  For the string "xyz" what is its successor? In fact there is little to be gained.     *
 *  So the generic case is NOT ok for Integer or Long types (and so not for Zip codes)    *
 *  Having said all that - we can utilize an initial generic merge algorithm - as below.  *
 *  We handle the basic restructuring of the ranges using a "BoundaryPoint" analysis      *
 *  which can be done on a generic type T initially; we use classes BoundType and         *
 *  BoundPoint which allow us to reduce initially to non overlapping and sorted ranges.   *
 *  We then finally merge remaining ranges using "preceding" logic and join if needed.    *
 ******************************************************************************************
 */

	
	private enum BoundType{
		START,
		END;
	}
	
	private static class BoundPoint<T extends Comparable<T>> implements Comparable<BoundPoint<T>>{
		private T value;
		private BoundType type;
		private BoundPoint(T value, BoundType type) {
			this.value = value;
			this.type = type;
		}
		
		public static <T extends Comparable<T>> BoundPoint<T> start(T value) {
			return new BoundPoint<>(value, BoundType.START);
		}
		
		public static <T extends Comparable<T>> BoundPoint<T> end(T value) {
			return new BoundPoint<>(value, BoundType.END);
		}

		@Override
		public int compareTo(BoundPoint<T> o) {
			int result = value.compareTo(o.value);
			return result == 0 ? type.compareTo(o.type) : result;
		}

	}

	public static class Range<T extends Comparable<T>>{
		public T lo;
		public T hi;
		
		public List<BoundPoint<T>> toBoundPoints(){
			return Arrays.asList(BoundPoint.start(lo), BoundPoint.end(hi));
		}
		
		public Range(T lo, T hi) {
			this.hi = hi;
			this.lo = lo;
		}
		
		/*
		 * Here we effectively split the range up into a sequence of START and END values:
		 * e.g.
		 * [1,3], [5,10], [7,25] get mapped to an ordered list:
		 *  S1, E3, S5, S7, E10, E25  (where S => start of range and E => End of range.
		 *  From this simple sorted list we can deduce that the ranges are
		 *  [1,3] and [5,25] ..  
		 */
		
		public static <T extends Comparable<T>> List<Range<T>> reduce(Collection<Range<T>> ranges){
			List<BoundPoint<T>> points = ranges.stream()
					.map(Range::toBoundPoints)
					.flatMap(List::stream)
					.sorted()
					.collect(Collectors.toList());
			int depth = 0;
			BoundPoint<T> current = null;
			List<Range<T>> result = new ArrayList<>();
			for(BoundPoint<T> point : points) {
				switch(point.type) {
				case START: 
					if(current == null) current = point;
					depth++;
					break;
				case END:
				default:
					depth--; // Whenever the depth is 0 we are at the end of a range - so create that range now:
					if(depth == 0) {
						Range<T> range = new Range<>(current.value, point.value);
						result.add(range);
						current = null;
					}
				}
			}
			return result;
		}
		
		@Override
		public String toString() {
			return "Range[" + lo + "," + hi + "]";
		}
		
	}
	/***********************
	 * TEST CODE LAYER
	 **********************/
	private static void log(String s) {
		System.out.println(s);
	}
	
	private static void runTest(List<Range<Integer>> expected, Range<Integer>... rangeItems) {
		List<Range<Integer>> ranges = Arrays.asList(rangeItems);
		List<Range<Integer>> list = Range.reduce(ranges);
		log("\nTEST \nInput was    : " + ranges);
		log("Got range as : " + list);
		log("Expected was : " + expected);
		Assert.assertEquals(expected, list);
		log("SUCCESS!!");
	}

	private static void runTest(List<ZipRange> expected, ZipRange... rangeItems) {
		List<ZipRange> ranges = Arrays.asList(rangeItems);
		List<ZipRange> list = ZipRange.merge(ranges);
		log("\nTEST \nInput was    : " + ranges);
		log("Got range as : " + list);
		log("Expected was : " + expected);
		Assert.assertEquals(expected, list);
	}

	
	public static class ZipRange{
		private final int lo, hi;
		public ZipRange(int lo, int hi) {
			this.lo = lo;
			this.hi = hi;
		}
		
		public static List<ZipRange> merge(Collection<ZipRange> ranges){
			List<Range<Integer>> ZipRanges = ranges.stream()
					.map(ZipRange::toRange)
					.collect(Collectors.toList());
			List<Range<Integer>> reduced = Range.reduce(ZipRanges);
			if(reduced.size() > 1) {
				// Handle the use case where two ranges are consecutive:
				List<Range<Integer>> joined = new ArrayList<>();
				Range<Integer> current = reduced.get(0);
				for(int index = 1; index < reduced.size(); index++) {
					Range<Integer> next = reduced.get(index);
					if(preceeds(current, next)) {
						current = joinRanges(current, next);
					}else {
						joined.add(current);
						current = next;
					}
				}
				joined.add(current);
				reduced = joined;
			}
			return reduced.stream()
					.map(range -> new ZipRange(range.lo, range.hi))
					.collect(Collectors.toList());
		}
		
		private static boolean preceeds(Range<Integer> r1, Range<Integer> r2) {
			return (r1.hi + 1 == r2.lo + 0);
		}
		
		private static Range<Integer> joinRanges(Range<Integer> r1, Range<Integer> r2) {
			return new Range<>(Integer.min(r1.lo, r2.lo), Integer.max(r1.hi, r2.hi));
		}
		
		private Range<Integer> toRange(){
			return new Range<>(lo, hi);
		}
		
		// Some overrides to make the tests and display easy:
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof ZipRange))
				return false;
			ZipRange r = (ZipRange) other;
			return r.lo == lo && r.hi == hi;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(lo) + 7 * Integer.hashCode(hi);
		}

		
		@Override
		public String toString() {
			return "ZipRange[" + lo + "," + hi + "]";
		}

	}
	
	@Test
	public void testExample1() {
		ZipRange zips1 = new ZipRange(94133, 94133);
		ZipRange zips2 = new ZipRange(94200, 94299);
		ZipRange zips3 = new ZipRange(94600, 94699);
		List<ZipRange> testA = Arrays.asList(zips1, zips2, zips3);
		runTest(testA, zips1, zips2, zips3);
	}

	@Test
	public void testExample2() {
		ZipRange zips1 = new ZipRange(94133, 94133);
		ZipRange zips2 = new ZipRange(94200, 94299);
		ZipRange zips3 = new ZipRange(94226, 94399);
		List<ZipRange> testB = Arrays.asList(zips1, new ZipRange(94200, 94399));
		runTest(testB, zips1, zips2, zips3);
		// And check dups an other orders..
		runTest(testB, zips2, zips3, zips1, zips1);
	}

	@Test
	public void testConsecutive() {
		ZipRange zips1 = new ZipRange(94133, 94133);
		ZipRange zips2 = new ZipRange(94134, 94199);
		ZipRange zips3 = new ZipRange(94200, 94299);
		/*
		 * Final test for CONSECUTIVE ranges: [94133, 94133], [94134, 94199], [94200,
		 * 94299] get mapped to a single range new ZipRange(94133,94299)
		 */
		List<ZipRange> testC = Arrays.asList(new ZipRange(94133, 94299));
		runTest(testC, zips3, zips1, zips2);
		// And check dups and other orders..
		runTest(testC, zips1, zips3, zips2, zips1);
	}
}
