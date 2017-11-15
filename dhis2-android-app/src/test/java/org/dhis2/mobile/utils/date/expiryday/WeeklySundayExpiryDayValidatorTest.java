package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class WeeklySundayExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'W'ww";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(6), new LocalDate()).getDays();
        WeeklySundayExpiryDayValidator weeklyExpiryDayValidator =
                new WeeklySundayExpiryDayValidator(expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusExpiryDaysPlusOne() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(6), new LocalDate()).getDays() + 1;
        WeeklySundayExpiryDayValidator weeklyExpiryDayValidator =
                new WeeklySundayExpiryDayValidator(expiryDays,
                        periodDate.toString(PATTERN));
        assertTrue(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(6), new LocalDate()).getDays() - 1;
        WeeklySundayExpiryDayValidator weeklyExpiryDayValidator =
                new WeeklySundayExpiryDayValidator(expiryDays,
                        periodDate.toString(PATTERN));
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }
}
