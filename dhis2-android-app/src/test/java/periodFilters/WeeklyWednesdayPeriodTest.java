package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.filters.WeeklyWednesdayPeriodFilter;
import org.junit.Test;

public class WeeklyWednesdayPeriodTest {

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklyWednesdayPeriodFilter
                periodFilter = new WeeklyWednesdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-01-03"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-06"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-28"));
        assertFalse(periodFilter.apply());

        periodFilter = new WeeklyWednesdayPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2017-08-02"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-08-02"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-08-08"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-08-09"));
        assertTrue(periodFilter.apply());
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-08-15"));
        assertTrue(periodFilter.apply());
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
                PeriodFiltersCommon.getDateTimeFromString("2017-01-04"), PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-11"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklyWednesdayPeriodFilter periodFilter = new WeeklyWednesdayPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-12-28"), PeriodFiltersCommon.getDateTimeFromString("2017-01-03"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-03"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-04"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-27"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-12-29"));
        assertFalse(periodFilter.apply());
    }

}
