package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class SixmonthlyAprilPeriodFilter extends PeriodFilter {
    public SixmonthlyAprilPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month <= 6) {
            return startDate.withMonthOfYear(DateTimeConstants.APRIL).withDayOfMonth(2);
        } else if (month <= 12) {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(2);
        }
        return startDate;
    }
    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month <= 6) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.OCTOBER);
            endDate = endDate.withDayOfMonth(endDate.dayOfMonth().getMinimumValue());
        } else if (month <= 12) {
            endDate = endDate.withYear(endDate.getYear() + 1).withMonthOfYear(
                    DateTimeConstants.APRIL);
            endDate = endDate.withDayOfYear(endDate.dayOfYear().getMaximumValue());
        }
        return endDate;
    }

}