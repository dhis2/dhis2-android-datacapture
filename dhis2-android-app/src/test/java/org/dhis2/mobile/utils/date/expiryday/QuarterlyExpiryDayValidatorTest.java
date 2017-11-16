package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class QuarterlyExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'Q'";
    private static final int PREVIOUS_PERIOD_START_JANUARY = 1;
    private static final int PREVIOUS_PERIOD_START_APRIL = 2;
    private static final int PREVIOUS_PERIOD_START_JULY = 3;
    private static final int PREVIOUS_PERIOD_START_OCTOBER = 4;

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(3), new LocalDate()).getDays();
        QuarterlyExpiryDayValidator monthlyExpiryDayValidator =
                new QuarterlyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(3), new LocalDate()).getDays() + 2;
        QuarterlyExpiryDayValidator monthlyExpiryDayValidator =
                new QuarterlyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertTrue(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(3), new LocalDate()).getDays() + 1;
        QuarterlyExpiryDayValidator monthlyExpiryDayValidator =
                new QuarterlyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusExpiryDaysMinusOne() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(monthStart(previousPeriodStart)).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(3), new LocalDate()).getDays() - 1;
        QuarterlyExpiryDayValidator monthlyExpiryDayValidator =
                new QuarterlyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    private int getPreviousPeriodStart() {
        int nowMonthOfYear = new LocalDate().getMonthOfYear();
        if (nowMonthOfYear >= DateTimeConstants.OCTOBER) {
            return PREVIOUS_PERIOD_START_OCTOBER;
        } else if (nowMonthOfYear >= DateTimeConstants.JULY) {
            return PREVIOUS_PERIOD_START_JULY;
        } else if (nowMonthOfYear >= DateTimeConstants.APRIL) {
            return PREVIOUS_PERIOD_START_APRIL;
        } else {
            return PREVIOUS_PERIOD_START_JANUARY;
        }
    }

    private int monthStart(int previousPeriod) {
        switch (previousPeriod) {
            case PREVIOUS_PERIOD_START_JANUARY:
                return DateTimeConstants.JANUARY;
            case PREVIOUS_PERIOD_START_APRIL:
                return DateTimeConstants.APRIL;
            case PREVIOUS_PERIOD_START_JULY:
                return DateTimeConstants.JULY;
            case PREVIOUS_PERIOD_START_OCTOBER:
                return DateTimeConstants.OCTOBER;
            default:
                return DateTimeConstants.JANUARY;
        }
    }
}
