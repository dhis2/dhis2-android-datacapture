package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class YearlyExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JANUARY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays();
        YearlyExpiryDayValidator yearlyExpiryDayValidator = new YearlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JANUARY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 2;
        YearlyExpiryDayValidator yearlyExpiryDayValidator = new YearlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertTrue(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JANUARY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 1;
        YearlyExpiryDayValidator yearlyExpiryDayValidator = new YearlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JANUARY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() - 1;
        YearlyExpiryDayValidator yearlyExpiryDayValidator = new YearlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }
}
