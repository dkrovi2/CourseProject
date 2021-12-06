package edu.illinois.phantom.parseengine;

import static edu.illinois.phantom.util.Constants.pattern1;
import static edu.illinois.phantom.util.Constants.pattern2;
import static edu.illinois.phantom.util.Constants.pattern3;
import static edu.illinois.phantom.util.Constants.pattern4;
import static edu.illinois.phantom.util.Constants.pattern5;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

@Slf4j
public class DocParser {

  public static final DocParser INSTANCE = new DocParser();

  private DocParser() {
  }

  public Optional<List<String>> extractTextFromFile(File file) {
    try {
      try (FileInputStream fis = new FileInputStream(file)) {
        if (file.getName().endsWith(".pdf")) {
          return Optional.of(getResumeSectionsFromContent(extractTextFromPDF(fis)));
        } else if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
          return Optional.of(getResumeSectionsFromContent(extractTextFromDoc(fis)));
        } else {
          throw new DocParseException("Unknown file format: " + file.getName());
        }
      }
    } catch (IOException ioe) {
      throw new DocParseException(
        "Exception while extracting text from file (" + file + "): " + ioe.getMessage(), ioe);
    }
  }

  public List<String> extractTextFromDoc(final InputStream is) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(Objects.requireNonNull(is))) {
      return doc.getParagraphs().stream().map(XWPFParagraph::getText).collect(Collectors.toList());
    }
  }

  public List<String> extractTextFromPDF(final InputStream is) throws IOException {
    try (PDDocument doc = PDDocument.load(is)) {
      PDFTextStripper textStripper = new PDFTextStripper();
      String content = textStripper.getText(doc);
      log.debug("Parsed the given PDF document successfully:{}", content);
      String[] lines = content.split(System.lineSeparator());
      return Arrays.asList(lines);
    }
  }

  private static boolean textMatchesPattern(final String lowerCaseText) {
    return pattern4.matcher(lowerCaseText).find()
      || pattern3.matcher(lowerCaseText).find()
      || pattern2.matcher(lowerCaseText).find()
      || pattern1.matcher(lowerCaseText).find()
      || pattern5.matcher(lowerCaseText).find();
  }

  private static List<String> getResumeSectionsFromContent(final List<String> lines) {
    List<String> textList = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      String lowerCaseText = StringUtils.lowerCase(line);
      if (textMatchesPattern(lowerCaseText)) {
        textList.add(sb.toString());
        sb = new StringBuilder();
      }
      sb.append(lowerCaseText);
    }
    if (sb.length() > 0) {
      textList.add(sb.toString());
    }
    return textList;
  }
}
