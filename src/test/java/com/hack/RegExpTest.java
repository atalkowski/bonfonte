package com.hack;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpTest {

  private static String replacer(String input) {
    // The challenge is to remove duplicate consequetive words in a sentence with the first word of the sequence.
    // E.g. Hello to to to you -> Hello to you.
    // Explanation of \b(\w+)(\s+\1\b)+  regexp to do this as shown below.
    // The () syntax marks out a regexp group ("part" I would call it) that can be referenced via the \1 operand.
    // The first \b (not part of the group 1) forces a match only on a word boundary;
    // Then first group (\w+) is the WORD found  - a sequence of [a-zA-Z]+ which is same as \w+;
    // The second group (\s+\1\b)+ represents a repeat of that WORD in group 1 (see \1 which selects it)
    // The repeat word must occur after a space (or multiple spaces) and end at a boundary.
    // The final "+" for that subsequent group means that any number repeat words be include.
    // The match.find(0) represents the entire duplicate word sequence match;
    // The match.find(1) represents the first WORD in the in a sequence of the word.
    // By using replacing all find(0) with find(1) in the code we scan all such sequences.
    String regex = "\\b(\\w+)(\\s+\\1\\b)+";
    Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    String output = input;
    Matcher m = p.matcher(output);
    // Check for subsequences of input that match the compiled pattern
    while (m.find()) {
      // log("Got group as %s and part 1 as %s", m.group(), m.group(1));
      output = output.replaceAll(m.group(0), m.group(1));
    }
    // log("Converted '%s' to '%s'", input, output);
    return output;
  }

  private static void log(String f, Object... objects) {
    System.out.println(String.format(f, objects));
  }

  @Test
  public void testR1() {
    String input = "Hello to to World";
    String output = replacer(input);
    Assert.assertEquals("Hello to World", output);
  }

  @Test
  public void testR2() {
    String input = "Hello to to World world world to to you you.";
    String output = replacer(input);
    Assert.assertEquals("Hello to World to you.", output);
  }

  private static String checkPassword1(String s) {
    // Must be 8 - 30 chars, start with alphabetic, must have at least 1 number, and can also have underscore
    String regex = "^[A-Za-z][A-Za-z0-9_]{7,29}$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    return m.matches() ? "Valid" : "Invalid";
  }

  @Test
  public void hackPasswordTest1() {
    String[] arr = new String[]{ "Hello", "eightchr", "Stranger1", "andy.talkowski33", "rabby_Bonfonte_777",
        "alpha___10___15___20___25___30", "alpha___10___15___20___25____31"};
    String[] exp = new String[]{ "Invalid", "Valid", "Valid", "Invalid", "Valid", "Valid", "Invalid"};
    for (int i = 0; i < arr.length; i++) {
      String res = checkPassword1(arr[i]);
      log("Password '%s' is %s", arr[i], res);
      Assert.assertEquals(exp[i], res);
    }
  }

}
