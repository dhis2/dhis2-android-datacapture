import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.dhis2.mobile.utils.date.DatePeriods;
import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatePeriodTest {

    public DateTime getDateTimeFromString(String dateAsString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateTime date = null;
        try {
            date =  new DateTime(format.parse(dateAsString).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Test
    public void testMonthlyPeriods() {
        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"), DatePeriods.MONTHLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());

        periodFilter = new PeriodFilter(getDateTimeFromString("2016-12-15"), getDateTimeFromString("2017-01-15"), DatePeriods.MONTHLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-11-01"));
        assertTrue(periodFilter.apply());

        periodFilter = new PeriodFilter(getDateTimeFromString("2016-12-1"), getDateTimeFromString("2017-01-31"), DatePeriods.MONTHLY);
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
    public void testBiMonthlyPeriods() {
        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"), DatePeriods.BIMONTHLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-01"));
        assertTrue(periodFilter.apply());

        periodFilter = new PeriodFilter(getDateTimeFromString("2016-12-15"), getDateTimeFromString("2017-01-15"), DatePeriods.MONTHLY);
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
    public void testQuaterlyPeriods() {

        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2016-12-15"), getDateTimeFromString("2017-01-15"), DatePeriods.QUARTERLY);
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

        periodFilter = new PeriodFilter(getDateTimeFromString("2016-05-15"), getDateTimeFromString("2016-09-15"), DatePeriods.QUARTERLY);
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

        periodFilter = new PeriodFilter(getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-06"), DatePeriods.QUARTERLY);
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
    public void testWeeklyPeriods() {
        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2017-01-06"), getDateTimeFromString("2017-01-07"), DatePeriods.WEEKLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-02"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-02-09"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-12-26"));
        assertTrue(periodFilter.apply());

        periodFilter = new PeriodFilter(getDateTimeFromString("2016-12-26"), getDateTimeFromString("2017-01-09"), DatePeriods.WEEKLY);
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
    public void testYearlyPeriods() {
        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2015-01-01"), getDateTimeFromString("2015-12-31"), DatePeriods.YEARLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2015-01-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());
        periodFilter = new PeriodFilter(getDateTimeFromString("2014-12-31"), getDateTimeFromString("2016-01-01"), DatePeriods.YEARLY);
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
    public void testSixMontlyPeriods() {
        PeriodFilter periodFilter = new PeriodFilter(getDateTimeFromString("2016-06-30"), getDateTimeFromString("2016-06-30"), DatePeriods.SIX_MONTHLY);
        periodFilter.setSelectedDate(getDateTimeFromString("2016-07-01"));
        assertFalse(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2016-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter.setSelectedDate(getDateTimeFromString("2017-01-01"));
        assertTrue(periodFilter.apply());

        periodFilter = new PeriodFilter(getDateTimeFromString("2015-01-01"), getDateTimeFromString("2015-07-01"), DatePeriods.SIX_MONTHLY);
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
