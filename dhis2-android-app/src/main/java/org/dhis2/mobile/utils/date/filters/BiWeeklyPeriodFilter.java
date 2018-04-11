package org.dhis2.mobile.utils.date.filters;


import android.support.annotation.NonNull;

import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.Date;

public class BiWeeklyPeriodFilter extends PeriodFilter {
    public BiWeeklyPeriodFilter(DateTime startDate, DateTime endDate) {
            super(fixStartDate(startDate), fixEndDate(endDate));
        }

    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        if(isDateAfterFirstStartWeeklyDay(endDate)){
            if (isDateBeforeFirstEndWeekly(endDate)){
                endDate=getEndDayOfFirstBiWeek(endDate);
            }else if (isDateBeforeSecondEndWeek(endDate)) {
                endDate=getEndDayOfSecondBiWeek(endDate);
            }else{
                endDate=getEndDayOfNextMonthBiWeek(endDate);
            }
        }else{
            endDate=getEndDayOfPreviousMonthBiWeek(endDate);
        }
        return getEndOfADay(endDate);
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        if(isDateAfterFirstStartWeeklyDay(startDate)){

            if (isDateBeforeFirstEndWeekly(startDate)){
                startDate=getStartDayOfFirstBiWeek(startDate);

            }else if (isDateBeforeSecondEndWeek(startDate)) {
                startDate=getStartDayOfSecondBiWeek(startDate);

            }else{
                startDate=getStartDayOfNextMonthBiWeek(startDate);
            }

        }else{
            startDate=getStartDayOfPreviousMonthBiWeek(startDate);
        }

        return new DateTime(startDate);
    }

    private static boolean isDateBeforeSecondEndWeek(DateTime endDate) {
        return endDate.isBefore(getEndDayOfSecondBiWeek(endDate)) || endDate.isEqual(getEndDayOfSecondBiWeek(endDate));
    }

    private static boolean isDateBeforeFirstEndWeekly(DateTime endDate) {
        return endDate.isBefore(getEndDayOfFirstBiWeek(endDate)) || endDate.isEqual(getEndDayOfFirstBiWeek(endDate));
    }

    private static boolean isDateAfterFirstStartWeeklyDay(DateTime endDate) {
        return endDate.isAfter(getStartDayOfFirstBiWeek(endDate)) || endDate.isEqual(getStartDayOfFirstBiWeek(endDate));
    }

    private static DateTime getStartDayOfFirstBiWeek(DateTime date){
        LocalDate localDate = new LocalDate( date.getYear(), date.getMonthOfYear(), 1 );
        while ( localDate.getDayOfWeek() != DateTimeConstants.MONDAY ) {
            localDate = localDate.plusDays( 1 );
        }
        return localDate.toDateTimeAtStartOfDay();
    }

    private static DateTime getEndDayOfFirstBiWeek(DateTime date){
        DateTime startDay = getStartDayOfFirstBiWeek(date);
        LocalDate localDate = new LocalDate(startDay);
        localDate = localDate.plusDays( 13 );
        return localDate.toDateTimeAtStartOfDay();
    }

    private static DateTime getStartDayOfSecondBiWeek(DateTime date){
        LocalDate localDate = new LocalDate(getStartDayOfFirstBiWeek(date));
        localDate = localDate.plusDays(14);
        return localDate.toDateTimeAtStartOfDay();
    }

    private static DateTime getEndDayOfSecondBiWeek(DateTime date){
        DateTime startDay = getStartDayOfSecondBiWeek(date);
        LocalDate localDate = new LocalDate(startDay);
        localDate = localDate.plusDays( 13 );
        return localDate.toDateTimeAtStartOfDay();
    }
    private static DateTime getStartDayOfNextMonthBiWeek(DateTime date){
        LocalDate localDate = new LocalDate(getStartDayOfFirstBiWeek(date));
        localDate = localDate.plusDays(28);
        return localDate.toDateTimeAtStartOfDay();
    }

    private static DateTime getEndDayOfNextMonthBiWeek(DateTime date){
        DateTime startDay = getStartDayOfNextMonthBiWeek(date);
        LocalDate localDate = new LocalDate(startDay);
        localDate = localDate.plusDays( 13 );
        return localDate.toDateTimeAtStartOfDay();
    }
    private static DateTime getStartDayOfPreviousMonthBiWeek(DateTime date){
        LocalDate localDate = new LocalDate(getStartDayOfFirstBiWeek(date));
        localDate = localDate.minusDays(14);
        return localDate.toDateTimeAtStartOfDay();
    }
    private static DateTime getEndDayOfPreviousMonthBiWeek(DateTime date){
        DateTime startDay = getStartDayOfFirstBiWeek(date);
        LocalDate localDate = new LocalDate(startDay);
        localDate = localDate.minusDays( 14 );
        return localDate.toDateTimeAtStartOfDay();
    }

    static public DateTime getEndOfADay(DateTime date) {
        Date day = date.toDate();
        final long oneDayInMillis = 24 * 60 * 60 * 1000;
        return new DateTime(new Date((day.getTime() / oneDayInMillis + 1) * oneDayInMillis - 1).getTime());
    }
}
