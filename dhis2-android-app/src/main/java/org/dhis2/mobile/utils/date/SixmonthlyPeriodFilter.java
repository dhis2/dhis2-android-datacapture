package org.dhis2.mobile.utils.date;


import org.joda.time.DateTime;

import java.util.Calendar;

public class SixmonthlyPeriodFilter extends PeriodFilter {
    public SixmonthlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        Calendar startDateCalendar = CalendarUtils.getInstanceDate(startDate);

        int month = startDateCalendar.get(Calendar.MONTH);
        if (month < 6) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JANUARY);
        } else if (month < 12) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JULY);
        }
        startDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return new DateTime(startDateCalendar.getTime());
    }
    private static DateTime fixEndDate(DateTime endDate) {
        Calendar endDateCalendar = CalendarUtils.getInstanceDate(endDate);

        int month = endDateCalendar.get(Calendar.MONTH);
        if (month < 6) {
            CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.JUNE);
        } else if (month < 12) {
            CalendarUtils.getLastDayOfYear(endDateCalendar);
        }
        endDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return new DateTime(endDateCalendar.getTime());
    }

}