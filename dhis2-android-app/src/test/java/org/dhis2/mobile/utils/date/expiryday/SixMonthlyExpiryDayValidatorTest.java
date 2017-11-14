package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class SixMonthlyExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'S'";
    private static final int PREVIOUS_PERIOD_START_JANUARY = 1;
    private static final int PREVIOUS_PERIOD_START_JULY = 2;

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_JANUARY ? DateTimeConstants.JANUARY
                        : DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays();
        SixMonthlyExpiryDayValidator monthlyExpiryDayValidator = new SixMonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_JANUARY ? DateTimeConstants.JANUARY
                        : DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() + 2;
        SixMonthlyExpiryDayValidator monthlyExpiryDayValidator = new SixMonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN) + previousPeriodStart);
        assertTrue(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_JANUARY ? DateTimeConstants.JANUARY
                        : DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() + 1;
        SixMonthlyExpiryDayValidator monthlyExpiryDayValidator = new SixMonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_JANUARY ? DateTimeConstants.JANUARY
                        : DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() - 1;
        SixMonthlyExpiryDayValidator monthlyExpiryDayValidator = new SixMonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    private int getPreviousPeriodStart() {
        return new LocalDate().getMonthOfYear() >= DateTimeConstants.JULY
                ? PREVIOUS_PERIOD_START_JULY : PREVIOUS_PERIOD_START_JANUARY;
    }

}
