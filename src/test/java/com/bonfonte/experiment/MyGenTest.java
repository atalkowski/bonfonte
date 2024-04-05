package com.bonfonte.experiment;

import org.junit.Assert;
import org.junit.Test;

public class MyGenTest {

  public static class MyGenString extends MyGen<String> {
  }

  @Test
  public void testGenericArray() {
    MyGenString[] array = new MyGenString[3];
    array[0] = new MyGenString(); // .add("Something"); doesn't compile.
    array[1] = new MyGenString();
    array[1].add("World").add("Hello").sort();
    array[2] = new MyGenString();
    array[2].add("the").add("quick").add("fox").sort();

    Assert.assertEquals(null, array[0].get(0));
    Assert.assertEquals("Hello", array[1].get(0));
    Assert.assertEquals("World", array[1].get(1));
    Assert.assertEquals("fox", array[2].get(0));
  }
}
