package org.dhis2.mobile.utils.date;


import org.dhis2.mobile.ui.models.Filter;
import org.dhis2.mobile.utils.date.filters.BiMonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.MonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.QuarterlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.SixMonthlyAprilPeriodFilter;
import org.dhis2.mobile.utils.date.filters.SixMonthlyPeriodFilters;
import org.dhis2.mobile.utils.date.filters.WeeklyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklySaturdayPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklySundayPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklyThursdayPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklyWednesdayPeriodFilter;
import org.dhis2.mobile.utils.date.filters.YearlyPeriodFilter;
import org.joda.time.DateTime;

public class PeriodFilterFactory {
    public static final String YEARLY = "Yearly";
    public static final String FINANCIAL_APRIL = "FinancialApril";
    public static final String FINANCIAL_JULY = "FinancialJuly";
    public static final String FINANCIAL_OCT = "FinancialOct";
    public static final String SIX_MONTHLY = "SixMonthly";
    public static final String SIX_MONTHLY_APRIL = "SixMonthlyApril";
    public static final String QUARTERLY = "Quarterly";
    public static final String BIMONTHLY = "BiMonthly";
    public static final String MONTHLY = "Monthly";
    public static final String WEEKLY = "Weekly";
    public static final String WEEKLY_WEDNESDAY = "WeeklyWednesday";
    public static final String WEEKLY_THURSDAY = "WeeklyThursday";
    public static final String WEEKLY_SATURDAY = "WeeklySaturday";
    public static final String WEEKLY_SUNDAY = "WeeklySunday";

    public static final String DAILY = "Daily";

    private PeriodFilterFactory(){

    }
    public static Filter getPeriodFilter(DateTime startDate, DateTime endDate, String periodType){
        if(periodType.equals(YEARLY)){
            return new YearlyPeriodFilter(startDate, endDate);
        }else if(periodType.equals(WEEKLY)){
            return new WeeklyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(WEEKLY_WEDNESDAY)){
            return new WeeklyWednesdayPeriodFilter(startDate, endDate);
        } else if (periodType.equals(WEEKLY_THURSDAY)){
            return new WeeklyThursdayPeriodFilter(startDate, endDate);
        } else if (periodType.equals(WEEKLY_SATURDAY)){
            return new WeeklySaturdayPeriodFilter(startDate, endDate);
        } else if (periodType.equals(WEEKLY_SUNDAY)){
            return new WeeklySundayPeriodFilter(startDate, endDate);
        } else if (periodType.equals(MONTHLY)){
            return new MonthlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(BIMONTHLY)){
            return new BiMonthlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(QUARTERLY)){
            return new QuarterlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(SIX_MONTHLY)){
            return new SixMonthlyPeriodFilters(startDate, endDate);
        } else if (periodType.equals(SIX_MONTHLY_APRIL)){
            return new SixMonthlyAprilPeriodFilter(startDate, endDate);
        } else {
            return new PeriodFilter(startDate, endDate);
        }
    }
}
