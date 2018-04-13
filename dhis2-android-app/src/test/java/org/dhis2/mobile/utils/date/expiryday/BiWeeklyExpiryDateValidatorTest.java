package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class BiWeeklyExpiryDateValidatorTest {
    private static final String PATTERN = "yyyy'BiW'ww";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(2).withDayOfWeek(DateTimeConstants.MONDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(13), new LocalDate()).getDays();
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusExpiryDaysPlusOne() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(2).withDayOfWeek(DateTimeConstants.MONDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(13), new LocalDate()).getDays() + 1;
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(expiryDays,
                periodDate.toString(PATTERN));
        assertTrue(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        periodDate = periodDate.minusWeeks(2).withDayOfWeek(DateTimeConstants.MONDAY);
        int expiryDays = Days.daysBetween(periodDate.plusDays(13), new LocalDate()).getDays() - 1;
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(expiryDays,
                periodDate.toString(PATTERN));
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }
}
