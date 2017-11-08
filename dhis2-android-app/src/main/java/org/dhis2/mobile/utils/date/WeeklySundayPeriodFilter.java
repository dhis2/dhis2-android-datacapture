package org.dhis2.mobile.utils.date;


import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class WeeklySundayPeriodFilter extends PeriodFilter {
    public WeeklySundayPeriodFilter(DateTime startDate, DateTime endDate) {
            super(fixStartDate(startDate), fixEndDate(endDate));
        }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        LocalDate fixedWeek = new LocalDate(endDate.withDayOfWeek(DateTimeConstants.SATURDAY));
        endDateCalendar.setTime(fixedWeek.toDate());
        return new DateTime(endDateCalendar.getTime());
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate.toDate());
        startDateCalendar.setTime(new LocalDate(startDate.withDayOfWeek(DateTimeConstants.SUNDAY)).toDate());
        return new DateTime(startDateCalendar.getTime());
    }
}
