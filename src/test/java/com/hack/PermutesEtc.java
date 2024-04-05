package com.hack;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PermutesEtc {

  /*
      A frag is a short string couple with an offset and a span.
      Example. Given a string HELLOWORLD - there is NCR(10, 3) ordered subsets of length 3, bu only 2 matching "LWR":
      Example 1: LWR - offset 2, span 6 (from this LLOWOR substring(begin=2, end=8) of length = span = 6 and
      Example 2: LWR - offset 3. span 5 (from this LOWOR)
      Of importance to the Frag usage will be to choose the minumum span version for duplicate text values like this.
   */

  private static class Permuter {
    int[] arr;
    int R;
    int N;
    private static final int READY = 0, CHECK = 1, ENDED = 2;

    private int state = READY;

    public Permuter(int R, int N) {
      if (!(N >= R && R >= 1)) throw new RuntimeException("Cannot choose " + R + " from " + N);
      this.N = N;
      this.R = R;
      arr = new int[R];
      for (int i = 0; i < R; i++) {
        arr[i] = i;
      }
    }

    /*
       Essentially the range of values each ceell the permutation can take goes like this (e.g. 5 choose 2)
         [0,1] [0,2] [0,3] [0,4] [0,5]
         [1,2] [1,3] [1,4] [1,5]
         [2,3] [2,4] [2,5]
         [3,4] [3,5]
         [4,5]
     */
    private boolean checkNext() {
      int pos = R-1;
      int maxAllowed = N-1;

      while (pos >= 0) {
        if (arr[pos] < maxAllowed) {
          int next = arr[pos] + 1;
          // Each time we
          for (int i = pos; i <= R-1; i++) {
            arr[i] = next++;
          }
          state = READY;
          return true;
        }
        pos = pos - 1;
        maxAllowed = maxAllowed - 1;
      }
      state = ENDED;
      return false;
    }

    public boolean hasNext() {
      switch (state) {
        case READY: return true;
        case ENDED: return false;
        default: return checkNext();
      }
    }

    public int[] next() {
      switch (state) {
        case CHECK: hasNext();
        return next();
        case READY: state = CHECK; return arr;
        default: throw new RuntimeException("Next call to permuter when hasNext is false");
      }
    }

  }

  private static class Frag {
    public int span;
    public int offset;
    public int end;
    public String text;
    public Frag() {
    }
    public Frag(String text, int offset, int end) {
      this.text = text;
      this.offset = offset;
      this.span = end + 1 - offset;
      this.end = end ;
    }

    @Override
    public String toString() {
      return "Frag[" + text + ":" + offset + ":" + end +" span = " + span + "]";
    }
  }

  private Map<Long, Long> factorials = new TreeMap<>();

  private long factorial(long n) {
    if (n <= 0) return 1;
    long result = 1L;
    Long possible = factorials.get(0l + n);
    if (possible != null) return possible;
    for (long i = 2; i <= n; i++) {
      result = result * i;
      factorials.put(i, result);
    }
    return result;
  }
  private long chooseNcR(long n, long r) {
    // N choose R is N!/ (r! * (n-r!)) is it not?
    if (!(n >= r && r > 0)) return 0L;
    long R = 0L + n - r;
    long RD = 2L;

    long res = 1;
    for (long i = r+1; i <= n; i++) {
      res = res * i;
      // Stop the res becoming too high during this process
      while (RD <= R && res % RD == 0) {
        res = res / RD++;
      }
    }
    while (RD <= R) {
      res = res / RD++;
    }
    return res;
  }

  private void log(String s, Object... o) {
    String res = String.format(s, o);
    System.out.println(res);
  }

  private long listFacts(int N, boolean debug) {
    return listFacts(1L, N + 0l, N + 0l, debug);
  }

  private long listFacts(long lower, long upper, long N, boolean debug) {
    long hi = 0;
    long R = 0;
    for (long i = lower; i <= upper; i++) {
      long fact = chooseNcR(N, i);
      if (debug)
        log("(%s C %s) = %s", N, i, fact);
      if (hi < fact) {
        hi = fact;
        R = i;
      }
    }
    log("Maximum was (%s C %s) => %s", N, R, hi);
    return hi;
  }

  @Test
  public void chooseFrom10() {
    long highest = listFacts(10, true);
    Assert.assertTrue(252L == highest);
  }


  @Test
  public void chooseFrom100() {
    long highest = listFacts(100, true);
  }

  @Test
  public void choose8From100() {
    long highest = listFacts(8, 8, 100, true);
  }

  @Test
  public void choose3From10and100() {
    listFacts(3, 3, 10, true);
    listFacts(3, 3, 25, true);

  }

  @Test
  public void chooseFromUpto100() {
    for (int i = 2; i <= 100; i++) {
      listFacts(i, false);
    }
  }


  public List<Frag> getAllFrags(String s, int size) {
    List<Frag> res = new ArrayList<>();
    size = Math.min(s.length(), size);
    char[] arr = s.toCharArray();
    Permuter perm = new Permuter(size, s.length());

    while (perm.hasNext()) {
      int[] offsets = perm.next();

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < size; i++) {
        sb.append(arr[offsets[i]]);
      }
      String text = sb.toString();
      int offset = offsets[0];
      int end = offsets[size-1];
      Frag frag = new Frag(text, offset, end);
      res.add(frag);
      log("Created " + frag);
    }
    return res;
  }


  @Test
  public void testFrags() {
    List<Frag> frags = getAllFrags("Hello", 2);
    Assert.assertTrue(10 == frags.size());
  }

  @Test
  public void testFrags20() {
    List<Frag> frags = getAllFrags("BRANDYSNAP", 3);
    Assert.assertTrue(120 == frags.size());

    frags = getAllFrags("BRANDYSNAP", 2);
    Assert.assertTrue(45 == frags.size());
  }

}
