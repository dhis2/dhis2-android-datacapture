package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class FinancialYearJulyExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'July'";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays();
        FinancialYearJulyExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearJulyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 2;
        FinancialYearJulyExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearJulyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertTrue(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() + 1;
        FinancialYearJulyExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearJulyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusYears(1).withMonthOfYear(
                DateTimeConstants.JULY).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusYears(1), new LocalDate()).getDays() - 1;
        FinancialYearJulyExpiryDayValidator yearlyExpiryDayValidator =
                new FinancialYearJulyExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(yearlyExpiryDayValidator.canEdit());
    }
}
