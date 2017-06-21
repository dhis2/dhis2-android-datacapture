package org.dhis2.mobile.utils.date;


import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class DatePeriods {
    public static final String YEARLY = "Yearly";

    public static final String FINANCIAL_APRIL = "FinancialApril";
    public static final String FINANCIAL_JULY = "FinancialJuly";
    public static final String FINANCIAL_OCT = "FinancialOct";

    public static final String SIX_MONTHLY = "SixMonthly";
    public static final String QUARTERLY = "Quarterly";
    public static final String BIMONTHLY = "BiMonthly";

    public static final String MONTHLY = "Monthly";
    public static final String WEEKLY = "Weekly";
    public static final String DAILY = "Daily";


    @NonNull
    public static DateTime fixStartDate(DateTime startDate, String periodType) {
        if(startDate==null){
            return null;
        }
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate.toDate());
        switch (periodType) {
            case DAILY:
                //Not necessary
                break;
            case YEARLY:
                startDateCalendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            case WEEKLY:
                startDateCalendar.setTime(new LocalDate(startDate.withDayOfWeek(1)).toDate());
                break;
            case MONTHLY:
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case BIMONTHLY:
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int month = startDateCalendar.get(Calendar.MONTH);
                if (month < 2) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                } else if (month < 4) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.MARCH);
                } else if (month < 6) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.MAY);
                } else if (month < 8) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                } else if (month < 10) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                } else if (month < 12) {
                    startDateCalendar.add(Calendar.MONTH, Calendar.NOVEMBER);
                }
                break;
            case QUARTERLY:
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                month = startDateCalendar.get(Calendar.MONTH);
                if (month < 3) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                } else if (month < 6) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.APRIL);
                } else if (month < 9) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                } else if (month < 12) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
                }
                break;
            case SIX_MONTHLY:
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                month = startDateCalendar.get(Calendar.MONTH);
                if (month < 6) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                } else if (month < 12) {
                    startDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                }
                startDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case FINANCIAL_OCT:
            case FINANCIAL_JULY:
            case FINANCIAL_APRIL:
                //// TODO: 21/06/2017
                break;
        }
        startDate = new DateTime(startDateCalendar.getTime());
        return startDate;
    }

    @NonNull
    public static DateTime fixEndDate(DateTime endDate, String periodType) {
        if(endDate==null){
            return null;
        }
        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate.toDate());
        switch (periodType) {
            case DAILY:
                //Not necessary
                break;
            case YEARLY:
                endDateCalendar.add(Calendar.YEAR, 1);
                endDateCalendar.set(Calendar.DAY_OF_YEAR, 1);
                endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case WEEKLY:
                LocalDate fixedWeek = new LocalDate(endDate.withDayOfWeek(7));
                endDateCalendar.setTime(fixedWeek.toDate());
                break;
            case MONTHLY:
                endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                endDateCalendar.add(Calendar.MONTH, 1);
                endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case BIMONTHLY:
                endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int month = endDateCalendar.get(Calendar.MONTH);
                if (month < 2) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.MARCH);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 4) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.MAY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 6) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                }  else if (month < 8) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                }  else if (month < 10) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 12) {
                    endDateCalendar.add(Calendar.YEAR, 1);
                    endDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                }
                break;
            case QUARTERLY:
                endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                month = endDateCalendar.get(Calendar.MONTH);
                if (month < 3) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.APRIL);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 6) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 9) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 12) {
                    endDateCalendar.add(Calendar.YEAR, 1);
                    endDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                }
                break;
            case SIX_MONTHLY:
                endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                month = endDateCalendar.get(Calendar.MONTH);
                if (month < 6) {
                    endDateCalendar.set(Calendar.MONTH, Calendar.JULY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                } else if (month < 12) {
                    endDateCalendar.add(Calendar.YEAR, 1);
                    endDateCalendar.set(Calendar.MONTH, Calendar.JANUARY);
                    endDateCalendar.add(Calendar.DAY_OF_YEAR, -1);
                }
                endDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case FINANCIAL_OCT:
            case FINANCIAL_JULY:
            case FINANCIAL_APRIL:
                //// TODO: 21/06/2017
                break;
        }
        endDate = new DateTime(endDateCalendar.getTime());

        return endDate;
    }
}
