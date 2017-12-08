package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklySundayPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklyThursdayPeriodFilter;
import org.junit.Test;

public class WeeklyThursdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-05"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-29"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyLastDayLimit() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-11-08"),
                PeriodFiltersCommon.getDateTimeFromString("2017-11-08"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-08"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-02"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-05"), PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-12"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-29"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-29"), PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-28"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-29"));
        assertFalse(periodFilter.apply());
    }

}
