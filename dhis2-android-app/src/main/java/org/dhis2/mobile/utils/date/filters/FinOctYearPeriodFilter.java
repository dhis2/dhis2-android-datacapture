package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class FinOctYearPeriodFilter extends PeriodFilter {
    public FinOctYearPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month <= 2) {
            return startDate.withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(1);
        } else if (month <= 4) {
            return startDate.withMonthOfYear(DateTimeConstants.MARCH).withDayOfMonth(1);
        } else if (month <= 6) {
            return startDate.withMonthOfYear(DateTimeConstants.MAY).withDayOfMonth(1);
        } else if (month <= 8) {
            return startDate.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
        } else if (month <= 10) {
            return startDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
        } else {
            return startDate.withMonthOfYear(DateTimeConstants.NOVEMBER).withDayOfMonth(1);
        }
    }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month <= 2) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.FEBRUARY);
        } else if (month <= 4) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.APRIL);
        } else if (month <= 6) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.JUNE);
        } else if (month <= 8) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.AUGUST);
        } else if (month <= 10) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.OCTOBER);
        } else if (month <= 12) {
            endDate = endDate.withYear(endDate.getYear() + 1).withMonthOfYear(
                    DateTimeConstants.JANUARY);
        }
        return endDate.withDayOfMonth(endDate.dayOfMonth().getMaximumValue());
    }

}
