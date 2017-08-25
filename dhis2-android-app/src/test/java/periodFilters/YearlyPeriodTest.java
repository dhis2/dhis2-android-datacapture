package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.YearlyPeriodFilter;
import org.junit.Test;

public class YearlyPeriodTest {

    @Test
    public void testYearlyPeriodsNullStartDate() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2015-12-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2014-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsNullEndDate() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-01-01"),null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsLastDaysOfYearLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-01-01"), PeriodFiltersCommon.getDateTimeFromString("2015-12-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsLastDaysOfMultipleYearsLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2014-12-31"), PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2013-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2014-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsSameDaysLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-01-15"), PeriodFiltersCommon.getDateTimeFromString("2015-01-15"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2014-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());
    }

}
