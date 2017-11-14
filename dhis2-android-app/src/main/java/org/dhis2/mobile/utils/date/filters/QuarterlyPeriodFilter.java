package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class QuarterlyPeriodFilter extends PeriodFilter {
    public QuarterlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month <= 3) {
            return startDate.withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(1);
        } else if (month <= 6) {
            return startDate.withMonthOfYear(DateTimeConstants.APRIL).withDayOfMonth(1);
        } else if (month <= 9) {
            return startDate.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
        } else if (month <= 12) {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(1);
        }
        return startDate;
    }
    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month <= 3) {
            return endDate.withMonthOfYear(DateTimeConstants.MARCH).withDayOfMonth(
                    endDate.dayOfMonth().getMaximumValue());
        } else if (month <= 6) {
            return endDate.withMonthOfYear(DateTimeConstants.JUNE).withDayOfMonth(
                    endDate.dayOfMonth().getMaximumValue());
        } else if (month <= 9) {
            return endDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(
                    endDate.dayOfMonth().getMaximumValue());
        } else if (month <= 12) {
            return endDate.withYear(endDate.getYear() + 1).withMonthOfYear(
                    DateTimeConstants.JANUARY).withDayOfYear(endDate.dayOfYear().getMaximumValue());
        }
        return endDate;
    }

}