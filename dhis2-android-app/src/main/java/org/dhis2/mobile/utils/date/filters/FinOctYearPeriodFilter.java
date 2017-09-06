package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class FinOctYearPeriodFilter extends PeriodFilter {
    public FinOctYearPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if (startDate == null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month < 4) {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(30).withYear(
                    startDate.getYear() - 1);
        } else {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(30);
        }
    }

    private static DateTime fixEndDate(DateTime endDate) {
        if (endDate == null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month <= 4) {
            return endDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
        } else {
            return endDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1).withYear(endDate.getYear()+1);
        }
    }

}
