package edu.illinois.phantom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Slf4j
public class DocParser {

  public static final DocParser INSTANCE = new DocParser();

  private DocParser() {}

  public void extractTextFromFile(File file) {
    try {
      try (FileInputStream fis = new FileInputStream(file)) {
        if (file.getName().endsWith(".pdf")) {
          extractTextFromPDF(fis);
        } else if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
          extractTextFromPDF(fis);
        } else {
          throw new DocParseException("Unknown file format: " + file.getName());
        }
      }
    } catch (IOException ioe) {
      throw new DocParseException(
          "Exception while extracting text from file (" + file + "): " + ioe.getMessage(), ioe);
    }
  }



  public Optional<String> extractTextFromPDF(final InputStream is) {
    try (PDDocument doc = PDDocument.load(is)) {
      PDFTextStripper textStripper = new PDFTextStripper();
      String content = textStripper.getText(doc);
      log.debug("Parsed the given PDF document successfully:{}", content);
      return Optional.of(content);
    } catch (Exception e) {
      log.error("Exception while parsing the PDF document stream: {}", e.getMessage(), e);
    }
    return Optional.empty();
  }
}
