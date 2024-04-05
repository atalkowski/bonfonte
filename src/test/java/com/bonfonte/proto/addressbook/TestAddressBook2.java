package com.bonfonte.proto.addressbook;


import org.junit.Test;

public class TestAddressBook2 {

  @Test
  public void testToMessage() {
    Person person = Person.newBuilder()
        .setId(1)
        .setEmail("fred@aol.com")
        .setName("Fred Jones")
        .build();

  }

}
