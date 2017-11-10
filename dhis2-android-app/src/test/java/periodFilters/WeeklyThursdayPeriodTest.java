package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklyThursdayPeriodFilter;
import org.junit.Test;

public class WeeklyThursdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-05"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2017-01-05"), PeriodFiltersCommon.getDateTimeFromString("2017-01-11"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-05"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklyThursdayPeriodFilter periodFilter = new WeeklyThursdayPeriodFilter(
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
