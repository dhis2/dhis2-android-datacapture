package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklySundayPeriodFilter;
import org.junit.Test;

public class WeeklySundayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklySundayPeriodFilter periodFilter = new WeeklySundayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklySundayPeriodFilter periodFilter = new WeeklySundayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-01"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklySundayPeriodFilter periodFilter = new WeeklySundayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-01"), PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklySundayPeriodFilter periodFilter = new WeeklySundayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-25"), PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-19"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

}
