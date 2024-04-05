package com.bonfonte.geek;

import java.util.ArrayList;
import java.util.List;

/**
 * Given an initial string "abc" this class provides "iterator like" functions
 * hasNext/next to deliver all permutations of a string with unique characters.
 * Limitation .. it does not handle duplicate characters - these would generate
 * duplicate permutations.
 * That could be easily fixed using a filter + HashSet to skip the dups.
 * It avoids recursion to avoid blowing the lid off the stack.
 * Note that there are N! strings for a string of length N; a string of just 10
 * characters will generate 3_628_880 permutations.
 * 
 */
public class PermuteString {
  private String input;

  private static class Pair {
    String prefix;
    String suffix;

    public Pair(String pre, String suff) {
      this.prefix = pre;
      this.suffix = suff;
    }

    public Pair(String init) {
      this.prefix = "";
      this.suffix = init;
    }

    public String join() {
      return prefix + suffix;
    }
  }

  private List<Pair> pairs = new ArrayList<>();

  public PermuteString(String s) {
    input = s;
    pairs.add(new Pair(input));
  }

  public boolean hasNext() {
    return !pairs.isEmpty();
  }

  private void push(Pair p) {
    pairs.add(p);
  }

  private Pair pop() {
    int inx = pairs.size() - 1;
    return (inx < 0) ? null : pairs.remove(inx);
  }

  private static String at(String s, int pos) {
    return pos < s.length() ? s.substring(pos, pos + 1) : "";
  }

  private static String pre(String s, int pos) {
    return pos > 0 && pos <= s.length() ? s.substring(0, pos) : "";
  }

  private static String suf(String s, int pos) {
    int leng = s.length();
    pos++;
    return pos < leng ? s.substring(pos, leng) : "";
  }

  public String next() {
    while (true) {
      Pair pair = pop();
      if (pair == null)
        throw new RuntimeException("No next item available");
      switch (pair.suffix.length()) {
        case 0:
        case 1:
          return pair.join();
        default:
          // When the suffix part of a pair is still of size > 1 we have to
          // create new child pairs using each character and it's "complement"
          // Consider a string "abcd"; one of its child pairs is ("a, "bcd")
          // That suffix = "bcd" generates 3 char/complements: ("b","cd"), ("c", "bd") and
          // ("d", "bc");
          // So the one pair ("a", "bcd") will be popped off the stack
          // and will replace itself with its 3 new pairs ("ab", "cd"), ("ac", "bd") and
          // ("ad", "bc")
          // Each of those pairs will each generate 2 new pairs ... for example:
          // (ab, cd) will generate (abc, d) and (abd, c) - with suffixes of length 1.
          // Once a suffix is one character long we know there are no further permutations
          // of the Pair.
          // So we output the permutation e.g. "abcd" and "abdc" for those last two.
          // Every permutation will eventually be encountered.
          int length = pair.suffix.length();
          int inx = length - 1;
          // We push pairs in reverse order so that when popped and output by a next()
          // call they are in nice order.
          // E.g for s = "abdcef" the first permutation output will be "abcdef" ... and
          // the last "fedcba"
          while (inx >= 0) {
            String ch = at(pair.suffix, inx);
            String rest = pre(pair.suffix, inx) + suf(pair.suffix, inx);
            Pair child = new Pair(pair.prefix + ch, rest);
            push(child);
            inx--;
          }
          break;
      }
    }
  }
}
