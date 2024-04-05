package com.hack;

import com.bonfonte.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PseudoParseTest {

   /*
   Parse tagged data with the idea
   <tag1>data1</tag1> => data1
   <tag1>data1</tag2> => None
   <t1><t2>data2 is ok</t2></t1> => data2
   <t3><t4>data4</t4> data3 </t3> => None ... because data3 is mixed in with t4 (normally allowed but not today!!!
   <t5><t6>data6 here4</t6><t7> data7 here ok</t7></t5> => data6 here data7 here ok

<h1>Nayeem loves counseling</h1>
<h1><h1>Sanjay has no watch</h1></h1><par>So wait for a while</par>
<Amee>safat codes like a ninja</amee>
<SA premium>Imtiaz has a secret crush</SA premium>
    */
  private static class Markup {
    String name;
    String content = "";
    boolean success = false;
    List<Markup> kids = new ArrayList<>();
    public Markup(String name) {
      this.name = name;
    }

    public boolean isValid() {
      if (success) {
        if (content.length() > 0 && kids.size() > 0) return false;
        if (content.length() == 0) return kids.size() > 0;
        return content.length() > 0;
      }
      return false;
    }

    private static void addLine(StringBuilder sb, String text) {
      if (text.length() == 0) return;
      if (sb.length() > 0) sb.append("\n");
      sb.append(text);
    }

    private static void dohacker(StringBuilder sb, int depth, Markup m) {
      if (m.isValid()) {
        if (m.content.length() > 0) addLine(sb, m.content);
      }
      for (Markup kid : m.kids) {
        dohacker(sb, depth + 1, kid);
      }
    }

    public String toHack() {
      StringBuilder sb = new StringBuilder();
      dohacker(sb, 0, this);
      return sb.toString();
    }

    @Override
    public String toString() {
      String res = toHack();
      if (res.length() == 0) return "None";
      return res;
    }
  }

  private enum Symb {
    TAG, TEXT, ENDTAG, ERROR,
    EOI // End of input
  }
  private static class Token {
    String value;
    Symb symb;

    public Token(Symb symb) {
      this.symb = symb;
      this.value = "";
    }

    public Token(Symb symb, String value) {
      this.symb = symb;
      this.value = value;
    }
  }

  private static class Lexer {
    String input;
    int pos = 0;

    private char nextch() {
      char res = peekch();
      if (res != 0) pos++;
      return res;
    }

    private char peekch() {
      if (pos < input.length()) {
        return input.charAt(pos);
      }
      return 0;
    }

    public Lexer(String in) {
      input = in == null ? "" : in.trim();
    }

    public Token errorToken(String s, Object... objects) {
      String err = String.format(s, objects);
      return new Token(Symb.ERROR, String.format("%s (at position %s)", err, pos));
    }

    public boolean isEOI() {
      return pos >= input.length();
    }

    public Token nextToken() {
      Symb symb = null;
      StringBuilder sb = new StringBuilder();
      while (true) {
        char ch = nextch();

        switch (ch) {
          case 0:
            if (symb == null) return new Token(Symb.EOI);
            switch (symb) {
              case TAG:
              case ENDTAG:
                if (sb.length() == 0) {
                  return errorToken("Invalid empty %s", symb);
                }
                break;
              default:
                break;
            }
            return new Token(symb, sb.toString());
          case '<': // Assume were are reading a tag or end tag:
            if (symb != null) {
              pos--;
              return new Token(symb, sb.toString());
            }
            char next = peekch();
            switch (next) {
              case 0:
                return errorToken("Unexpected end of input after < character");
              case '/':
                pos++;
                symb = Symb.ENDTAG;
                break; // Go read the end tag;
              case '>':
                pos++; // Ensure discard this character
                return errorToken("Unexpected end tag character " + next);
              case '<':
                pos++; // Ensure discard this character
                return errorToken("Unexpected start tag character " + next);
              default:
                symb = Symb.TAG;
                break;
            }
            break;
          case '>':
            if (symb == null || symb == Symb.TEXT) return errorToken("Unexpected end tag character");
            return new Token(symb, sb.toString());
          default:
            sb.append(ch);
            if (symb == null) symb = Symb.TEXT;
            break;
        }
      }
    }
  }

  private static void logdbg(String s, Object... obs) {
    if (false) log(s, obs);
  }

  private static void log(String s, Object... obs) {
    System.out.println(String.format(s, obs));
  }

  private static Markup brokenMarkup(String error, Object...obs) {
    Markup res = new Markup("ERROR");
    res.success = false;
    res.content = String.format(error, obs);
    return res;
  }

  private static Markup parseTag(Lexer lex, Markup res) {
    while (!lex.isEOI()) {
      Token token = lex.nextToken(); // Decide ... do we skip whit space in token? check lexer;
      switch (token.symb) {
        case ERROR:
          return brokenMarkup(token.value);
        case EOI:
          return res;
        case ENDTAG:
          if (res != null) {
            if (res.name.equals(token.value)) {
              res.success = true;
              return res;
            }
            return brokenMarkup("End tag %s does not match tag %s", token.value, res.name);
          }
          return brokenMarkup("Unexpected end tag %s", token.value);
        case TAG:
          Markup markup = new Markup(token.value);
          if (res != null) {
            Markup child = parseTag(lex, markup);
            if (child == null) return brokenMarkup("No tag was found");
            if (child.success) {
              res.kids.add(child);
            } else {
              return brokenMarkup("Failed to parse child for tag %s : %s", token.value, child.content);
            }
          } else {
            res = markup;
          }
          continue;
        case TEXT:
          // if (res == null) return brokenMarkup("Content found where tag expected");
          // We are skipping irrlevant garbage between tags - in the hope that this is the expected behavior.
          if (res != null) {
            res.content += token.value;
          }
          continue;
        default:
          return brokenMarkup("Unexpected symbol type %s", token);
      }
    }
    return brokenMarkup("Unexpected end of input when trying to read tag");
  }

  private static List<Markup> parse(String input) {
      Lexer lex = new Lexer(input);
      List<Markup> result = new ArrayList<>();
      while (!lex.isEOI()) {
        Markup markup = parseTag(lex, null);
        if (markup != null) {
          result.add(markup);
        }
      }
      if (result.isEmpty()) {
        // Just sugar to get around a blank or useless line of markup.
        result.add(brokenMarkup("No markup found"));
      }
      return result;
  }

  private static String parseToHacker(String input) {
    List<Markup> list = parse(input);
    StringBuilder sb = new StringBuilder();
    String hackos = list.stream().map(Markup::toHack).filter(s -> s.length() > 0)
        .collect(Collectors.joining("\n"));
    return hackos.isEmpty() ? "None" : hackos;
  }


  private void parseTest(String in, String... expected) {
    List<Markup> list = parse(in);
    for (Markup item : list) {
      log("%s", item.toString());
    }

    Assert.assertEquals(list.size(), expected.length);
    for (int i = 0; i < list.size(); i++) {
      logdbg("Checking '%s' matches '%s'", expected[i], list.get(i));
      Assert.assertEquals(expected[i], list.get(i).toString());
    }
  }

  @Test
  public void parse1() {
    String in = "<t1>Hello world</t1>";
    parseTest(in, "Hello world");
  }

  @Test
  public void parse2() {
    String in = "<h1>Nayeem loves counseling</h1>" +
        "<h1><h1>Sanjay has no watch</h1></h1><par>So wait for a while</par>" +
        "<Amee>safat codes like a ninja</amee>" +
        "<SA premium>Imtiaz has a secret crush</SA premium>";
    parseTest(in, "Nayeem loves counseling", "Sanjay has no watch", "So wait for a while",
        "None", "Imtiaz has a secret crush");
  }

  @Test
  public void parseErr1() {
    parseTest("<>", "None");
  }

  @Test
  public void parseErr2() {
    parseTest("<H1>Here be <b>dragons</b></H1>", "dragons");
  }

  private void simTest(String line, String output) {
    List<Markup> list = parse(line);
    StringBuilder sb = new StringBuilder();
    //Write your code here
    int valid = 0;
    for (Markup m : list) {
      if (m.success) {
        valid++;
        if (sb.length() > 0) sb.append("\n");
        sb.append(m.toString());
      }
    }
    String result = valid == 0 ? "None" : sb.toString();
    log("Parsed '%s' to '%s' which is %s", line, result, result.equals(output) ? "correct" : "incorrect");
    Assert.assertEquals(output, result);
  }

  private void simulate(String line) {
    List<Markup> list = parse(line);
    //Write your code here
    int valid = 0;
    for (Markup m : list) {
      if (m.success) {
        valid++;
        System.out.println(m.toString());
      }
    }
    if (valid == 0)
      System.out.println("None");
  }

  @Test
  public void testHacker1() {
    String[] hacks = new String[] {
      "<h1>Nayeem loves counseling</h1>",
      "<h1><h1>Sanjay has no watch</h1></h1><par>So wait for a while</par>",
      "<Amee>safat codes like a ninja</amee>"
    };
    String[] exps = new String[] {"Nayeem loves counseling", "Sanjay has no watch\nSo wait for a while", "None" };
    for (int i = 0; i < hacks.length; i++) {
      simTest(hacks[i], exps[i]);
    }
  }

  @Test
  public void testHack2() {
    String[] hacks = new String[] {
      "<h1>some</h1>",
      "<h1>had<h1>public</h1></h1>",
      "<h1>had<h1>public</h1515></h1>",
      "<h1><h1></h1></h1>",
      "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<",
      ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
      "<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>",
      "<>hello</>",
      "<>hello</><h>dim</h>",
      "<>hello</><h>dim</h>>>>>" };
    String[] exps = new String[] { "some", "public", "None", "None", "None", "None", "None", "None", "dim", "dim" };
    int i = 0;
    for (String s : hacks) {
      simTest(s, exps[i++]);
    }
  }

  @Test
  public void testHackPublic() {
    simTest( "carpola<h1>had<h1>public</h1></h1>", "public");
  }

  @Test
  public void testHackLf() {
    simTest( "<h1>had<h1>public convenience\nis great!</h1></h1>",
        "public convenience\nis great!");
  }

  private static int getLFPos(String data, int pos) {
    while (pos < data.length()) {
      if (data.charAt(pos) == '\n') return pos;
      pos++;
    }
    return pos;
  }

  @Test
  public void testHack3() {
    String data = FileUtils.loadFile("samples/hacktest1.txt");
    int pos = 0;
    int end = 0;
    while (pos < data.length()) {
      end = getLFPos(data, pos);
      if (end > pos) {
        String line = data.substring(pos, end);
        String hacko = parseToHacker(line);
        System.out.println(hacko);
      }
      pos = end + 1;
    }
  }

}
