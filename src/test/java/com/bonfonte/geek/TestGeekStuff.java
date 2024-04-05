package com.bonfonte.geek;

import org.junit.Assert;
import org.junit.Test;

public class TestGeekStuff {



  // temp: input array
  // n: size of array
  //Function to rearrange  the array elements alternately.
  public static void rearrange(long arr[], int n){
    // Your code here
    long res[] = new long[n];
    int index = 0;
    int hi = n-1;
    int lo = 0;
    while (index < n) {
      if ((index & 1) == 1) res[index++] = arr[lo++];
      else res[index++] = arr[hi--];
    }
    for (int i = 0; i < n; i++) {
      arr[i] = res[i];
    }
  }

  @Test
  public void testRearrangeEven() {
    long arr[] = new long[] { 10L, 20L, 30L, 40L, 50L, 60L };
    long expected[] = new long[] { 60L, 10L, 50L, 20L, 40L, 30L };
    rearrange(arr, 6);
    for (int i = 0; i < 6; i++)
       Assert.assertTrue(expected[i] == arr[i]);
  }

  @Test
  public void testRearrangeOdd() {
    long arr[] = new long[] { 10L, 20L, 30L, 40L, 50L };
    long expected[] = new long[] { 50L, 10L, 40L, 20L, 30L };
    rearrange(arr, 5);
    for (int i = 0; i < 5; i++)
      Assert.assertTrue(expected[i] == arr[i]);
  }

}
