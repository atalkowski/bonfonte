package com.bonfonte.testing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class BoundsTest {

  private static void log(String s, Object... obs) {
    System.out.println(String.format(s, obs));
  }
  private static final int MAX_ALLOW = 1_000_000_000;

  private static void show(String desc, int[] C) {
    String res = Arrays.toString(C);
    log("%s %s", desc, res);
  }
  /*
      Motivation for this solution:
      Going through the array A finding subsets within range of K
         .       /\__/\
        / \ _ _ /      \___  _/\    As K gets larger you can count ranges in bigger chunks
      /                    \/

     STOP PRESS : All tests show this solution is WORSE than original solution1!!! By order 6 to 10!!
     This Class allows us to optimize the speed of finding slices by using a map:
     The algorithm uses a TreeMap which maps values in A to a list of index positions in A;
     We may find (I think so) that we only need the latest index for any value.
     (In this description treat the last list member of the indexes as the value we would need).
     For example: if we have these integers in A = [ 3, 4, 3, 5, 4, 3 ]
     Then the code will initially create a map as follows (depending on K):
     *  If K == 1 Then map = { 3:[0,2], 4[1] } and we cannot proceed to the 4th element 5 at index 3.
        - The length of the longest slice is therefore 3 at this point (index = 0 to 2)
     *  If K == 2 Then map = { 3:[0,2,5], 4:[1,4], 5[3] } and we have reached the end of the array.
        - The length of the longest slice is therefore 6 at this point (index ranges over entire A).
     In each case then we know the number of slices + subslices found so far to be n(n+1)/2
     where n is slice length found.
     For K == 1 : slice length is 3 and total slice count (so far) is therefore 3*4/2 = 6
       Length 1:(3) (4) (3)  Length 2: (3,4) (4,3) and lastly a Length 3: (3,4,3)
     For K == 2 : slice length is 6 and total slices (altogether) is therefore 6*7/2 => 21
       and this is the solution as all slices fit within K initially.
     But for the K == 1 case - we must continue.
     The element value that stopped us was 5 (index=3) and this can be seen to be an upperbound (>maximum)
     of the current slice (3,4,3). To continue we must start as follows:
     (A) Because we have an UPPERBOUND stop - skip forward and base our slice start (=lo) at point:
         1  + the LAST index in the TreeMap Minimum value spot ... 3:[0,2] ... so at index 3.
         so set lo = 3; the hi value remains at the point we stopped = 3 (same in this case)
         (Note : if the stop index was a lowerbound ... use 1 + TreeMap Maximum value).
     (B) We clear the TreeMap to start the next search.
     There is a catch though ... suppose that A = [ 3, 3, 4, 5, 4, 3 ].
     Then the index point that we continue from is now 2 (not 3 as before).
     This is because the 2nd element 4 is legitimate to include going forward .. from where step A tells us.
     But this means we would be double counting any cells between lo (=2 here) and hi-1 (=2).
     ... unless we allow for that.
     In general there will be M cells that overlap. Above we only found 1 - but consider this array:
       Ax = [ 3, 3, 4, 4, 4, 4, 4, 5 ]
     Here the overlap is on all those 4's ... from cell 2 to 6.
     So before continuing we need to take care of this overlap when returning the next slice total.
     In the example Ax array. We compute as follows for K = 1:
     1st Total is for n=7 => 7*8/2 = 28 and
         we must reset our start point as 2; lo will be reset to 2 using rule (A)
         hi set to
     Now we have an overlap of those 5 fours which we must deduct from this
   */
  private static class SliceCounter {
    private final TreeMap<Integer, Integer> map = new TreeMap<>();
    private final int[] A;
    private final int K;

    private int pos = 0;
    private int prev = 0;
    public SliceCounter(int K, int[]A) {
      this.K = K <= 0 ? 0 : K;
      this.A = A == null ? new int[]{} : A;
      if (A.length > 0) { // Init map to have min and max value:
        map.put(A[0], 0);
      }
    }

    private int countSlices(int N) {
      return N < 2 ? N : N * (N + 1)/2;
    }
    private int handleUpperBound(int newMax) {
      // This is called when newMax (i.e. A[pos]) is such that A[pos] - min > K
      // In this case we must:
      // 1. Compute the total slices for the range prev to pos-1
      int total = countSlices(pos - prev);
      // 2. Now remove all items not in range K of newMax and compute new offset for "prev"
      while (map.size() > 0) {
        int min = getFirstLiveMin(newMax);
        if (newMax - min > K) {
          int minIndex = map.get(min);
          if (minIndex >= prev) prev = minIndex + 1;
          map.remove(min);
          // log("New max at %s/%s caused removal of %s at index %s", newMax, pos, min, minIndex);
        } else {
          break;
        }
      }
      map.put(newMax, pos);
      // 3. Finally .. disregard the items that will be recounted in the next call:
      int overlap = countSlices(pos - prev);
      return total - overlap;
    }

    private int handleNewLowerBound(int newMin) {;
      // This is called when newMin (i.e. A[pos]) is such that max - A[pos] > K
      // In this case we must:
      // 1. Compute the total slices for the range prev to pos-1
      int total = countSlices(pos - prev);
      // 2. Now remove all items not in range K of newMin and compute new offset for "prev"
      while (map.size() > 0) {
        int max = getFirstLiveMax(newMin);
        if (max - newMin > K) {
          int maxIndex = map.get(max);
          if (maxIndex >= prev) prev = maxIndex + 1;
          map.remove(max);
          // log("New min at %s/%s caused removal of %s at index %s", newMin, pos, max, maxIndex);
        } else {
          break;
        }
      }
      // 3. Now we can add the new minimum into the mix.
      map.put(newMin, pos);
      // 4. Finally .. before returning our total - we discount the slices between prev and pos
      // otherwise this get counted twice.
      int overlap = countSlices(pos - prev);
      return total - overlap;
    }

    private int getFirstLiveMin(int deflt) {
      while (map.size() > 0) {
        Map.Entry<Integer, Integer> entry = map.firstEntry();
        if (entry.getValue() >= prev) return entry.getKey();
        map.remove(entry.getKey());
      }
      return deflt;
    }

    private int getFirstLiveMax(int deflt) {
      while (map.size() > 0) {
        Map.Entry<Integer, Integer> entry = map.lastEntry();
        if (entry.getValue() >= prev) return entry.getKey();
        map.remove(entry.getKey());
      }
      return deflt;
    }
    public int findNextSliceCount() {
      if (pos >= A.length) return 0; // No more slices.
      int deflt = A[pos];
      int min = getFirstLiveMin(deflt);
      int max = getFirstLiveMax(deflt);
      while (pos < A.length) {
        int value = A[pos];
        if (value > max) {
          int diff = value - min;
          if (diff > K) return handleUpperBound(value);
          max = value;
        } else {
          if (value < min) {
            int diff = max - value;
            if (diff > K) {
              return handleNewLowerBound(value);
            }
            min = value;
          }
        }
        map.put(value, pos++);
      }
      int total = countSlices(pos - prev);
      // log("Final slice count returning %s at %s with prev=%s", total, pos, prev);
      return total;
    }

  }
  public int solution(int K, int[] A) {
    // show("Test for " + K, A);
    SliceCounter counter = new SliceCounter(K, A);
    int total = 0;

    for (;;)  {
      int newTotal = counter.findNextSliceCount();
      // log("Count returned new slice count %s to add to %s", newTotal, total);
      if (newTotal == 0) return total;
      total += newTotal;
      if (total > MAX_ALLOW) return MAX_ALLOW;
    }
  }

  public int solution1(int K, int[] A) {
    int total = 0;
    int lo = 0;
    // show("Test for " + K, A);
    while (lo < A.length) {
      int min = A[lo];
      int max = A[lo];
      int hi = lo+1;
      total++;
      while (hi < A.length) {
        int val = A[hi];
        if (val < min) {
          min = val;
        } else {
          if (val > max) {
            max = val;
          }
        }
        int diff = max - min;
        if (diff > K) {
          // log("Diff is %s at %s-%s > %s", diff, lo, hi, K);
          break;
        } else {
          // log("Diff is %s at %s-%s <= %s", diff, lo, hi, K);
          total += 1;
          // log("Incrementing total by %s now %s", hi-lo, total);
          if (total > MAX_ALLOW) return MAX_ALLOW;
          hi++;
        }
      }
      lo = lo + 1;
    }
    return total;
  }

  @Test
  public void test1() {
    int A[] = new int[] { 3, 5, 7, 6, 3 };
    int total = solution(2, A);
    log("Got total test1 as %s", total);
    Assert.assertEquals(9, total);
  }

  private int timeTest(int version, int count, int K, int[] A) {
    long start = System.currentTimeMillis();
    int result = 0;
    for (int i = 0; i < count; i++) {
      if (version == 1) {
        result = solution1(K, A);
      } else {
        result = solution(K, A);
      }
    }
    long time =  System.currentTimeMillis() - start;
    log("Version %s K=%s (x%s runs) was %sms; result %s", version, K, count, time, result);
    return result;
  }

  @Test
  public void speedTest1() {
    int A[] = new int[] { 1, 2, 5, 4, 6, 5, 3, 3, 3, 10, 1, 4 };
    for (int k = 1; k <= 12; k++) {
      int res1 = timeTest(1, 100, k, A);
      int res2 = timeTest(2, 100, k, A);
      Assert.assertTrue(res1 == res2);
    }
  }

  @Test
  public void speedTest2() {
    Random r = new Random(System.currentTimeMillis());
    int A[] = new int[10000];
    for (int i = 0; i<10000; i++) {
      int val = r.nextInt(100);
      A[i] = val;
    }
    int repeats = 100;
    int res1 = timeTest(1, repeats, 4, A);
    int res2 = timeTest(2, repeats, 4, A);
    int res3 = timeTest(1, repeats, 10, A);
    int res4 = timeTest(2, repeats, 10, A);
    int res5 = timeTest(1, repeats, 30, A);
    int res6 = timeTest(2, repeats, 30, A);
    int res7 = timeTest(1, repeats, 50, A);
    int res8 = timeTest(2, repeats, 50, A);
    Assert.assertTrue(res1 == res2);
    Assert.assertTrue(res3 == res4);
    Assert.assertTrue(res5 == res6);
    Assert.assertTrue(res7 == res8);
  }

  @Test
  public void speedTest3() {
    Random r = new Random(System.currentTimeMillis());
    int A[] = new int[100000];
    for (int i = 0; i<100000; i++) {
      int val = r.nextInt(1000);
      A[i] = val;
    }
    int repeats = 100;
    int res1 = timeTest(1, repeats, 4, A);
    int res2 = timeTest(2, repeats, 4, A);
    int res3 = timeTest(1, repeats, 10, A);
    int res4 = timeTest(2, repeats, 10, A);
    int res5 = timeTest(1, repeats, 30, A);
    int res6 = timeTest(2, repeats, 30, A);
    int res7 = timeTest(1, repeats, 50, A);
    int res8 = timeTest(2, repeats, 50, A);
    int res9 = timeTest(1, repeats, 200, A);
    int res0 = timeTest(2, repeats, 200, A);
    Assert.assertTrue(res1 == res2);
    Assert.assertTrue(res3 == res4);
    Assert.assertTrue(res5 == res6);
    Assert.assertTrue(res7 == res8);
    Assert.assertTrue(res9 == res0);
  }


  @Test
  public void test2() {
    int A[] = new int[] { 3, 5, 7, 6, 3 };
    int total = solution(3, A);
    log("Got total test2 as %s", total);
    Assert.assertEquals(10, total);
  }

  @Test
  public void test3() {
    int A[] = new int[] { 3, 5, 7, 6, 3 };
    int total = solution(4, A);
    log("Got total test3 as %s", total);
    Assert.assertEquals(15, total);
  }

  @Test
  public void test4() {
    int A[] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int total = solution(1, A);
    log("Got total test3 as %s", total);
    Assert.assertEquals(55, total);
  }

}
