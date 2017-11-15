package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class WeeklySaturdayPeriodFilter extends PeriodFilter {
    public WeeklySaturdayPeriodFilter(DateTime startDate, DateTime endDate) {
            super(fixStartDate(startDate), fixEndDate(endDate));
        }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        String day = PeriodFilter.getDayString(endDate);
        LocalDate fixedWeek = new LocalDate(endDate);
        if(endDate.getDayOfWeek()!=DateTimeConstants.FRIDAY) {
            fixedWeek = new LocalDate(endDate.withDayOfWeek(DateTimeConstants.FRIDAY));
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
        String day = PeriodFilter.getDayString(startDate);
        LocalDate fixedWeek=new LocalDate(startDate.withDayOfWeek(DateTimeConstants.SATURDAY));

        if(startDate.getDayOfWeek()!=DateTimeConstants.SATURDAY) {
            fixedWeek = new LocalDate(startDate.withDayOfWeek(DateTimeConstants.SATURDAY));
            String fixedDay = fixedWeek.getDayOfMonth()+""+fixedWeek.getMonthOfYear()+""+fixedWeek.getYear();
            if (Integer.parseInt(day) > Integer.parseInt(fixedDay)) {
                fixedWeek = fixedWeek.minusWeeks(1);
            }
        }
        startDateCalendar.setTime(fixedWeek.toDate());
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
