package com.hack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class ZipReduce {

	/*
	 * Background of PROBLEM Sometimes items cannot be shipped to certain zip codes,
	 * and the rules for these restrictions are stored as a series of ranges of 5
	 * digit codes. For example if the ranges are: [94133,94133] [94200,94299]
	 * [94600,94699] Then the item can be shipped to zip code 94199, 94300, and
	 * 65532, but cannot be shipped to 94133, 94650, 94230, 94600, or 94299. Given a
	 * collection of 5-digit ZIP code ranges (each range includes both their upper
	 * and lower bounds), .....
	 *    Provide an algorithm that produces the minimum number of ranges
	 *    required to represent the same restrictions as the input.
	 */

	/**
	 * Here is our simple class to support the merging of arbitrary ranges of Integers.
	 */
	private static class Range implements Comparable<Range> {
		private final int lo;
		private final int hi;

		public Range(int v1, int v2) {
			this.lo = Integer.min(v1, v2); // Paranoid : don't assume range is correctly ordered
			this.hi = Integer.max(v1, v2);
		}

		@Override
		public int compareTo(Range o) {
			int res = Integer.compare(this.lo, o.lo);
			return res == 0 ? Integer.compare(this.hi, o.hi) : res;
		}

		public boolean includes(int value) {
			return Integer.compare(lo, value) <= 0 && Integer.compare(hi, value) >= 0;
		}

		public boolean overlaps(Range r) {
			return includes(r.lo) || includes(r.hi) || r.includes(lo) || r.includes(hi);
		}

		// I've deliberately made this method private because it is not "reflexive";
		// That is to say a.directlyPreceeds(b) clearly does not imply b.directlyPreceeds(a).
		private boolean directlyPreceeds(Range other) {
			return this.hi + 1 == other.lo;
		}

		/** 
		 * Construct a new IntRange from two existing.
		 */
		public Range combine(Range other) {
			return new Range(Integer.min(lo, other.lo), Integer.max(hi, other.hi));
		}

		public int getLo() {
			return lo;
		}

		public int getHi() {
			return hi;
		}

		// Some overrides to make the tests and display easy:
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Range))
				return false;
			Range r = (Range) other;
			return r.lo == lo && r.hi == hi;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(lo) + 7 * Integer.hashCode(hi);
		}

		@Override
		public String toString() {
			return "Range[" + lo + "," + hi + "]";
		}
	}

	/**
	 * Here is the main merge algorithm: The idea is to order the input ranges by
	 * their lower bound initially to make life easy. Having sorted We identify the
	 * first (current) range and we now iterate over remaining ranges. If two
	 * adjacent ranges overlap (or if current one directly precedes next) then combine these into a
	 * single range and keep going. Otherwise just store the current range in the
	 * result and continue the hunt from the next (assign to current).
	 * 
	 * @return a minimal list of ranges - by combining any overlapping or consecutive ranges
	 */
	public static List<Range> merge(Collection<Range> ranges) {
		List<Range> sorted = ranges == null ? new ArrayList<>()
				: ranges.stream().filter(Objects::nonNull).collect(Collectors.toList());

		// No merge necessary for a list of 0 or 1 length:
		if (sorted.size() <= 1) return sorted;
		
		Collections.sort(sorted); // We need the IntRange in ascending order
		
		List<Range> result = new ArrayList<>();
		Range current = sorted.get(0);
		int index = 1;
		while (index < sorted.size()) {
			Range next = sorted.get(index++);
			if (current.overlaps(next) || current.directlyPreceeds(next)) {
				current = current.combine(next);
			} else {
				result.add(current);
				current = next;
			}
		}
		result.add(current);
		return result;
	}

	/*********************
	 * TEST CODE FOLLOWS
	 *********************/
	private static void log(String s) {
		System.out.println(s);
	}

	private static void runTest(List<Range> expected, Range... rangeItems) {
		List<Range> ranges = Arrays.asList(rangeItems);
		List<Range> list = merge(ranges);
		log("\nTEST \nInput was    : " + ranges);
		log("Got range as : " + list);
		log("Expected was : " + expected);
		Assert.assertEquals(expected, list);
		log("SUCCESS!!");
	}

	/*
	 * Run tests on some simple examples: z1 = [94133,94133] z2 = [94200,94299] z3 =
	 * [94600,94699] Then the output should be = [94133,94133] [94200,94299]
	 * [94600,94699] If the input = z1 = [94133,94133] z2 = [94200,94299] z4 =
	 * [94226,94399] Then the output should be = [94133,94133] [94200,94399]
	 */

	@Test
	public void testExample1() {
		Range zips1 = new Range(94133, 94133);
		Range zips2 = new Range(94200, 94299);
		Range zips3 = new Range(94600, 94699);
		List<Range> testA = Arrays.asList(zips1, zips2, zips3);
		runTest(testA, zips1, zips2, zips3);
	}

	@Test
	public void testExample2() {
		Range zips1 = new Range(94133, 94133);
		Range zips2 = new Range(94200, 94299);
		Range zips3 = new Range(94226, 94399);
		List<Range> testB = Arrays.asList(zips1, new Range(94200, 94399));
		runTest(testB, zips1, zips2, zips3);
		// Check duplicate and other orders..
		runTest(testB, zips2, zips3, zips1, zips1);
	}

	@Test
	public void testBadlyOrderedInputs() {
		Range zips1 = new Range(94143, 94133); // Range is badly formed lo > hi but we convert.
		Range zips2 = new Range(94200, 94299);
		Range zips3 = new Range(96000, 94699);
		List<Range> testA = Arrays.asList(zips1, zips2, zips3);
		runTest(testA, zips1, zips2, zips3);
	}

	@Test
	public void testConsecutive() {
		Range zips1 = new Range(94133, 94133);
		Range zips2 = new Range(94134, 94199);
		Range zips3 = new Range(94200, 94299);
		/*
		 * Final test for CONSECUTIVE ranges: [94133, 94133], [94134, 94199], [94200,
		 * 94299] get mapped to a single range new IntRange(94133,94299)
		 */
		List<Range> testC = Arrays.asList(new Range(94133, 94299));
		runTest(testC, zips3, zips1, zips2);
		// And check dups and other orders..
		runTest(testC, zips1, zips3, zips2, zips1);
	}

	public static void main(String[] args) {
		ZipReduce reduce = new ZipReduce();
		reduce.testExample1();
		reduce.testExample2();
		reduce.testConsecutive();
	}

}
