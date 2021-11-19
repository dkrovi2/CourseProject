package edu.illinois.phantom.model;

import org.apache.commons.lang.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import static edu.illinois.phantom.wordParser.Constants.monthToInt;


public class MonthYear {
    String month;
    String year;


    public MonthYear(String month, String year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthYear monthYear = (MonthYear) o;
        return month == monthYear.month && year == monthYear.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, year);
    }

    public int diffMonths(MonthYear other) {
        LocalDate localDate;
        LocalDate otherLocalDate;
        //we will assume day is first day of month
        int day = 1;
        try {

            if (month.equalsIgnoreCase("present") || year.equalsIgnoreCase("present")) {
                localDate = LocalDate.now();
            } else {
                int thisIntMonth;
                if(StringUtils.isNumeric(month)) {
                    thisIntMonth = Integer.parseInt(month);
                }
                 else thisIntMonth = monthToInt.get(month);
                String normalizedYear = normalizeYear(year);
                localDate = LocalDate.of(Integer.parseInt(normalizedYear), thisIntMonth, day);
            }

            if (other.month.equalsIgnoreCase("present") || other.year.equalsIgnoreCase("present")) {
                otherLocalDate = LocalDate.now();
            } else {
                int otherIntmonth;
                if(StringUtils.isNumeric(other.month)) {
                    otherIntmonth = Integer.parseInt(other.month);
                }
                else otherIntmonth = monthToInt.get(other.month);
                String normalizedYear = normalizeYear(other.year);
                otherLocalDate = LocalDate.of(Integer.parseInt(normalizedYear), otherIntmonth, day);
            }

            if (localDate.compareTo(otherLocalDate) > 0) {
                Period period = Period.between(otherLocalDate, localDate);
                return period.getYears() * 12 + period.getMonths();

            } else {
                Period period = Period.between(localDate, otherLocalDate);
                return period.getYears() * 12 + period.getMonths();
            }
        } catch (DateTimeException e) {
            return 0;
        }
    }

    private String normalizeYear(String year) {
        String normalizedYear;
        if (year.length() < 4) {
            if (Integer.parseInt(year) >= 0 && Integer.parseInt(year) <= 21)
                normalizedYear = "20" + year;
            else
                normalizedYear = "19" + year;
        } else normalizedYear = year;
        return normalizedYear;
    }
}
