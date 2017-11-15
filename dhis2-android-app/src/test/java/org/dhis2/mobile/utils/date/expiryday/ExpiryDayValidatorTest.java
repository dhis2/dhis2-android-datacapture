package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

public class ExpiryDayValidatorTest {
    private static final String PATTERN = "yyyyMMdd";

    @Test
    public void testCanNotEditWithPeriodWithSameMinusDaysAsExpiryDays() {
        LocalDate todayDate = new LocalDate();
        int expiryDays = 5;
        todayDate = todayDate.minusDays(expiryDays);
        ExpiryDayValidator expiryDayValidator = new ExpiryDayValidator(expiryDays,
                todayDate.toString(PATTERN));
        assertFalse(expiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditDateWithLessMinusDaysThanExpiryDays() {
        LocalDate todayDate = new LocalDate();
        int expiryDays = 5;
        todayDate = todayDate.minusDays(expiryDays - 1);
        ExpiryDayValidator expiryDayValidator = new ExpiryDayValidator(expiryDays,
                todayDate.toString(PATTERN));
        assertTrue(expiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditDateWithMorePlusDaysThanExpiryDays() {
        LocalDate todayDate = new LocalDate();
        int expiryDays = 5;
        todayDate = todayDate.minusDays(expiryDays + 1);
        ExpiryDayValidator expiryDayValidator = new ExpiryDayValidator(expiryDays,
                todayDate.toString(PATTERN));
        assertFalse(expiryDayValidator.canEdit());
    }

}
