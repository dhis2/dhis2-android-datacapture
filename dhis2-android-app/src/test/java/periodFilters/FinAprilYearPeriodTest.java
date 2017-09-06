package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.FinAprilYearPeriodFilter;
import org.junit.Test;

public class FinAprilYearPeriodTest {
    @Test
    public void testFinAprilPeriodsNullStartDate() {
        FinAprilYearPeriodFilter periodFilter = new FinAprilYearPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-04-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-05-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testFinAprilPeriodsNullEndDate() {
        FinAprilYearPeriodFilter periodFilter = new FinAprilYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-30"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

    }
    @Test
    public void testFinAprilPeriodsSameDayLimit() {
        FinAprilYearPeriodFilter periodFilter = new FinAprilYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-04-30"), PeriodFiltersCommon.getDateTimeFromString("2016-04-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }
    @Test
    public void testFinAprilPeriodsPeriodLimit() {
        FinAprilYearPeriodFilter periodFilter = new FinAprilYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-04-30"), PeriodFiltersCommon.getDateTimeFromString("2015-05-01"));
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
