package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.SixMonthlyAprilPeriodFilter;
import org.junit.Test;

public class SixMonthlyAprilPeriodTest {

    @Test
    public void testSixMonthlyPeriodsNullStartDate() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-04-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-05-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsNullEndDate() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-01"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-04-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-04-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2018-04-01"));
        assertFalse(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsSameDayLimit() {

        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-01"), PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-31"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-09-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-04-01"));
        assertTrue(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-03-31"));
        assertFalse(periodFilter.apply());

        periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-30"), PeriodFiltersCommon.getDateTimeFromString("2016-04-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
        periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-03-31"), PeriodFiltersCommon.getDateTimeFromString("2016-03-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-03-31"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-04-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsPeriodLimit() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-04-01"), PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2014-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-03-30"));
        assertTrue(periodFilter.apply());
    }

}
