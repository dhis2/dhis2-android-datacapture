package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class WeeklyThursdayPeriodFilter extends PeriodFilter {

    public WeeklyThursdayPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        int day = endDate.getDayOfMonth();
        LocalDate fixedWeek = new LocalDate(endDate);
        if(endDate.getDayOfWeek()!=DateTimeConstants.WEDNESDAY) {
            fixedWeek = new LocalDate(endDate.withDayOfWeek(DateTimeConstants.WEDNESDAY));
            if (day > fixedWeek.getDayOfMonth()) {
                fixedWeek = fixedWeek.plusWeeks(1);
            }
        }
        endDateCalendar.setTime(fixedWeek.toDate());
        return new DateTime(endDateCalendar.getTime());
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate.toDate());
        startDateCalendar.setTime(new LocalDate(startDate.withDayOfWeek(DateTimeConstants.THURSDAY)).toDate());
        return new DateTime(startDateCalendar.getTime());
    }
}