package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.FinJulyYearPeriodFilter;
import org.dhis2.mobile.utils.date.filters.FinOctYearPeriodFilter;
import org.junit.Test;

public class FinJulyYearPeriodTest {
    @Test
    public void testFinJulyPeriodsNullStartDate() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-08-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-08-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-07-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-06-30"));
        assertFalse(periodFilter.apply());

    }

    @Test
    public void testFinJulyPeriodsNullEndDate() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-07-01"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-06-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

    }
    @Test
    public void testFinJulyPeriodsSameDayLimit() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-07-31"), PeriodFiltersCommon.getDateTimeFromString("2016-07-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-06-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-07-01"));
        assertTrue(periodFilter.apply());
        periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-07-01"), PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-06-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-07-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testFinJulyPeriodsPeriodLimit() {
        FinJulyYearPeriodFilter periodFilter = new FinJulyYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-07-31"), PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-06-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-07-01"));
        assertTrue(periodFilter.apply());
    }
}
