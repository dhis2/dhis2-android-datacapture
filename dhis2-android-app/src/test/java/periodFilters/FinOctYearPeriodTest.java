package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
 
import org.dhis2.mobile.utils.date.filters.FinOctYearPeriodFilter;
import org.junit.Test;

public class FinOctYearPeriodTest {
    @Test
    public void testFinOctPeriodsNullStartDate() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-10-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2014-10-30"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testFinOctPeriodsNullEndDate() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-30"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

    }
    @Test
    public void testFinOctPeriodsSameDayLimit() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-30"), PeriodFiltersCommon.getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-11-01"));
        assertTrue(periodFilter.apply());

    }
    @Test
    public void testFinOctPeriodsPeriodLimit() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-10-01"), PeriodFiltersCommon.getDateTimeFromString("2015-07-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }
}
