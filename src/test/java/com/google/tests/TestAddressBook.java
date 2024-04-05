package com.google.tests;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAddressBook {

  public enum PhoneType {
    UNSPECIFIED(com.bonfonte.proto.addressbook.Person.PhoneType.PHONE_TYPE_UNSPECIFIED),
    MOBILE(com.bonfonte.proto.addressbook.Person.PhoneType.PHONE_TYPE_MOBILE),
    HOME(com.bonfonte.proto.addressbook.Person.PhoneType.PHONE_TYPE_HOME),
    WORK(com.bonfonte.proto.addressbook.Person.PhoneType.PHONE_TYPE_WORK);
    private com.bonfonte.proto.addressbook.Person.PhoneType protosType;
    private PhoneType(com.bonfonte.proto.addressbook.Person.PhoneType protosType) {
      this.protosType = protosType;
    }
    public static PhoneType fromMsgPhoneType(com.bonfonte.proto.addressbook.Person.PhoneType protosType) {
      return Arrays.stream(values())
          .filter(p -> p.protosType == protosType)
          .findFirst().orElse(null);
    }
    public com.bonfonte.proto.addressbook.Person.PhoneType toMsgPhoneType() {
      return this.protosType;
    }
  }

  private static class MessageBuilder {
    public com.bonfonte.proto.addressbook.Person.Builder personBuilder =
        com.bonfonte.proto.addressbook.Person.newBuilder();
    private com.bonfonte.proto.addressbook.Person.PhoneNumber.Builder newPhoneNumber() {
      return com.bonfonte.proto.addressbook.Person.PhoneNumber.newBuilder();
    }
    private MessageBuilder() {
    }

    public static MessageBuilder getNew() {
      return new MessageBuilder();
    }
  }

  @Data
  public static class PhoneNumber {
    private String number;
    private PhoneType phoneType;

    public static PhoneNumber make(String number, PhoneType type) {
      PhoneNumber result = new PhoneNumber();
      result.number = number;
      result.phoneType = type;
      return result;
    }
  }

  @Data
  public static class Person {
    private String name;
    private Integer id;
    private String email;
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    public com.bonfonte.proto.addressbook.Person toMsgPerson() {
      MessageBuilder builder = MessageBuilder.getNew();
      builder.personBuilder
          .setId(id)
          .setName(name)
          .setEmail(email);
      int maxPhones = 4;
      for (PhoneNumber phoneNumber : phoneNumbers) {
        if (phoneNumber.phoneType.toMsgPhoneType() == null) continue;
        maxPhones--;
        if (maxPhones <= 0) break;
        builder.personBuilder.addPhones(
            builder.newPhoneNumber()
                .setNumber(phoneNumber.number)
                .setType(phoneNumber.phoneType.toMsgPhoneType())
                .build());
      }
      return builder.personBuilder.build();
    }

    public Person addPhone(String number, PhoneType type) {
      PhoneNumber phoneNumber = PhoneNumber.make(name, type);
      if (phoneNumber != null) this.phoneNumbers.add(phoneNumber);
      return this;
    }
  }

  @Test
  public void testToMessage() {
    Person person = new Person();
    person.setEmail("fred@aol.com");
    person.setId(1);
    person.setName("Fred Jones");
    person
        .addPhone("650-123-4567", PhoneType.MOBILE)
        .addPhone("650-111-2222", PhoneType.HOME);
    com.bonfonte.proto.addressbook.Person msg = person.toMsgPerson();
    Assert.assertEquals(person.name, msg.getName());
    Assert.assertTrue(person.id == msg.getId());
    Assert.assertEquals(person.email, msg.getEmail());
    Assert.assertTrue(person.phoneNumbers.size() == msg.getPhonesCount());
    for (int i = 0; i < person.getPhoneNumbers().size(); i++) {
      Assert.assertEquals(person.phoneNumbers.get(i).number, msg.getPhones(i).getNumber());
      Assert.assertEquals(person.phoneNumbers.get(i).phoneType,
          PhoneType.fromMsgPhoneType(msg.getPhones(i).getType()));
    }
  }

  @Test
  public void testToMessage2() {
    Person person = new Person();
    person.setEmail("fred@aol.com");
    person.setId(1);
    person.setName("Fred Jones");
    person
        .addPhone("650-123-4567", PhoneType.MOBILE)
        .addPhone("650-111-2222", PhoneType.HOME);
    com.bonfonte.proto.addressbook.Person msg = person.toMsgPerson();
    Assert.assertEquals(person.name, msg.getName());
    Assert.assertTrue(person.id == msg.getId());
    Assert.assertEquals(person.email, msg.getEmail());
    Assert.assertTrue(person.phoneNumbers.size() == msg.getPhonesCount());
    for (int i = 0; i < person.getPhoneNumbers().size(); i++) {
      Assert.assertEquals(person.phoneNumbers.get(i).number, msg.getPhones(i).getNumber());
      Assert.assertEquals(person.phoneNumbers.get(i).phoneType,
          PhoneType.fromMsgPhoneType(msg.getPhones(i).getType()));
    }
  }


}
