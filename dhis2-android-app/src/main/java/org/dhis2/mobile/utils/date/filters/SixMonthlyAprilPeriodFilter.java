package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class SixMonthlyAprilPeriodFilter extends PeriodFilter {
    public SixMonthlyAprilPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month >= 4 && month <= 9) {
            return startDate.withMonthOfYear(DateTimeConstants.APRIL).withDayOfMonth(1);
        } else if( month>9) {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(1);
        } else{
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(1);
        }
    }
    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month >= 4 && month <= 9) {
            return endDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(30);
        }else if( month>9) {
            return endDate.withYear(endDate.getYear() + 1).withMonthOfYear(
                    DateTimeConstants.MARCH).withDayOfMonth(31);
        }else{
            return endDate.withYear(endDate.getYear()+1).withMonthOfYear(
                    DateTimeConstants.MARCH).withDayOfMonth(31);
        }
    }

}