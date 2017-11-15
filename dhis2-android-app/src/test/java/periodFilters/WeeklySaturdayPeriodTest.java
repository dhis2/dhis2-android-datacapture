package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklySaturdayPeriodFilter;
import org.junit.Test;

public class WeeklySaturdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-14"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-13"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-30"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-07"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-15"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-07"), PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-13"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-14"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-31"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyLastDayLimit() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-11-10"),
                PeriodFiltersCommon.getDateTimeFromString("2017-11-10"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-04"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-11"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-03"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklySaturdayPeriodFilter periodFilter = new WeeklySaturdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-31"), PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-07"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-31"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-30"));
        assertTrue(periodFilter.apply());
    }

}
