package com.bonfonte.testing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class FishTest {

  private static final int DOWN = 1;
  private static final int UP = 0;
  private static void log(String s, Object... obs) {
    System.out.println(String.format(s, obs));
  }

  private static void show(String desc, int[] C) {
    String res = Arrays.toString(C);
    log("%s %s", desc, res);
  }

  public int solution(int[] A, int[] B) {
    // Implement your solution here
    show("\nFish", A);
    show("Dirc", B);

    int hi = A.length;
    if (hi <= 0) return 0;
    int alive = 0;
    int lo = 0;
    int currentWinner = UP;
    Stack<Integer> contenders = new Stack<>();
    // B[n] == 1 => fish swimming downstream
    // Skip escaped upstream;

    while (lo < hi) {
      String otherDir = currentWinner == DOWN ? "UP" : "DOWN";
      int fish = A[lo];
      int direction = B[lo];

      if (direction == currentWinner) {
        if (currentWinner == UP) {
          alive++;
          log("UP stream swimmer %s not challenged", A[lo]);
        } else {
          contenders.push(fish);
          log("Down stream swimmer %s added", fish);
        }
      } else {
        // Adversary! who is the biggest? who gets eaten?
        while (!contenders.isEmpty()) {
          int contender = contenders.peek();
          if (contender < fish) {
            contenders.pop(); // Eaten?
            log("Swimmer %s got eaten by %s stream swimmer %s", contender, otherDir, fish);
          } else {
            log("%s swimmer %s got eaten by stream swimmer %s", otherDir, fish, contender);
            break;
          }
        }
        if (contenders.isEmpty()) {
          currentWinner = currentWinner == DOWN ? UP : DOWN;
          contenders.push(fish);
        }
      }
      lo++;
    }
    return alive + contenders.size();
  }




  public int solution1(int[] A, int[] B) {
    // Implement your solution here
    show("\nFish", A);
    show("Dirc", B);

    int hi = A.length;
    if (hi <= 0) return 0;
    int alive = 0;
    int lo = 0;
    int currentWinner = UP;
    Stack<Integer> contenders = new Stack<>();
    // B[n] == 1 => fish swimming downstream
    // Skip escaped upstream;
    while (lo < hi) {
      if (B[lo] == 0) {
        log("UP stream swimmer %s not challenged", A[lo]);
        lo++;
        alive++;
      } else {
        contenders.push(A[lo]);
        currentWinner = DOWN;
        break;
      }
    }
    hi--;
    // Skip downstream gone by;
    while (hi > lo) {
      if (B[hi] == 1) {
        log("DOWN stream swimmer %s not challenged", A[hi]);
        alive++;
        hi--;
      } else {
        break;
      }
    }

    while (lo < hi) {
      lo++;
      String otherDir = currentWinner == DOWN ? "UP" : "DOWN";
      int fish = A[lo];
      int direction = B[lo];
      if (direction == currentWinner) {
        log("Swimmer %s now swimming ahead of %s", fish, contenders.peek());
        contenders.push(fish);
      } else {
        // Adversary! who is the biggest? who gets eaten?
        while (!contenders.isEmpty()) {
          int contender = contenders.peek();
          if (contender < fish) {
            contenders.pop(); // Eaten?
            log("Swimmer %s got eaten by %s stream swimmer %s", contender, otherDir, fish);
          } else {
            log("%s swimmer %s got eaten by stream swimmer %s", otherDir, fish, contender);
            break;
          }
        }
        if (contenders.isEmpty()) {
          currentWinner = currentWinner == DOWN ? UP : DOWN;
          contenders.push(fish);
        }
      }
    }
    return alive + contenders.size();
  }

  @Test
  public void test0() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 0, 0, 0, 0, 0 };
    int soln = solution(A, B);
    log("Result of test1 is %s", soln);
    Assert.assertEquals(5, soln);
  }

  @Test
  public void test1() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 0, 1, 0, 0, 0 };
    int soln = solution(A, B);
    log("Result of test1 is %s", soln);
    Assert.assertEquals(2, soln);
  }

  @Test
  public void test2() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 1, 0, 0, 0, 0 };
    int soln = solution(A, B);
    log("Result of test2 is %s", soln);
    Assert.assertEquals(1, soln);
  }

  @Test
  public void test3() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 1, 0, 0, 1, 1 };
    int soln = solution(A, B);
    log("Result of test3 is %s", soln);
    Assert.assertEquals(3, soln);
  }

  @Test
  public void test4() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 0, 0, 0, 1, 1 };
    int soln = solution(A, B);
    log("Result of test4 is %s", soln);
    Assert.assertEquals(5, soln);
  }

  @Test
  public void test5() {
    int A[] = new int[] { 2, 3, 4, 5, 1 };
    int B[] = new int[] { 1, 1, 1, 1, 0 };
    int soln = solution(A, B);
    log("Result of test5 is %s", soln);
    Assert.assertEquals(4, soln);
  }

  @Test
  public void test6() {
    int A[] = new int[] { 7, 9, 3, 4, 1, 11, 5, 10, 6, 8, 12, 2 };
    int B[] = new int[] { 0, 1, 0, 0, 0,  1, 0,  1, 1, 1, 0, 0 };
    int soln = solution(A, B);
    log("Result of test6 is %s", soln);
    Assert.assertEquals(3, soln);
  }

  @Test
  public void test7() {
    int A[] = new int[] { 4, 3, 2, 1, 5 };
    int B[] = new int[] { 1, 1, 1, 1, 1 };
    int soln = solution(A, B);
    log("Result of test5 is %s", soln);
    Assert.assertEquals(5, soln);
  }


}
