package com.bonfonte.hacker;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TestAnagrams {

  private static Map<Character, Integer> buildMap(String s) {
    Map<Character, Integer> map = new HashMap<>();
    if (s == null) return map;
    s = s.toLowerCase();
    for (int index = 0; index < s.length(); index++) {
      Character c = s.charAt(index);
      Integer count = map.computeIfAbsent(c, dft -> 0);
      map.put(c, count + 1);
    }
    return map;
  }

  static boolean isAnagram(String a, String b) {
    // Complete the function
    Map<Character, Integer> amap = buildMap(a);
    Map<Character, Integer> bmap = buildMap(b);
    return amap.equals(bmap);
  }

  @Test
  public void testAnas1() {
    Assert.assertTrue(isAnagram("Abc", "bac"));
    Assert.assertTrue(isAnagram("AbCDefAbcd", "abCdFedcba"));
    Assert.assertFalse(isAnagram("abCDefAbcda", "abCdFedcba"));
  }

  public static class Lexer {
    String input;
    int pos = 0;
    public Lexer(String s) {
      this.input = s == null ? "" : s;
    }

    private boolean isAlpha(char c) {
      return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    public String next() {
      StringBuilder token = new StringBuilder();
      while (pos < input.length()) {
        char c = input.charAt(pos++);
        if (isAlpha(c)) {
          token.append(c);
        } else {
          if (token.length() > 0) break;
        }
      }
      return token.toString();
    }
  }


  @Test
  public void testLex() {
    Lexer lex = new Lexer("He is a very good boy isn't he?");
    List<String> tokens = new ArrayList<>();
    for(;;) {
      String token = lex.next();
      if (token.length() == 0) break;
      tokens.add(token);
    }
    System.out.println(tokens.size());
    for (String token : tokens) {
      System.out.println(token);
    }
  }

  private static boolean isValidPatt(String exp) {
    try {
      Pattern pat = Pattern.compile(exp);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Test
  public void testPatts() {
    String tests[] = { "([A-Z])(.+)", "[AZ[a-z](a-z)", "batcatpat(nat}" };
    for (String p : tests) {
      boolean valid = isValidPatt(p);
      System.out.println("Patt '" + p + "' is " + (valid ? "Valid" : "Invalid"));
    }
  }

}
