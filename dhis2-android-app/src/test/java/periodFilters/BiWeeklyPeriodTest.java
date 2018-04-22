package periodFilters;


import org.dhis2.mobile.utils.date.filters.BiWeeklyPeriodFilter;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class BiWeeklyPeriodTest {

    @Test
    public void testBiWeeklyPeriodsNullStartDate() {
        BiWeeklyPeriodFilter periodFilter = new BiWeeklyPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-26"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-12-26"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-15"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiWeeklyPeriodsNullEndDate() {
        BiWeeklyPeriodFilter periodFilter = new BiWeeklyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-06"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2018-02-09"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiWeeklyPeriodsSameDayLimits() {
        BiWeeklyPeriodFilter periodFilter = new BiWeeklyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-06"), PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-15"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testBiWeeklyPeriodsLastDaysOfYearLimits() {
        BiWeeklyPeriodFilter periodFilter = new BiWeeklyPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-26"), PeriodFiltersCommon.getDateTimeFromString("2017-01-08"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-15"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-18"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-18"));
        assertTrue(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-19"));
        assertFalse(periodFilter.apply());
    }

}
