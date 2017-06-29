import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.BiMonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.MonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.QuarterlyPeriodFilter;
import org.dhis2.mobile.utils.date.SixmonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.WeeklyPeriodFilter;
import org.dhis2.mobile.utils.date.iterators.YearlyPeriodFilter;
import org.joda.time.DateTime;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PeriodFiltersTest {

    public DateTime getDateTimeFromString(String dateAsString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateTime date = null;
        try {
            date = new DateTime(format.parse(dateAsString).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Test
    public void testMonthlyPeriodsNullStartDate() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                null,  getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsNullEndDate() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                getDateTimeFromString("2017-01-06"), null);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsSameDayLimits() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testMonthlyPeriodsLastDaysOfTheYearLimits() {
        MonthlyPeriodFilter periodFilter = new MonthlyPeriodFilter(
                getDateTimeFromString("2016-12-31"), getDateTimeFromString("2017-01-31"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsNullStartDate() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                null, getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsNullEndDate() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                null, getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsSameDayLimits() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                getDateTimeFromString("2016-12-15"), null);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testBiMonthlyPeriodsLastDaysOfYearLimits() {
        BiMonthlyPeriodFilter periodFilter = new BiMonthlyPeriodFilter(
                getDateTimeFromString("2016-12-15"), getDateTimeFromString("2017-01-15"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertTrue(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());
        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testQuaterlyPeriodsNullStartDate() {
        QuarterlyPeriodFilter periodFilter = new QuarterlyPeriodFilter(
                null, getDateTimeFromString("2017-01-15"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-09-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testQuaterlyPeriodsNullEndDate() {
        QuarterlyPeriodFilter periodFilter = new QuarterlyPeriodFilter(
                getDateTimeFromString("2016-12-15"), null);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-04-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-09-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testQuaterlyPeriodsLastDaysOfYearLimits() {
        QuarterlyPeriodFilter periodFilter = new QuarterlyPeriodFilter(
                getDateTimeFromString("2016-12-15"), getDateTimeFromString("2017-01-15"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-09-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testQuaterlyPeriodsQuaterlyLimits() {
        QuarterlyPeriodFilter periodFilter = new QuarterlyPeriodFilter(
                getDateTimeFromString("2016-05-15"), getDateTimeFromString("2016-09-15"));
        periodFilter.setSelectedDate(getDateTimeFromString("2016-06-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-08-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-09-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-10-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-05-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-04-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-03-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testQuaterlyPeriodsQuaterlySameDayLimits() {
        QuarterlyPeriodFilter periodFilter = new QuarterlyPeriodFilter(
                getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-03-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-04-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullStartDate() {
        WeeklyPeriodFilter periodFilter = new WeeklyPeriodFilter(
                null, getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsNullEndDate() {
        WeeklyPeriodFilter periodFilter = new WeeklyPeriodFilter(
                getDateTimeFromString("2017-01-06"), null);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsSameDayLimits() {
        WeeklyPeriodFilter periodFilter = new WeeklyPeriodFilter(
                getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testWeeklyPeriodsLastDaysOfYearLimits() {
        WeeklyPeriodFilter periodFilter = new WeeklyPeriodFilter(
                getDateTimeFromString("2016-12-26"), getDateTimeFromString("2017-01-09"));
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-09"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-16"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-19"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-26"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsNullStartDate() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                null, getDateTimeFromString("2015-12-31"));
        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2014-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsNullEndDate() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                getDateTimeFromString("2015-01-01"),null);
        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsLastDaysOfYearLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                getDateTimeFromString("2015-01-01"), getDateTimeFromString("2015-12-31"));
        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsLastDaysOfMultipleYearsLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                getDateTimeFromString("2014-12-31"), getDateTimeFromString("2016-01-01"));
        periodFilter.setSelectedDate(getDateTimeFromString("2013-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2014-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testYearlyPeriodsSameDaysLimits() {
        YearlyPeriodFilter periodFilter = new YearlyPeriodFilter(
                getDateTimeFromString("2015-01-15"), getDateTimeFromString("2015-01-15"));
        periodFilter.setSelectedDate(getDateTimeFromString("2014-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());
    }

    @Test
    public void testSixMonthlyPeriodsNullStartDate() {
        SixmonthlyPeriodFilter periodFilter = new SixmonthlyPeriodFilter(
                null, getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(getDateTimeFromString("2016-07-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsNullEndDate() {
        SixmonthlyPeriodFilter periodFilter = new SixmonthlyPeriodFilter(
                getDateTimeFromString("2016-06-30"), null);
        periodFilter.setSelectedDate(getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsSameDayLimit() {
        SixmonthlyPeriodFilter periodFilter = new SixmonthlyPeriodFilter(
                getDateTimeFromString("2016-06-30"), getDateTimeFromString("2016-06-30"));
        periodFilter.setSelectedDate(getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

    }

    @Test
    public void testSixMonthlyPeriodsPeriodLimit() {
        SixmonthlyPeriodFilter periodFilter = new SixmonthlyPeriodFilter(
                getDateTimeFromString("2015-01-01"), getDateTimeFromString("2015-07-01"));
        periodFilter.setSelectedDate(getDateTimeFromString("2015-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
    }
}
