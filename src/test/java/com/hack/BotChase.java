package com.hack;

import org.junit.Test;

import java.io.*;

public class BotChase {
  private static class Point {
    int x;
    int y;
    public Point(int xx, int yy) {
      x = xx; y = yy;
    }
  }

  private static void printSteps(int diffx, String left, String right) {
    String step = diffx < 0 ? right : left;
    if (diffx < 0) diffx = - diffx;
    while (diffx-- > 0) System.out.println(step);
  }

  private static void printSteps(Point m, Point p) {
    if (m == null) throw new RuntimeException("Illegal request missing bot");
    if (p == null) throw new RuntimeException("Illegal request missing princess");
    printSteps(m.x - p.x, "LEFT", "RIGHT");
    printSteps(m.y - p.y, "UP", "DOWN");
  }


  public static void runit(InputStream in) throws Exception {
    /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
    String line = bufferedReader.readLine().replaceAll(" ", "");
    int lines = Integer.parseInt(line);
    Point m = null;
    Point p = null;
    int lineStart = 0;
    int lineIncrement = 0;
    while (lines-- > 0 && bufferedReader.ready()) {
      line = bufferedReader.readLine();
      if (m == null) {
        int pos = line.indexOf('m');
        if (pos >= 0) {
          m = new Point(pos, lineStart);
          if (p != null) break;
        }
        lineIncrement = 1;
      }
      if (p == null) {
        int pos = line.indexOf('p');
        if (pos >= 0) {
          p = new Point(pos, lineStart);
          if (m != null) break;
        }
        lineIncrement = 1;
      }
      lineStart = lineStart + lineIncrement;
    }
    bufferedReader.close();
    printSteps(m, p);
  }

  public static void myrun(InputStream in) {
    try {
      runit(in);
    } catch (Exception e) {
      System.out.println("Exception occured : " + e.getClass().getSimpleName() + " " + e.getMessage());
      int index = 0;
      for (StackTraceElement s : e.getStackTrace()) {
        if (index++ > 10) break;
        System.out.println(index + " - " + s.getMethodName() + ":" + s.getLineNumber());
      }
    }
  }

  @Test
  public void TestExample1() {
    String test = "3\n"
        + "m--\n"
        + "---\n"
        + "--p\n"
        + "---\n";
    InputStream stream = new ByteArrayInputStream(test.getBytes());
    myrun(stream);
  }

  @Test
  public void TestExampleUp() {
    String test = "4\n"
        + "----\n"
        + "-p--\n"
        + "----\n"
        + "---m\n";
    InputStream stream = new ByteArrayInputStream(test.getBytes());
    myrun(stream);
  }

  public static void main(String[] args) {
    myrun(System.in);
  }

}