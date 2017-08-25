package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.BiMonthlyPeriodFilter;
import org.junit.Test;

public class BiMonthlyPeriodTest {

    @Test
    public void testBiMonthlyPeriodsNullStartDate() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsNullEndDate() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsSameDayLimits() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-15"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsLastDaysOfYearLimits() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-15"), PeriodFiltersCommon.getDateTimeFromString("2017-01-15"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-03-01"));
        assertTrue(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());
    }

}
