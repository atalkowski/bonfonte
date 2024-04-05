package com.bonfonte.math;

import org.junit.Test;

public class Heavens {

   /*
      This code written to test the hypothesis that an infinite universe full of stars distributed fairly
      evenly throughout space - would render a sky brilliant white - full of stars in all directions.
      Of course, we now know that stars are not evenly distributed at all - but in galaxies that are
      growing further apart and that some galaxies are speeding away so fast that their light has not
      (or never will) reach us anyway. But the theoretical question is asked anyway.
      The size of the universe is still in debate - as are the properties of space time. With light never
      reaching us from parts of the universe outside our "light cone" we cannot actually use this argument.

      However, the algorithm here tries to mimic the idea of stars occupying the sky in an evenly distributed
      universe where we assume some sort of uniformity to their distribution - on average at least.
      Assumption: if you zoom in on a part of the sky in an even distributed universe you should see (on average)
      the same number of stars in the sky most of the time. We will ignore the light cone concept ... and assume
      light will reach us from every star wherever it may be. (A clear load of tat - but hang in there).
      We also assume that stars are pretty much the same size. Another load of tat.

      Let's focus on just 1 area of the sky where there is a visible star quite close.
      So - if a star visibly occupies 1/100th of a particular section of the sky - we could divide that section
      into a grid 10 x 10 = 100 sub-sections and assume that when we zoom into these we will see the same sort of
      distribution of star(s). Therefore on average we will find 1 star in each of the 99 sections that were not
      occupied by the first star. And we could do this over and over - plotting stars on a theoretical map -
      each star occupying a small part that is ~10 times smaller than the previous zoom level of our original section.

      To step back - let's do the maths on an easy case where n = 2 (not 10x10) - so a star occupies 1/2 our section
      of sky at each zoom level.

      We will fill 1/2 + 1/4 + 1/8 + ... 1/2^n in the process of zooming in here. This sequence has a limit of 1.
      In other words the process WOULD fill the entire sky.
      Is this true for n = 4, n = 100, n = 10000000000? The answer would appear to be YES. That is - no matter how
      sparse stars are in this theoretical universe - an infinite universe would indeed be lit from every point.

      The algorithm falls short on calculating the amount of 'lit sky' - because zooming in quickly causes overflow
      of the arithmetic. But it does show that each zoom in does in fact fill near enough the same amount of sky as the
      initial star. So as long as you keep zooming in - always possible in an infinite universe - you will fill
      approximately (n-1)/n parts of the sky - which of course tends to 1 (the whole sky).

    ** TODO - create an algorithm which computes the formula for a general series of N.
     e.g. When n = 2, the series is 1/2, 3/4, 7/8   and Sn = (2^n - 1)/ 2^n ... Sk -> 1 as k -> infinity .
          for  n = 3, the series is 1/3, 5/9, 19/27, 65/81 and Sn = (something)/3^N.
          2 6 20 66
                                             27/81  = + 27
          2/3  * 1/3 = 2/9  + 1/3 = 5/9      45/81  = + 18
          4/9  * 1/3 = 4/27 + 5/9 = 19/27    57/81  = + 12
          8/27 * 1/3 = 8/81 + 19/27 = 65/81  65/81  = + 8

          for n = 4,   1/4, 7/16,
   */
  private static void log(String s, Object... o) {
    System.out.println(String.format(s, o));
  }

  private static double runSequence(int n, int times) {
    double result = 0.0;
    double space = 1.0;
    double box = 1.0;
    long div = n + 0L;
    long count = 1L;
    for (int t = 1; t <= times; t++) {
      double newWhite = count * space/div;
      if (newWhite <= 0) break;
      result = result + newWhite;
      log("Iteration %s : res = %s div = %s and count = %s", t, result, div, count);
      count = count * (n-1);
      div = div * n;
      if (div <= 0L || count <= 0L) break;
    }
    return result;
  }

  @Test
  public void test2() {
    runSequence(2, 10);
  }

  @Test
  public void test4() {
    runSequence(4, 20);
  }

  @Test
  public void test10() {
    runSequence(100, 9);
  }
}
