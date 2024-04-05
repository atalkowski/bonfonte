package com.bonfonte.hacker;

import org.junit.Test;

import java.util.*;

public class PriorQTest {


  public static class Student implements Comparable<Student> {
    public String id;
    public String name;
    public Double gpa;

    public Student(String name, double gpa, String id) {
      this.id = id;
      this.name = name;
      this.gpa = gpa;
    }

    public int compareTo(Student other) {
      int res = gpa.compareTo(other.gpa);
      if (res != 0) return 0 - res;
      res = name.compareTo(other.name);
      return res == 0 ? id.compareTo(other.id) : res;
    }

    public String getName() {
      return name;
    }
  }

  private static class Priorities {

    private TreeSet<Student> students = new TreeSet<>();

    public Student next(boolean consume) {
      Student s = students.first();
      if (consume) students.remove(s);
      return s;
    }
    public List<Student> getStudents() {
      List<Student> res = new ArrayList<>();
      while (!students.isEmpty()) {
        res.add(next(true));
      }
      return res;
    }

    public List<Student> getStudents(List<String> events) {
      for (String event : events) {
        String[] e = event.split(" ");
        switch (e[0]) {
          case "SERVED": next(true);
          continue;
          case "ENTER":
            Double gpa = Double.parseDouble(e[2]);
            Student s = new Student(e[1], gpa, e[3]);
            students.add(s);
            break;
        }
      }
      return getStudents();
    }
  }

  @Test
  public void testStuds() {
    List<String> events = Arrays.asList("ENTER John 3.75 50",
            "ENTER Mark 3.8 24",
            "ENTER Shafaet 3.7 35",
            "SERVED",
            "SERVED",
            "ENTER Samiha 3.85 36",
            "SERVED",
            "ENTER Ashley 3.9 42",
            "ENTER Maria 3.6 46",
            "ENTER Anik 3.95 49",
            "ENTER Dan 3.95 50",
            "SERVED");
    Priorities p = new Priorities();
    List<Student> studs = p.getStudents(events);
    for (Student s : studs) {
      System.out.println(s.name);
    }
  }

}
