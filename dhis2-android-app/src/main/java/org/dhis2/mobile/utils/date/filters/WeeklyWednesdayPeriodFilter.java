package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class WeeklyWednesdayPeriodFilter extends PeriodFilter {
    public WeeklyWednesdayPeriodFilter(DateTime startDate, DateTime endDate) {
            super(fixStartDate(startDate), fixEndDate(endDate));
        }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        String day = endDate.getDayOfMonth()+""+endDate.getMonthOfYear()+""+endDate.getYear();
        LocalDate fixedWeek = new LocalDate(endDate);
        if(endDate.getDayOfWeek()!=DateTimeConstants.TUESDAY) {
            fixedWeek = new LocalDate(endDate.withDayOfWeek(DateTimeConstants.TUESDAY));
            String fixedDay = fixedWeek.getDayOfMonth()+""+fixedWeek.getMonthOfYear()+""+fixedWeek.getYear();
            if (Integer.parseInt(day) > Integer.parseInt(fixedDay)) {
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
        startDateCalendar.setTime(new LocalDate(startDate.withDayOfWeek(DateTimeConstants.WEDNESDAY)).toDate());
        return new DateTime(startDateCalendar.getTime());
    }

    @Override
    public boolean apply() {
        if ((startDate == null && endDate == null) || selectedDate == null) {
            return false;
        }

        if (startDate != null && endDate != null) {
            // return true, if criteria is not between two dates
            // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
            return !((selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate))
                    && (selectedDate.isBefore(endDate) || selectedDate.isEqual(endDate)));
        }

        if (startDate != null) {
            // return true, if criteria is before startDate
            // return startDate.isBefore(selectedDate);
            return !(selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate));
        }

        // return true, if criteria is after endDate
        // return endDate.isAfter(selectedDate);
        return !(selectedDate.isBefore(endDate) || selectedDate.isEqual(endDate));
    }
}
