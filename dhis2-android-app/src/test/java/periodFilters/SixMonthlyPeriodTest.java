package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.SixMonthlyPeriodFilter;
import org.junit.Test;

public class SixMonthlyPeriodTest {

    @Test
    public void testSixMonthlyPeriodsNullStartDate() {
        SixMonthlyPeriodFilter periodFilter = new SixMonthlyPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsNullEndDate() {
        SixMonthlyPeriodFilter periodFilter = new SixMonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-06-30"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsSameDayLimit() {
        SixMonthlyPeriodFilter periodFilter = new SixMonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-06-30"), PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsPeriodLimit() {
        SixMonthlyPeriodFilter periodFilter = new SixMonthlyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-01-01"), PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

}
