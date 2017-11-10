package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklyWednesdayPeriodFilter;
import org.junit.Test;

public class WeeklyWednesdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklyWednesdayPeriodFilter periodFilter = new WeeklyWednesdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklyWednesdayPeriodFilter periodFilter = new WeeklyWednesdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-04"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklyWednesdayPeriodFilter periodFilter = new WeeklyWednesdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-04"), PeriodFiltersCommon.getDateTimeFromString("2017-01-11"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklyWednesdayPeriodFilter periodFilter = new WeeklyWednesdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-29"), PeriodFiltersCommon.getDateTimeFromString("2017-01-11"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-19"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-29"));
        assertFalse(periodFilter.apply());
    }

}
