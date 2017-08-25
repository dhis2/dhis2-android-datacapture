package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.MonthlyPeriodFilter;
import org.junit.Test;

public class MonthlyPeriodTest {

    @Test
    public void testMonthlyPeriodsNullStartDate() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                null,  PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsNullEndDate() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-06"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsSameDayLimits() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-06"), PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsLastDaysOfTheYearLimits() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-31"), PeriodFiltersCommon.getDateTimeFromString("2017-01-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertTrue(periodFilter.apply());
    }

}
