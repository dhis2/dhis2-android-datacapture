package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class WeeklyPeriodFilter extends PeriodFilter {
    public WeeklyPeriodFilter(DateTime startDate, DateTime endDate) {
            super(fixStartDate(startDate), fixEndDate(endDate));
        }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        LocalDate fixedWeek = new LocalDate(endDate.withDayOfWeek(7));
        endDateCalendar.setTime(fixedWeek.toDate());
        return new DateTime(endDateCalendar.getTime());
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate.toDate());
        startDateCalendar.setTime(new LocalDate(startDate.withDayOfWeek(1)).toDate());
        return new DateTime(startDateCalendar.getTime());
    }
}
