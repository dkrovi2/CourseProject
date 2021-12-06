package edu.illinois.phantom.parseengine;

import static edu.illinois.phantom.util.Constants.pattern1;
import static edu.illinois.phantom.util.Constants.pattern2;
import static edu.illinois.phantom.util.Constants.pattern3;
import static edu.illinois.phantom.util.Constants.pattern4;
import edu.illinois.phantom.model.MonthYear;
import edu.illinois.phantom.model.Resume;
import edu.illinois.phantom.model.SkillInfo;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public class ResumeParser {

  public static final ResumeParser INSTANCE = new ResumeParser();

  private static final String SKILLS_CSV = "/skills.csv";
  private static final String STOPWORDS_TXT = "/stopwords.txt";

  private final Set<String> skills = new TreeSet<>();
  private final Set<String> stopWords = new TreeSet<>();

  private ResumeParser() {
    try (InputStream is = ResumeParser.class.getResourceAsStream(SKILLS_CSV)) {
      skills.addAll(
        new HashSet<>(
          IOUtils.readLines(Objects.requireNonNull(is), Charset.defaultCharset()).stream()
            .map(s -> Arrays.asList(s.split(",")))
            .flatMap(List::stream)
            .collect(Collectors.toList())));
      log.debug("Read skills to cache: {}", skills);
    } catch (IOException e) {
      System.err.println("Error loading GAIA cache " + e.getMessage());
    }

    try (InputStream is = ResumeParser.class.getResourceAsStream(STOPWORDS_TXT)) {
      stopWords.addAll(
        new HashSet<>(IOUtils.readLines(Objects.requireNonNull(is), Charset.defaultCharset())));
      log.debug("Read stopWords to cache: {}", stopWords);
    } catch (IOException e) {
      System.err.println("Error loading stopwords cache " + e.getMessage());
    }
  }

  public Optional<Resume> parseResumeFile(final File file) {
    log.info("Parsing resume file at {}", file.toString());
    return DocParser.INSTANCE
      .extractTextFromFile(file)
      .map(
        sections ->
          Resume.builder()
            .location(file.getAbsolutePath())
            .skills(
              sections.stream()
                .map(this::parseResume)
                .flatMap(List::stream)
                .collect(
                  Collectors.groupingBy(
                    SkillInfo::getSkill,
                    Collectors.summingInt(SkillInfo::getDuration)))
                .entrySet()
                .stream()
                .map(
                  entry ->
                    SkillInfo.builder()
                      .skill(entry.getKey())
                      .duration(entry.getValue())
                      .build())
                .sorted(Comparator.comparing(SkillInfo::getSkill))
                .collect(Collectors.toList()))
            .build());
  }

  /**
   * Helper function to extract skills from spacy nlp text return list of skills extracted
   */
  public List<SkillInfo> parseResume(String text) {
    Optional<Integer> durationOptional = extractDuration(text);
    return durationOptional
      .map(
        duration ->
          extractSkills(text).stream()
            .map(s -> SkillInfo.builder().skill(s).duration(duration).build())
            .collect(Collectors.toList()))
      .orElse(new ArrayList<>());
  }

  private Set<String> extractSkills(String text) {
    // set up pipeline properties
    Properties props = new Properties();
    // set the list of annotators to run
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    // example of how to customize the PTBTokenizer (these are just random example settings!!)
    props.setProperty("tokenize.options", "splitHyphenated=false,americanize=false");
    // build pipeline
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // create a document object
    CoreDocument doc = new CoreDocument(text);
    // annotate
    pipeline.annotate(doc);
    // display tokens
    Set<String> skillSet =
      doc.tokens().stream()
        .map(CoreLabel::word)
        .filter(token -> skills.contains(token) && !stopWords.contains(token))
        .collect(Collectors.toSet());

    StringUtils.getNgramsFromTokens(doc.tokens(), 1, 3).stream()
      .filter(ngram -> skills.contains(ngram) && !skillSet.contains(ngram))
      .forEach(skillSet::add);

    return skillSet;
  }

  /**
   * Helper function to extract skills from spacy nlp text return list of skills extracted
   */
  private Optional<Integer> extractDuration(String lowertext) {
    try {
      String trim = org.apache.commons.lang3.StringUtils.trim(lowertext);
      Matcher matcher4 = pattern4.matcher(trim);
      Matcher matcher3 = pattern3.matcher(trim);
      Matcher matcher2 = pattern2.matcher(trim);
      Matcher matcher1 = pattern1.matcher(trim);

      if (matcher4.find()) {
        MonthYear startMonthYear =
          new MonthYear(matcher4.group("startMonth"), matcher4.group("startYear"));
        MonthYear endMonthYear = new MonthYear("present", matcher4.group("endYear"));
        int diffMonths = startMonthYear.diffMonths(endMonthYear);
        return Optional.of(diffMonths);
      } else if (matcher3.find()) {
        String pattern = "[^0-9|\\/]";
        String startDate = matcher3.group("startYear").replaceAll(pattern, "");
        String splitBattern = "[^0-9]";
        String[] split = startDate.split(splitBattern);
        MonthYear startMonthYear = null;
        if (split.length > 1) {
          String month = split[0];
          String year = split[1];
          startMonthYear = new MonthYear(month, year);
        }
        MonthYear endMonthYear = new MonthYear("present", matcher3.group("endYear"));
        if (startMonthYear != null) {
          int diffMonths = startMonthYear.diffMonths(endMonthYear);
          return Optional.of(diffMonths);
        } else return Optional.of(0);
      } else if (matcher2.find()) {
        String pattern = "[^0-9|\\/]";
        String startDate = matcher2.group("startYear").replaceAll(pattern, "");
        String endDate = matcher2.group("endYear").replaceAll(pattern, "");
        String splitBattern = "[^0-9]";
        String[] split = startDate.split(splitBattern);
        MonthYear startMonthYear = null;
        if (split.length > 1) {
          String month = split[0];
          String year = split[1];
          startMonthYear = new MonthYear(month, year);
        }
        MonthYear endMonthYear = null;
        split = endDate.split(splitBattern);
        if (split.length > 1) {
          String month = split[0];
          String year = split[1];
          endMonthYear = new MonthYear(month, year);
        }
        if (startMonthYear != null && endMonthYear != null) {
          int diffMonths = startMonthYear.diffMonths(endMonthYear);
          return Optional.of(diffMonths);
        } else return Optional.of(0);
      } else if (matcher1.find()) {
        MonthYear startMonthYear =
          new MonthYear(matcher1.group("startMonth"), matcher1.group("startYear"));
        MonthYear endMonthYear =
          new MonthYear(matcher1.group("endMonth"), matcher1.group("endYear"));
        int diffMonths = startMonthYear.diffMonths(endMonthYear);
        return Optional.of(diffMonths);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + " " + lowertext);
    }

    return Optional.empty();
  }
}
