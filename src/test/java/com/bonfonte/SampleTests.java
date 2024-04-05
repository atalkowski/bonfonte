package com.bonfonte;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SampleTests {

  /*
   * Complete the 'rotLeft' function below.
   *
   * The function is expected to return an INTEGER_ARRAY.
   * The function accepts following parameters:
   *  1. INTEGER_ARRAY a
   *  2. INTEGER d
   */

  public static List<Integer> rotLeft(List<Integer> a, int d) {
    // Write your code here
    int size = a.size();
    if (size == 0) return a;
    int fromIndex = d % size;
    if (fromIndex == 0) return a;

    List<Integer> res = new ArrayList<>(size);
    int toIndex = 0;
    while (toIndex < size) {
      if (fromIndex >= size) fromIndex = 0;
      res.add(toIndex++, a.get(fromIndex++));
    }
    return res;
  }

  private void checkMatch(List<Integer> list, Integer... values) {
    Assert.assertEquals(list.size(), values.length);
    for (int i = 0; i < values.length; i++) {
      Assert.assertEquals(list.get(i), values[i]);
    }
  }

  @Test
  public void testRotate1() {
    List<Integer> sample = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> rot1 = rotLeft(sample, 1);
    checkMatch(rot1, 2, 3, 4, 5, 1);
  }

  @Test
  public void testRotate5() {
    List<Integer> sample = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> rot1 = rotLeft(sample, 5);
    checkMatch(rot1, 1, 2, 3, 4, 5);
  }

  // The bribe queue issue:
  // Complete the minimumBribes function below.
  static int getSwapsOrig(int[] q) {
    int totalSwaps = 0;
    int max = q.length - 1;
    while(max > 1) {
      int swaps = 0;
      for(int i = 0; i < max; i++) {
        if(q[i] > q[i+1]) {
          int t = q[i];
          q[i] = q[i+1];
          q[i+1] = t;
          swaps++;
        }
      }
      if(swaps == 0) break;
      max--;
      totalSwaps += swaps;
    }
    return totalSwaps;
  }

  static int getSwaps(int[] q) {
    int totalSwaps = 0;
    int max = q.length ;
    for(int i = 0; i < max; i++) {
      int swaps = 0;
      for (int j = i+1; j < max; j++) {
        if (q[i] > q[j]) {
          swaps++;
          totalSwaps++;
          //System.out.println("Found swap " + swaps + " because " + q[i] + ">" + q[j]);
          if (swaps == 2) break;
        }
      }
    }
    return totalSwaps;
  }
  static String minimumBribesText(int[] q) {
    for (int i = 0; i < q.length; i++) {
      if (q[i] > i + 3) return "Too chaotic";
    }
    int total = getSwapsOrig(q);
    //if (total < 0) return "Too chaotic";
    return total + "";
  }

  static void minimumBribes(int[] q) {
    System.out.println(minimumBribesText(q));
  }

  @Test
  public void testBribes3() {
    int[] test1 = {1, 3, 4, 5, 2, 6};
    String res = minimumBribesText(test1);
    Assert.assertEquals("3", res);
  }

  @Test
  public void testBribes5andOut() {
    int[] test1 = {1, 2, 5, 3, 7, 6, 4};
    int[] test2 = {4, 1, 2, 3, 5, 6};
    String res = minimumBribesText(test1);
    Assert.assertEquals("5", res);
    res = minimumBribesText(test2);
    Assert.assertEquals("Too chaotic", res);
  }

  @Test
  public void testBribes5andOut2() {
    int[] test1 = {1, 2, 5, 3, 7, 6, 8, 10, 9, 4};
    int[] test2 = {4, 1, 2, 3, 5, 6};
    String res = minimumBribesText(test1);
    Assert.assertEquals("9", res);
    res = minimumBribesText(test2);
    Assert.assertEquals("Too chaotic", res);
  }

  private static class Manip {
    long value;
    int pos;

    public Manip(int pos, int todo) {
      this.pos = pos;
      this.value = todo;
    }

    public void add(Manip other) {
      if (this.pos == other.pos) {
        this.value = this.value + other.value;
      } else {
        throw new RuntimeException("Illegal add of incompatible manipulation");
      }
    }

    @Override
    public String toString() {
      return "Manip(" + pos + ":"  + value + ")";
    }
  }

  private static void pushAction(Map<Integer, Manip> actions, Manip manip) {
    Manip entry = actions.get(manip.pos);
    if (entry == null) {
      log("Adding new action " + manip);
      actions.put(manip.pos, manip);
    } else {
      log("Adding " + manip.value + " to " + entry);
      entry.add(manip);
    }
  }

  // Complete the arrayManipulation function below.
  private static long arrayManipulation(int n, int[][] queries) {
    TreeMap<Integer, Manip> actions = new TreeMap<>();
    for (int[] line : queries) {
      if (line[2] == 0) continue;
      Manip on = new Manip(line[0], line[2]);
      Manip off = new Manip(line[1] + 1, 0 - line[2]);
      pushAction(actions, on);
      pushAction(actions, off);
    }
    long current = 0;
    long max = Long.MIN_VALUE;
    for (Integer key : actions.keySet()) {
      Manip manip = actions.get(key);
      long newValue = current + manip.value;
      log("Current max is " + max + ": current =" + current + " value is " + manip
         + " so next range value is " + newValue +
          (newValue > max ? " - so resetting max!" : "")) ;
      current = newValue;
      if (current > max) {
        max = current;
      }
    }
    return max;
  }

  @Test
  public void testArrayManipulationTest() {
    int[][] queries = {{1, 5, 3}, {4, 8, 7}, {6, 9, 1}};
    long res = arrayManipulation(10, queries);
    Assert.assertEquals(10L, res);
  }

  @Test
  public void testArrayManipulationTest2() {
    int[][] queries = {{2,6,8}, {3,5,7}, {1,8,1}, {5,9,15}};
    long res = arrayManipulation(10, queries);
    //Assert.assertEquals(31L, res);
  }

  private static long naiveArrayManipulation(int n, int[][] queries) {
    long[] arr = new long[n];

    for (int i = 0; i < n; i++) {
      arr[i] = 0;
    }

    for(int[] line : queries) {
      int add = line[2];
      if (add == 0) continue;
      int lo = line[0] - 1; // Dum indexes are from 1 to N (not 0 to N-1)
      int hi = line[1] - 1;
      while (lo <= hi) arr[lo++] += add;
    }
    long res = arr[0];
    for (int i = 1; i < n; i++) {
      if (arr[i] > res) res = arr[i];
    }
    return res;
  }

  public static String isValid(String s) {
    if (s.length() <= 3) return "YES";
    Map<Character, Integer> map = new HashMap<>();
    for (int i = 0; i < s.length(); i++) {
      Character c = s.charAt(i);
      int value = map.computeIfAbsent(c, x -> 0) + 1;
      map.put(c, value);
    }
    // Find the max and min counts: abort if more than 2
    int min = 0;
    int max = 0;
    Set<Integer> countSet = new HashSet<>();
    for (Integer count : map.values()) {
      if (count > max) max = count;
      if (min == 0 || min > count) min = count;
      countSet.add(count);
      if (countSet.size() > 2) return "NO"; // Case F - early detection warranted.
    }

    // So there are at most two different totals (min and max).
    // Suppose we had these original counts:
    // Case A: Example: 3 3 3 ... all the same implies min == max so YES
    // Case B: Example: 3 1 3 ... this is YES ,,, we can remove the 1 count and balance it up.
    // Case C: Example: 3 2 2 ... this is YES ... remove the one element from the 3 total to give all same.
    // Case D: Example: 4 2 2 ... this is NO ... maxima is too high to remove 1 item to balance this
    // Case E: Example: 3 2 3 ... this is NO ... too many maxima - we cannot remove 1 item to balance this
    // Case F: Example: 4 3 2 ... too many different totals .. cannit
    // Case G: Example: 2 2 1 1 ... we have too many minima at 1 (different than case B)
    if (max == min) return "YES"; // Case A - all counts are the same!
    int totalMaxima = 0;
    int totalMinima = 0;
    // Note min is not equal max....
    for (Integer count : map.values()) {
      if (count == max) totalMaxima++;
      if (count == min) totalMinima++;
    }
    if (totalMinima == 1) {
      if (min == 1) return "YES"; // Case B
      if (totalMaxima == 1) {
        if (max == min + 1) return "YES"; // Case C
        return "NO"; // Case E
      }
      return "NO"; // Case G mulitple maxima so cannot change this
    }
    if (totalMaxima > 1) {
      return "NO"; // Case G ... too many maxima with multiple minima
    }
    // Only one max (totalMaxima == 1)....
    if (max == min + 1) return "YES"; // Case C
    return "NO"; // Case E one maxima but it is too high to reduce to minimum
  }



  @Test
  public void testValidYes() {
    Assert.assertEquals("YES", isValid(""));
    Assert.assertEquals("YES", isValid("a"));
    Assert.assertEquals("YES", isValid("ab"));
    Assert.assertEquals("YES", isValid("abb"));
    Assert.assertEquals("YES", isValid("abba"));
    Assert.assertEquals("YES", isValid("abbab"));
    Assert.assertEquals("YES", isValid("aabab"));
    Assert.assertEquals("YES", isValid("abcdeebcda"));
    Assert.assertEquals("YES", isValid("aaab"));
  }

  @Test
  public void testValidNo() {
    Assert.assertEquals("NO", isValid("aaabbccc"));
    Assert.assertEquals("YES", isValid("aaabccc"));
    Assert.assertEquals("NO", isValid("aaaabbcc"));
    Assert.assertEquals("NO", isValid("aabbcd"));

  }

  @Test
  public void testxxxaabbccrry() {
    Assert.assertEquals("NO", isValid("xxxaabbccrry"));
  }

    /* Ideas:
       Build an index Map<Character, List<Integer>> M1 of locations for each character of S1.
       Build an index Map<Character, List<Integer>> M2 of locations for each character of S2.
       Have a method List<Integer> findSublist(char, M, pos)
          finds sublist for char in M
       Algorithm initially recursive.
           findmaxstring(s1, s2, pos1, pos2, m1, m2)
       int pos1 = 0; for string s1
       int pos2 = 0; for string s2
       String res = .... call below:
       String findmaxstring(s1, s2, pos1, pos2, m1, m2) {
          char c = s1.charAt(pos1);
          String s = "" + c;
          String longest = "";
          List<Integer> poses = findSublist(c, M2, pos2);
          for (Integer pos2_next: poses) {
            String child = findmax(s2, s1, pos2_next, pos1+1, m2, m1);
            if (child.length() > longest.length()) longest = child;
          }
          return s + longest;
       }
     */

  private static List<Integer> findSublist(char c, Map<Character, List<Integer>> m, int pos) {
    List<Integer> list = m.get(c);
    if (list == null) return new ArrayList<>();
    List<Integer> result = list.stream().filter(i -> i >= pos).collect(Collectors.toList());
    return result;
  }

  private static int findPosGE(List<Integer> list, int value) {
    int hi = list.size() - 1;
    int lo = 0;
    while (hi >= lo) {
      int mid = (hi + lo + 1) / 2;
      int valueAtMid = list.get(mid);
      if (valueAtMid == value) return mid;
      if (valueAtMid > value) {
        hi = mid - 1;
      } else {
        lo = mid;
        hi = hi - 1;
      }
    }
    if (list.get(lo) < value) return -1;
    return lo;
  }

  private static List<Integer> findSublistChop(char c, Map<Character, List<Integer>> m, int pos) {
    List<Integer> list = m.get(c);
    if (list == null) return new ArrayList<>();
    int offset = findPosGE(list, pos);
    if (offset < 0) return new ArrayList<>();
    List<Integer> result = list.subList(offset, list.size());
    // stream().filter(i -> i >= pos).collect(Collectors.toList());
    return result;
  }

  private static String removeNotFound(String s1, String s2) {
    StringBuilder res = new StringBuilder();
    Set<Character> ok = new HashSet<>();
    for (char c : s2.toCharArray()) {
      ok.add(c);
    }
    for (char c : s1.toCharArray()) {
      if (ok.contains(c)) {
        res.append(c);
      }
    }
    return res.toString();
  }

  private static Map<Character, List<Integer>> buildCharMap(String s) {
    Map<Character, List<Integer>> result = new HashMap<>();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      List<Integer> list = result.computeIfAbsent(c, x -> new ArrayList<>());
      list.add(i);
    }
    return result;
  }

  private static String qik(String s, int pos) {
    String res = s.substring(pos);
    if (res.length() > 10) return res.substring(0, 9) + "...";
    return res;
  }

  private static String findmax(int depth, String s1, String s2, int pos1, int pos2,
                                Map<Character, List<Integer>> m1,
                                Map<Character, List<Integer>> m2) {
    String longest = "";
    Set<Character> uselessList = new HashSet<>();

    while (pos1 < s1.length()) {
      String msg = String.format("findmax(%s, %s..., %s..., %s, %s)", depth,
          qik(s1, pos1), qik(s2, pos2), pos1, pos2);
      // log(msg);
      String sublongest = "";
      char c = s1.charAt(pos1++);
      if (uselessList.contains(c)) continue;
      List<Integer> poses = findSublist(c, m2, pos2);
      if (poses.isEmpty()) {
        uselessList.add(c);
        continue; // This character has no (more) matches
      }

      // If here ... we know that c is valid and can be appended to as a candidate longest
      for (Integer pos2_next: poses) {
        if (longest.length() >= s2.length() - pos2_next) break;
        String child = findmax(depth+1, s2, s1, pos2_next+1, pos1, m2, m1);
        if (child.length() > 0) {
//          log(String.format("Depth %s = found child %s at pos %s in %s",
//              depth, child, pos2_next, qik(s2,0)));
        } else {
          uselessList.add(c);
        }
        if (child.length() > sublongest.length()) {
//          log(String.format("Depth %s - replacing sublongest '%s-%s' with '%s-%s'",
//              depth, c, sublongest, c, child));
          sublongest = child;
        }
        break;
      }
      sublongest = c + sublongest;
      if (sublongest.length() > longest.length()) {
        if (depth < 2)
          log(String.format("Depth %s replacing longest '%s' with '%s'", depth, longest, sublongest));
        longest = sublongest;
      }
    }
    return longest;
  }

  private static void removeUseless(Map<Character, List<Integer>> m1, Map<Character, List<Integer>> m2) {
    for (Character c : m1.keySet()) {
      if (m2.containsKey(c)) continue;
      m2.remove(c);
    }
  }
  public static int commonChild(String s1, String s2) {
    s1 = removeNotFound(s1, s2);
    s2 = removeNotFound(s2, s1);
    log("S1 = " + s1);
    log("S2 = " + s2);
    Map<Character, List<Integer>> m1 = buildCharMap(s1);
    Map<Character, List<Integer>> m2 = buildCharMap(s2);
    String longest = findmax(0, s1, s2, 0, 0, m1, m2);
    log("Found longest item as " + longest);
    return longest.length();
  }

  static AtomicInteger logcount = new AtomicInteger();
  private static void log(String s) {
    if (logcount.incrementAndGet() > 8000) throw new RuntimeException("Took too long!!");
    System.out.println(s);
  }

  @Test
  public void testCommon() {
    int leng = commonChild("HARRYSOLOMON", "SALLYMENSON");
    Assert.assertEquals(5, leng);
  }

  @Test
  public void testCommon1() {
    int leng = commonChild(
        "ELGGYJWKTDHLXJRBJLRYEJWVSUFZKYHOIKBGTVUTTO",
        //012345---10---15---20---25---30---35---40---45---50---55---60---65---70---75---80---85---90---95--100
        "FRVIFOVJYQLVZMFBNRUTIYFBMFFFRZVBYINXLDDSVM");
    log( "Total common length is " + leng);
  }

  // @Test
  public void testCommon2() {
    int leng = commonChild(
        "ELGGYJWKTDHLXJRBJLRYEJWVSUFZKYHOIKBGTVUTTOCGMLEXWDSXEBKRZTQUVCJNGKKRMUUBACVOEQKBFFYBUQEMYNENKYYGUZSP",
       //012345---10---15---20---25---30---35---40---45---50---55---60---65---70---75---80---85---90---95--100
        "FRVIFOVJYQLVZMFBNRUTIYFBMFFFRZVBYINXLDDSVMPWSQGJZYTKMZIPEGMVOUQBKYEWEYVOLSHCMHPAZYTENRNONTJWDANAMFRX");
    log( "Total common length is " + leng);
  }

  /*
     Synopsis solution;
     1. Create a map m1 to contain current state for each integer Map(Integer, Integer>
     2. Creata a map m2 to contain current counts Map<Integer = count, Integer how many have this>
     2. Input 1 x - Increment count C for x in m1; adjust m2 (decrement C in M, increment C+1)
     3. Input 2 y - Decrement count for y in m1;  reverse m2
     3. Input 3 z - Just check that m2 has z in it. If so print 1 otherwise 0
   */

  private static int adjustMap(Map<Integer,Integer> m, int value, int adjust) {
    int count = m.computeIfAbsent(value, f -> 0) + adjust;
    if (count < 0) count = 0;
    m.put(value, count);
    return count;
  }

  static List<Integer> freqQuery(List<List<Integer>> queries) {
    Map<Integer, Integer> m1 = new HashMap<>();
    Map<Integer, Integer> m2 = new HashMap<>();
    List<Integer> results = new ArrayList<>();
    int t1 = 0, t2 =0, t3 = 0;
    for (List<Integer> query : queries) {
      log(String.format("Query: %s", query));
      int value = query.get(1);
      int freq;
      switch(query.get(0)) {
        case 1: t1++;
          freq = adjustMap(m1, value,1);
          adjustMap(m2, freq,1);
          if (freq > 1) {
            adjustMap(m2, freq - 1, -1);
          }
          break;
        case 2: t2++;
          freq = adjustMap(m1, value,-1);
          if (freq > 0)
            adjustMap(m2, freq, 1);
          adjustMap(m2, freq + 1,-1);
          break;
        case 3: t3++;
          freq = adjustMap(m2, value, 0);
          results.add(freq > 0 ? 1 : 0);
          break;
        default:
          continue;
      }
      log(String.format("m1=%s\nm2=%s", m1, m2));
    }
    log(String.format("Totals: t1:%s t2:%s t3:%s = %s all", t1, t2, t3, t1+t2+t3));
    return results;
  }

  private List<List<Integer>> createPairs(int... values) {
    List<List<Integer>> pairs = new ArrayList<>();
    int index = 0;
    while (index + 1 < values.length) {
      List<Integer> pair = new ArrayList<>(2);
      pair.add(values[index++]);
      pair.add(values[index++]);
      pairs.add(pair);
    }
    return pairs;

  }

  @Test
  public void testFreq() {
    List<List<Integer>> input = createPairs(
        1, 3,
        2, 3,
        3, 2,
        1, 4,
        1, 5,
        1, 5,
        1, 4,
        3, 2,
        2, 4,
        3, 2
        // 1, 1, 2, 2, 3, 2, 1, 1, 1, 1, 2, 1, 3, 2
    );
    List<Integer> res = freqQuery(input);
    Assert.assertEquals(3, res.size());
    Assert.assertTrue(0 == res.get(0));
    Assert.assertTrue(1 == res.get(1));
    Assert.assertTrue(1 == res.get(2));
  }

  /*
  3,1, 1,100038, 1,100026, 2,5, 3,2, 1,100043, 2,2, 3,3, 1,100080, 1,100073, 2,2,
1,100005, 2,5, 1,100078, 1,100085, 1,100070, 1,100077, 2,1, 3,3, 3,3, 1,100056,
3,1, 3,4, 3,4, 3,1, 3,1, 2,5, 1,100021, 3,1, 2,2, 3,5, 2,3, 3,2, 1,100050,
1,100098, 3,3, 2,5, 3,1, 3,1, 3,1, 3,5, 1,100007, 3,3, 3,4, 2,5
   */
  @Test
  public void testFreqErr() {
    List<List<Integer>> input = createPairs(1,100021,
        3,3, 1,100043, 1,100062, 1,100089, 1,100007, 2,3, 3,4, 3,3, 3,4, 2,5, 1,100092,
        3,5, 2,1, 3,1, 1,100091, 1,100053, 1,100044, 2,2, 3,3, 1,100063, 1,100068, 2,3, 1,100084,
        2,2, 1,100004, 2,1, 3,5, 2,5, 3,3, 1,100077, 1,100080, 3,1, 1,100020, 1,100093, 3,1,
        2,3, 3,2, 3,4, 2,5, 2,5, 2,5, 3,2, 3,4, 3,5, 2,2, 3,2, 3,2, 1,100056,
        3,3, 2,4, 1,100070, 3,5, 3,3, 3,3,
        3,1, 1,100038, 1,100026, 2,5, 3,2, 1,100043, 2,2, 3,3, 1,100080, 1,100073, 2,2,
        1,100005, 2,5, 1,100078, 1,100085, 1,100070, 1,100077, 2,1, 3,3, 3,3, 1,100056,
        3,1, 3,4, 3,4, 3,1, 3,1, 2,5, 1,100021, 3,1, 2,2, 3,5, 2,3, 3,2, 1,100050,
        1,100098, 3,3, 2,5, 3,1, 3,1, 3,1, 3,5, 1,100007, 3,3, 3,4, 2,5
    );
    Assert.assertEquals(100, input.size());
    List<Integer> res = freqQuery(input);
    log(res.toString());
    Assert.assertEquals(42 , res.size());
  }

}
