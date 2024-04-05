package com.bonfonte.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtils {

  public static String loadFile(String fileName) {
    try {
      ClassLoader loader = FileUtils.class.getClassLoader();
      String resourcePath = loader.getResource(fileName).getPath();
      BufferedReader reader  = new BufferedReader(new FileReader(resourcePath));
      StringBuilder stringBuilder = new StringBuilder();
      String line = null;
      String ls = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append(ls);
      }
      // delete the last new line separator
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      reader.close();
      return stringBuilder.toString();
    } catch (Exception e) {
      System.out.println("Failed to load file " + fileName + ": " + e.getMessage());
      throw new RuntimeException("Failed to load file " + fileName + ": " + e.getMessage());
    }
  }
}
