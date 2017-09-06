package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.FinJulyYearPeriodFilter;
import org.junit.Test;

public class FinJulyYearPeriodTest {
    @Test
    public void testFinJulyPeriodsNullStartDate() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testFinJulyPeriodsNullEndDate() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-06-30"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

    }
    @Test
    public void testFinJulyPeriodsSameDayLimit() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-06-30"), PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }
    @Test
    public void testFinJulyPeriodsPeriodLimit() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2014-06-30"), PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }
}
