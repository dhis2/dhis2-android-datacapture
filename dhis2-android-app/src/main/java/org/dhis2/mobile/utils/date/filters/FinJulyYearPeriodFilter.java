package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class FinJulyYearPeriodFilter extends PeriodFilter {
    public FinJulyYearPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if (startDate == null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month <= 6) {
            return startDate.withMonthOfYear(DateTimeConstants.JUNE).withDayOfMonth(30).withYear(
                    startDate.getYear() - 1);
        } else {
            return startDate.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1).withYear(
                    startDate.getYear() - 1);
        }
    }

    private static DateTime fixEndDate(DateTime endDate) {
        if (endDate == null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month <= 6) {
            endDate = endDate.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1).withYear(
                    endDate.getYear() - 1);
        } else {
            return endDate.withMonthOfYear(DateTimeConstants.JUNE).withDayOfMonth(30);
        }
        return endDate.withDayOfMonth(endDate.dayOfMonth().getMaximumValue());
    }

}
