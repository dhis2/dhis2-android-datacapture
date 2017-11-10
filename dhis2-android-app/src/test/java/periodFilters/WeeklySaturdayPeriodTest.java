package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklySaturdayPeriodFilter;
import org.junit.Test;

public class WeeklySaturdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-07"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-14"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-07"), PeriodFiltersCommon.getDateTimeFromString("2017-01-14"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-01"), PeriodFiltersCommon.getDateTimeFromString("2017-01-14"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-19"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
    }

}
