package org.dhis2.mobile.utils.date;


import org.joda.time.DateTime;

import java.util.Calendar;

public class CalendarUtils {

    protected static void moveToFistDayOfMonth(Calendar startDateCalendar,
            int month) {
        startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startDateCalendar.set(Calendar.MONTH, month);
    }

    protected static void moveDateToLastDayOfMonth(Calendar endDateCalendar, int month) {
        endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        endDateCalendar.set(Calendar.MONTH, month);
        endDateCalendar.add(Calendar.MONTH, 1);
        endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
    }

    protected static void getLastDayOfYear(Calendar endDateCalendar) {
        endDateCalendar.add(Calendar.YEAR, 1);
        moveDateToLastDayOfMonth(endDateCalendar, Calendar.JANUARY);
    }

    public static Calendar getInstanceDate(DateTime dateTime) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(dateTime.toDate());
        return calendar;
    }
}
