package com.hack;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FIndACGT {

  private static class GENEFRAG {
    private Map<Character, Integer> map = init();
    static final char[] CHARSET = {'A', 'C', 'G', 'T'};

    int total = 0;

    public void increment(char c) {
      update(c, 1);
    }

    public void decrement(char c) {
      update(c, -1);
    }

    public GENEFRAG update(char c, int diff) {
      int value = map.computeIfAbsent(c, unfound -> 0) + diff;
      map.put(c, value);
      total += diff;
      return this;
    }

    private static Map<Character, Integer> init() {
      Map map = new HashMap<>();
      for (char c : CHARSET) map.put(c, 0);
      return map;
    }

    public GENEFRAG init(int a, int c, int g, int t) {
      map.put('A', a);
      map.put('C', c);
      map.put('G', g);
      map.put('T', t);
      this.total = a + c + g + t;
      return this;
    }

    public int getA() { return get('A'); }
    public int getC() { return get('C'); }
    public int getG() { return get('G'); }
    public int getT() { return get('T'); }
    private static int getcorr(int current, int steady) {
      if (current > steady) return current - steady;
      return 0;
    }

    public GENEFRAG correction(int steady) {
      GENEFRAG res = new GENEFRAG();
      res.init(getcorr(getA(), steady), getcorr(getC(), steady), getcorr(getG(), steady), getcorr(getT(), steady));
      return res;
    }

    public boolean isSteady() {
      int A = getA();
      return A == getC() && A == getG() && A == getT();
    }

    public int deviation(int steady) {
      return abs(steady, getA()) + abs(steady, getC()) + abs(steady, getG()) + abs(steady, getT());
    }

    private static int abs(int a, int b) {
      return Math.abs(a - b);
    }

    public GENEFRAG copy() {
      return new GENEFRAG().init(getA(), getC(), getG(), getT());
    }


    public int get(char c) {
      return map.computeIfAbsent(c, u -> 0);
    }

    public boolean canCorrect(GENEFRAG correction, Set<Character> hits) {
      for (char c : CHARSET) {
        if (hits.contains(c) && correction.get(c) > this.get(c)) return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "FRAG(A="+getA()+",C="+getC()+",G="+getG()+",T="+getT()+")";
   }
  }

  static void log(String s, Object... objects) {
    System.out.println(String.format(s, objects));
  }
  /*
     Find the minumum length Gene segment (a string of A, C, G and T that when replacing
     a corresponding section of same length makes the gene "steady" with equals counts of A, C, G and T.
   */

  private static GENEFRAG scan(char[] s, int pos, int nchars) {
    GENEFRAG frag = new GENEFRAG();
    for (int i = pos; i < pos + nchars; i++) {
      frag.increment(s[i]);
    }
    return frag;
  }

  static int nextToTry(int lowerBound, int upperBound) {
    return (lowerBound + upperBound)/2;
  }

  public static int steadyGene(String gene) {
    // Write your code here

    int leng = gene.length();
    int steady = leng/4;
    if (steady * 4 != leng) throw new RuntimeException("Illegal gene length " + leng);
    char[] arr = gene.toCharArray();

    GENEFRAG frag = scan(arr, 0, arr.length);
    if (frag.isSteady()) return 0;

    int deviation = frag.deviation(steady);
    int charsToScan = deviation/2;
    GENEFRAG correct = frag.correction(steady); // Identify what we must delete and replace.
    log("Correction will be " + correct);
    Set<Character> hits = new HashSet<>();
    if (correct.getA() > 0) hits.add('A');
    if (correct.getC() > 0) hits.add('C');
    if (correct.getG() > 0) hits.add('G');
    if (correct.getT() > 0) hits.add('T');

    int lowerbound = charsToScan;
    int upperbound = leng;

    // In this first version .. we use a line increase in charsToScan ...
    // But to optimise we should try charsToScan (where K=deviations/2) of K, 2K, 4K, .. etc. We find an upperbound
    // for the charsToScan and then proceed to binary chop that with previous K until we home in on the actual result.
    while (charsToScan < leng) {
      log("Trying length %s (lower=%s and upper=%s)", charsToScan, lowerbound, upperbound);
      GENEFRAG gf = scan(arr, 0, charsToScan);
      int pos = 0;
      boolean found = false;
      while (pos + charsToScan < leng) {
        if (gf.canCorrect(correct, hits)) {
          String match = gene.substring(pos, pos + charsToScan);
          if (charsToScan == lowerbound) {
            log("Found match of length " + charsToScan + " at pos " + pos + " " + match);
            return charsToScan;
          }
          log("Found upperBound for " + charsToScan + " at pos " + pos + " " + match);
          upperbound = charsToScan;
          found = true;
          break;
        }
        gf.decrement(arr[pos]); // Remove tail char at  pos
        gf.increment(arr[pos + charsToScan]); // Add new incoming
        pos++;
      }
      if (!found) lowerbound = charsToScan + 1;
      charsToScan = nextToTry(lowerbound, upperbound);
    }
    return charsToScan;
  }


  @Test
  public void testUseCase1() {
    Assert.assertEquals(0, steadyGene("ACTG"));
    Assert.assertEquals(1, steadyGene("ACTGACAG"));
    Assert.assertEquals(1, steadyGene("CTGACAGA"));
  }

  @Test
  public void testFailedOne() {
    String t = "GAAATAAA";
    Assert.assertEquals(5, steadyGene(t));
  }
  @Test
  public void testUseCase1Medium() {
                          // 012345---10---15---20---25--
    int actual = steadyGene("CTGAACTGGGATATTCTGTGACTACTGG");
    Assert.assertEquals(3, actual);
  }

  @Test
  public void testBuggerLugs() {
    String gene ="AATTTTGGAATCACGACGAGGGAACTGCGGCTTGACATGCAGGCCAGCGAACGGTCATAGCTCAAAGCTTTCTGTCATTTACGCATTATCTTAGCCAATACTTAAGATGGAGATCGGTTCAAAGGGTTATGTCGAAGCTTTGTGATAAGTGGGTCCTGGTTAGCATCGGAAGTCTGACCGACCGGTAAGTCCGACCTGGGCAGACACACGCATTATGGACCCCGCTCTAAGTGAACGACAGAGAGCCAACTAGGGGCTGCGGTATTTCCCAATGGACACCTCAAAGGATGAACTCGGCGAAAGGATCCGAAGCCATTCTGCGCAAATGTTTGACAATAAAAGGTTTACGCTGGATCCCGCCCTTCACGAGCCTGTGCATCTCCAAGTGTCTTCCTTAAACGTAATTATGAGATGTAAGGTAAGGCCACTAAAAGGATCTATAATCCATATGGTTGTAGTACGTATACGCTGCATTTATCGGTGTAAATGGGGAGGCAGCGCGGATCGCTTTC";
    int actual = steadyGene(gene);
    log("Actual = %s", actual);
  }

}
