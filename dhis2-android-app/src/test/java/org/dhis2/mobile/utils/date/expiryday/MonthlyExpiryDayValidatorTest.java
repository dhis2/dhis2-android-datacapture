package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class MonthlyExpiryDayValidatorTest {
    private static final String PATTERN = "yyyyMM";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusMonths(1).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(1), new LocalDate()).getDays();
        MonthlyExpiryDayValidator monthlyExpiryDayValidator = new MonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusDifferencePlusTwoExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusMonths(1).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(1), new LocalDate()).getDays() + 2;
        MonthlyExpiryDayValidator monthlyExpiryDayValidator = new MonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertTrue(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusMonths(1).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(1), new LocalDate()).getDays() + 1;
        MonthlyExpiryDayValidator monthlyExpiryDayValidator = new MonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusMonths(1).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(1), new LocalDate()).getDays() - 1;
        MonthlyExpiryDayValidator monthlyExpiryDayValidator = new MonthlyExpiryDayValidator(
                expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }
}
