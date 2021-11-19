package edu.illinois.phantom.wordParser;


import edu.illinois.phantom.model.Experience;
import edu.illinois.phantom.model.MonthYear;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;

import static edu.illinois.phantom.wordParser.Constants.*;

public class ResumeParser {
    private static final String SKILLS_CSV = "/skills.csv";
    private static final String STOPWORDS_TXT = "/stopwords.txt";
    private static final Object SEPARATOR = ",";
    Set<String> skills = new HashSet<>();
    Set<String> stopWords = new HashSet<>();

    public ResumeParser() {
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(ResumeParser.class.getResourceAsStream(SKILLS_CSV)))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                String[] words = line.split(",");
                skills.addAll(Arrays.asList(words));
            }
            System.out.println("Read skills to cache " + skills.size());
        } catch (IOException e) {
            System.err.println("Error loading GAIA cache " + e.getMessage());
        }

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(ResumeParser.class.getResourceAsStream(STOPWORDS_TXT)))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                stopWords.add(line);
            }
            System.out.println("Read stopWords to cache " + stopWords.size());
        } catch (IOException e) {
            System.err.println("Error loading stopwords cache " + e.getMessage());
        }
    }

    /**
     * Helper function to extract skills from spacy nlp text
     * return list of skills extracted
     */

    public Optional<List<Experience>> parseResume(String text) {
        List<String> skillset = new ArrayList<>();
        List<Experience> experienceList = new ArrayList<>();
        extract_skills(text)
                .filter(skills -> skills.size() > 0)
                .ifPresent(skillset::addAll);
        Optional<Integer> duration = extractDuration(text);
        if (duration.isPresent()) {
            for (String s : skillset) {
                Experience.ExperienceBuilder experienceBuilder = Experience.builder();
                experienceBuilder.skill(s);
                experienceBuilder.duration(duration.get());
                experienceList.add(experienceBuilder.build());
            }
        }
        return Optional.of(experienceList);
    }

    private Optional<List<String>> extract_skills(String text) {
//       System.out.println(text);
        // set up pipeline properties
        List<String> skillset = new ArrayList<>();
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
        for (CoreLabel tok : doc.tokens()) {
            String token = tok.word();
            if (skills.contains(token) && !stopWords.contains(token)) {
                skillset.add(token);
            }
        }

        Collection<String> ngrams = StringUtils.getNgramsFromTokens(doc.tokens(), 1, 3);
        for (String ngram : ngrams) {
            if (skills.contains(ngram) && !skillset.contains(ngram)) {
                skillset.add(ngram);
            }
        }


        return Optional.of(skillset);
    }

    /**
     * Helper function to extract skills from spacy nlp text
     * return list of skills extracted
     */
    private Optional<Integer> extractDuration(String lowertext) {
        try {
            String trim = org.apache.commons.lang.StringUtils.trim(lowertext);
            Matcher matcher4 = pattern4.matcher(trim);
            Matcher matcher3 = pattern3.matcher(trim);
            Matcher matcher2 = pattern2.matcher(trim);
            Matcher matcher1 = pattern1.matcher(trim);
            Matcher matcher5 = pattern5.matcher(trim);
            if (matcher4.find()) {
                MonthYear startMonthYear = new MonthYear(matcher4.group("startMonth"),matcher4.group("startYear"));
                MonthYear endMonthYear = new MonthYear("present",matcher4.group("endYear"));
                int diffMonths = startMonthYear.diffMonths(endMonthYear);
                return Optional.of(diffMonths);
            } else if (matcher3.find()) {
                String pattern = "[^0-9|\\/]";
                String startDate = matcher3.group("startYear").replaceAll(pattern, "");;
                String splitBattern = "[^0-9]";
                String[] split = startDate.split(splitBattern);
                MonthYear startMonthYear = null;
                if(split != null && split.length > 1) {
                    String month = split[0];
                    String year = split[1];
                    startMonthYear = new MonthYear(month,year);
                }
                MonthYear endMonthYear = new MonthYear("present",matcher3.group("endYear"));
                if(startMonthYear != null && endMonthYear != null) {
                    int diffMonths = startMonthYear.diffMonths(endMonthYear);
                    return Optional.of(diffMonths);
                }
                else return Optional.of(0);
            } else if (matcher2.find()) {
                String pattern = "[^0-9|\\/]";
                String startDate = matcher2.group("startYear").replaceAll(pattern, "");
                String endDate = matcher2.group("endYear").replaceAll(pattern, "");
                String splitBattern = "[^0-9]";
                String[] split = startDate.split(splitBattern);
                MonthYear startMonthYear = null;
                if(split != null && split.length > 1) {
                    String month = split[0];
                    String year = split[1];
                    startMonthYear = new MonthYear(month,year);
                }
                MonthYear endMonthYear = null;
                split = endDate.split(splitBattern);
                if(split != null && split.length > 1) {
                    String month = split[0];
                    String year = split[1];
                    endMonthYear = new MonthYear(month,year);
                }
                if(startMonthYear != null && endMonthYear != null) {
                    int diffMonths = startMonthYear.diffMonths(endMonthYear);
                    return Optional.of(diffMonths);
                }
                else return Optional.of(0);
            } else if (matcher1.find()) {
                MonthYear startMonthYear = new MonthYear(matcher1.group("startMonth"),matcher1.group("startYear"));
                MonthYear endMonthYear = new MonthYear(matcher1.group("endMonth"),matcher1.group("endYear"));
                int diffMonths = startMonthYear.diffMonths(endMonthYear);
                return Optional.of(diffMonths);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage() + " " + lowertext);
        }

        return Optional.empty();
    }
}


