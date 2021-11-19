package edu.illinois.phantom.wordParser;

import java.util.*;
import java.util.regex.Pattern;

public class Constants {

    Set<String> Education = new HashSet<>(Arrays.asList(
            "BE", "B.E.", "B.E", "BS", "B.S", "ME", "M.E",
            "M.E.", "MS", "M.S", "BTECH", "MTECH",
            "SSC", "HSC", "CBSE", "ICSE", "X", "XII", "BSC", "DIPLOMA", "BACHELOR", "MASTER",
            "MASTERS"
    ));
    //    public static final Pattern pattern1 = Pattern.compile("(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(\\d{2,4}).*(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(\\d{2,4})",Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Pattern pattern1 = Pattern.compile("(?<startMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<startYear>\\d{2,4}).*(?<endMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<endYear>\\d{2,4})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Pattern pattern2 = Pattern.compile("(?<startYear>(\\d{2}(.|..)\\d{4}).{1,4})(?<endYear>\\d{2}(.|..)\\d{4})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Pattern pattern3 = Pattern.compile("(?<startYear>(\\d{2}(.|..)\\d{4}).{1,4})(?<endYear>present)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Pattern pattern4 = Pattern.compile("(?<startMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<startYear>\\d{2,4}).*(?<endYear>present)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Pattern pattern5 = Pattern.compile("(?<startYear>\\d{2}\\/\\d{4})(.|..|\\s|\\S)(?<endYear>\\d{2}\\/\\d{4})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    public static final Map<String, Integer> monthToInt =
            new HashMap<String, Integer>() {
                {
                    put("jan", 1);
                    put("feb", 2);
                    put("mar", 3);
                    put("apr", 4);
                    put("may", 5);
                    put("jun", 6);
                    put("jul", 7);
                    put("aug", 8);
                    put("sep", 9);
                    put("oct", 10);
                    put("nov", 11);
                    put("dec", 12);
                    put("january", 1);
                    put("february", 2);
                    put("march", 3);
                    put("april", 4);
                    put("may", 5);
                    put("june", 6);
                    put("july", 7);
                    put("august", 8);
                    put("september", 9);
                    put("october", 10);
                    put("november", 11);
                    put("december", 12);
                }
            };


}
