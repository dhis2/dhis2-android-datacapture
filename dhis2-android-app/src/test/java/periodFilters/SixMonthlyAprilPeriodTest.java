package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.SixMonthlyAprilPeriodFilter;
import org.junit.Test;

public class SixMonthlyAprilPeriodTest {

    @Test
    public void testSixMonthlyPeriodsNullStartDate() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-04-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-05-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsNullEndDate() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-30"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-09-01"));
        assertFalse(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsSameDayLimit() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-30"), PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsPeriodLimit() {
        SixMonthlyAprilPeriodFilter periodFilter = new SixMonthlyAprilPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-05-01"), PeriodFiltersCommon.getDateTimeFromString("2015-09-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-10-01"));
        assertTrue(periodFilter.apply());
    }

}
