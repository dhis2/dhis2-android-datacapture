package periodFilters;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
 
import org.dhis2.mobile.utils.date.filters.FinOctYearPeriodFilter;
import org.junit.Test;

public class FinOctYearPeriodTest {
    @Test
    public void testFinOctPeriodsNullStartDate() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                null, PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-10-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-09-30"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testFinOctPeriodsNullEndDate() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-01"), null);
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-09-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-10-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        assertFalse(periodFilter.apply());

    }
    @Test
    public void testFinOctPeriodsSameDayLimit() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-31"), PeriodFiltersCommon.getDateTimeFromString("2016-10-31"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-09-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-10-01"));
        assertTrue(periodFilter.apply());
        periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2016-10-01"), PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-09-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2017-10-01"));
        assertTrue(periodFilter.apply());

    }
    @Test
    public void testFinOctPeriodsPeriodLimit() {
        FinOctYearPeriodFilter periodFilter = new FinOctYearPeriodFilter(
                PeriodFiltersCommon.getDateTimeFromString("2015-10-31"), PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-09-30"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2015-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-09-30"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(PeriodFiltersCommon.getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());
    }
}
