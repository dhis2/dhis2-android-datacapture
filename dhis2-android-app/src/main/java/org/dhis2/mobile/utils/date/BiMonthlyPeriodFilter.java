package org.dhis2.mobile.utils.date;


import org.joda.time.DateTime;

import java.util.Calendar;

public class BiMonthlyPeriodFilter extends PeriodFilter {
    public BiMonthlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        Calendar startDateCalendar = CalendarUtils.getInstanceDate(startDate);

        int month = startDateCalendar.get(Calendar.MONTH);
        if (month < 2) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JANUARY);
        } else if (month < 4) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.MARCH);
        } else if (month < 6) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.MAY);
        } else if (month < 8) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.JULY);
        } else if (month < 10) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.SEPTEMBER);
        } else if (month < 12) {
             CalendarUtils.moveToFistDayOfMonth(startDateCalendar, Calendar.NOVEMBER);
        }
        return new DateTime(startDateCalendar.getTime());
    }

    private static DateTime fixEndDate(DateTime endDate) {
        Calendar endDateCalendar = CalendarUtils.getInstanceDate(endDate);

        int month = endDateCalendar.get(Calendar.MONTH);
        if (month < 2) {
             CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.FEBRUARY);
        } else if (month < 4) {
             CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.APRIL);
        } else if (month < 6) {
             CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.JUNE);
        }  else if (month < 8) {
             CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.AUGUST);
        }  else if (month < 10) {
             CalendarUtils.moveDateToLastDayOfMonth(endDateCalendar, Calendar.OCTOBER);
        } else if (month < 12) {
           CalendarUtils.getLastDayOfYear(endDateCalendar);
        }
        return new DateTime(endDateCalendar.getTime());
    }

}
