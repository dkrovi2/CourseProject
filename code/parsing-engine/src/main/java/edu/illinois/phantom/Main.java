package edu.illinois.phantom;

import java.io.IOException;
import java.io.InputStream;

public class Main {
  public static void main(final String[] args) throws IOException {
    try (InputStream is = Main.class.getResourceAsStream("/1Amy.pdf")) {
      System.out.println("Content:" + DocParser.INSTANCE.extractTextFromPDF(is));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
