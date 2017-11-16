package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class FinancialYearAprilExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'April'";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.APRIL).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays();
        FinancialYearAprilExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.APRIL).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 2;
        FinancialYearAprilExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertTrue(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDaysMinusOne() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.APRIL).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 1;
        FinancialYearAprilExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.APRIL).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() - 1;
        FinancialYearAprilExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }
}
