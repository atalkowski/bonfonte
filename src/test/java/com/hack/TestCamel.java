package com.hack;

import org.junit.Assert;
import org.junit.Test;

public class TestCamel {

  public enum ST {
    BEGIN, CAMEL, WORD
  }

  private static String toCamel(String in) {
    StringBuilder sb = new StringBuilder();
    StringBuilder dash = new StringBuilder();
    ST st = ST.BEGIN;
    for (int i = 0; i < in.length(); i++) {
      char ch = in.charAt(i);
      if (ch == '_') {
        if (st == ST.BEGIN) {
          sb.append(ch);
        } else {
          dash.append(ch);
          st = ST.CAMEL;
        }
      } else {
        if (dash.length() > 0) {
          dash = new StringBuilder();
        }
        switch (st) {
          case CAMEL:
            sb.append(Character.toUpperCase(ch));
            break;
          default:
            sb.append(ch);
            break;
        }
        st = ST.WORD;
      }
    }
    sb.append(dash);
    return sb.toString();
  }


  @Test
  public void testCase1() {
    String res = toCamel("__hello_world__");
    Assert.assertEquals("__helloWorld__", res);
  }

  @Test
  public void testCase2() {
    String res = toCamel("hello_world_this_is_it");
    Assert.assertEquals("helloWorldThisIsIt", res);
  }

  @Test
  public void testCase3() {
    String res = toCamel("__hello___world__this_is__it___");
    Assert.assertEquals("__helloWorldThisIsIt___", res);
  }

}
