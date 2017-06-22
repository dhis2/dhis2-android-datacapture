package org.dhis2.mobile.utils.date;


import org.joda.time.DateTime;

import java.util.Calendar;

public class QuarterlyPeriodFilter extends PeriodFilter {
    public QuarterlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        Calendar startDateCalendar = CalendarUtils.getInstanceDate(startDate);

        int month = startDateCalendar.get(Calendar.MONTH);
        if (month < 3) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JANUARY);
        } else if (month < 6) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.APRIL);
        } else if (month < 9) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JULY);
        } else if (month < 12) {
            CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.OCTOBER);
        }
        return new DateTime(startDateCalendar.getTime());
    }
    private static DateTime fixEndDate(DateTime endDate) {
        Calendar endDateCalendar = CalendarUtils.getInstanceDate(endDate);

        int month = endDateCalendar.get(Calendar.MONTH);
        if (month < 3) {
            CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.MARCH);
        } else if (month < 6) {
            CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.JUNE);
        } else if (month < 9) {
            CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.SEPTEMBER);
        } else if (month < 12) {
            CalendarUtils.getLastDayOfYear(endDateCalendar);
        }
        return new DateTime(endDateCalendar.getTime());
    }

}