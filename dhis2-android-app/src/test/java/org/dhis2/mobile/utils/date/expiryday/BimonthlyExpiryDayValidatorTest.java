package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class BimonthlyExpiryDayValidatorTest {

    private static final String PATTERN = "yyyy'%sB'";
    private static final String PREVIOUS_PERIOD_START_JANUARY = "01";
    private static final String PREVIOUS_PERIOD_START_MARCH = "02";
    private static final String PREVIOUS_PERIOD_START_MAY = "03";
    private static final String PREVIOUS_PERIOD_START_JULY = "04";
    private static final String PREVIOUS_PERIOD_START_SEPTEMBER = "05";
    private static final String PREVIOUS_PERIOD_START_NOVEMBER = "06";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        String previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(2), new LocalDate()).getDays();
        BimonthlyExpiryDayValidator monthlyExpiryDayValidator = new BimonthlyExpiryDayValidator(
                expiryDays,
                String.format(periodDate.toString(PATTERN), previousPeriodStart));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        String previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(2), new LocalDate()).getDays() + 2;
        BimonthlyExpiryDayValidator monthlyExpiryDayValidator = new BimonthlyExpiryDayValidator(
                expiryDays,
                String.format(periodDate.toString(PATTERN), previousPeriodStart));
        assertTrue(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        String previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(2), new LocalDate()).getDays() + 1;
        BimonthlyExpiryDayValidator monthlyExpiryDayValidator = new BimonthlyExpiryDayValidator(
                expiryDays,
                String.format(periodDate.toString(PATTERN), previousPeriodStart));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        String previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(2), new LocalDate()).getDays() - 1;
        BimonthlyExpiryDayValidator monthlyExpiryDayValidator = new BimonthlyExpiryDayValidator(
                expiryDays,
                String.format(periodDate.toString(PATTERN), previousPeriodStart));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    private String getPreviousPeriodStart() {
        int nowMonthOfYear = new LocalDate().getMonthOfYear();
        if (nowMonthOfYear >= DateTimeConstants.NOVEMBER) {
            return PREVIOUS_PERIOD_START_NOVEMBER;
        } else if (nowMonthOfYear >= DateTimeConstants.SEPTEMBER) {
            return PREVIOUS_PERIOD_START_SEPTEMBER;
        } else if (nowMonthOfYear >= DateTimeConstants.JULY) {
            return PREVIOUS_PERIOD_START_JULY;
        } else if (nowMonthOfYear >= DateTimeConstants.MAY) {
            return PREVIOUS_PERIOD_START_MAY;
        } else if (nowMonthOfYear >= DateTimeConstants.MARCH) {
            return PREVIOUS_PERIOD_START_MARCH;
        } else {
            return PREVIOUS_PERIOD_START_JANUARY;
        }
    }

    private int monthStart(String previousPeriod) {
        switch (previousPeriod) {
            case PREVIOUS_PERIOD_START_JANUARY:
                return DateTimeConstants.JANUARY;
            case PREVIOUS_PERIOD_START_MARCH:
                return DateTimeConstants.MARCH;
            case PREVIOUS_PERIOD_START_MAY:
                return DateTimeConstants.MAY;
            case PREVIOUS_PERIOD_START_JULY:
                return DateTimeConstants.JULY;
            case PREVIOUS_PERIOD_START_SEPTEMBER:
                return DateTimeConstants.SEPTEMBER;
            case PREVIOUS_PERIOD_START_NOVEMBER:
                return DateTimeConstants.NOVEMBER;
            default:
                return DateTimeConstants.JANUARY;
        }
    }

}
