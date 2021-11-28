package edu.illinois.phantom;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Constants {

  public static final Pattern pattern1 =
      Pattern.compile(
          "(?<startMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<startYear>\\d{2,4}).*(?<endMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<endYear>\\d{2,4})",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
  public static final Pattern pattern2 =
      Pattern.compile(
          "(?<startYear>(\\d{2}(.|..)\\d{4}).{1,4})(?<endYear>\\d{2}(.|..)\\d{4})",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
  public static final Pattern pattern3 =
      Pattern.compile(
          "(?<startYear>(\\d{2}(.|..)\\d{4}).{1,4})(?<endYear>present)",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
  public static final Pattern pattern4 =
      Pattern.compile(
          "(?<startMonth>jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\s|\\S)(?<startYear>\\d{2,4}).*(?<endYear>present)",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
  public static final Pattern pattern5 =
      Pattern.compile(
          "(?<startYear>\\d{2}\\/\\d{4})(.|..|\\s|\\S)(?<endYear>\\d{2}\\/\\d{4})",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
  public static final Map<String, Integer> monthToInt;

  static {
    monthToInt = new HashMap<>();
    monthToInt.put("jan", 1);
    monthToInt.put("feb", 2);
    monthToInt.put("mar", 3);
    monthToInt.put("apr", 4);
    monthToInt.put("may", 5);
    monthToInt.put("jun", 6);
    monthToInt.put("jul", 7);
    monthToInt.put("aug", 8);
    monthToInt.put("sep", 9);
    monthToInt.put("oct", 10);
    monthToInt.put("nov", 11);
    monthToInt.put("dec", 12);
    monthToInt.put("january", 1);
    monthToInt.put("february", 2);
    monthToInt.put("march", 3);
    monthToInt.put("april", 4);

    monthToInt.put("june", 6);
    monthToInt.put("july", 7);
    monthToInt.put("august", 8);
    monthToInt.put("september", 9);
    monthToInt.put("october", 10);
    monthToInt.put("november", 11);
    monthToInt.put("december", 12);
  }
}
