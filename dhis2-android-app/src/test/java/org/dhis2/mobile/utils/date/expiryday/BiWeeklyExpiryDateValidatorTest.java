package org.dhis2.mobile.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class BiWeeklyExpiryDateValidatorTest {
    //This dhis pattern not work with LocalDate
    private static final String PATTERN = "yyyy'BiW'ww";

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(getExpiryDays(0),
                getPreviousPeriod());
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditPreviousPeriodEndsSameTodayMinusExpiryDaysPlusOne() {
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(getExpiryDays(1),
                getPreviousPeriod());
        assertTrue(weeklyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        BiWeeklyExpiryDayValidator weeklyExpiryDayValidator = new BiWeeklyExpiryDayValidator(getExpiryDays(-1),
                getPreviousPeriod());
        assertFalse(weeklyExpiryDayValidator.canEdit());
    }
    //get the expiry days from the end of the period of the current period
    private int getExpiryDays(int expiryDay){
        LocalDate periodDate = new LocalDate();
        LocalDate periodInitRange = new LocalDate();
        periodInitRange = periodInitRange.withWeekOfWeekyear(1);
        do{
            periodInitRange = periodInitRange.plusWeeks(2).withDayOfWeek(DateTimeConstants.MONDAY);
        } while (periodInitRange.isBefore(periodDate));
        periodInitRange = periodInitRange.minusWeeks(2);
        return Days.daysBetween(periodInitRange.minusDays(1), new LocalDate()).getDays() + expiryDay;
    }

    //get the previous period starting from the actual period
    private String getPreviousPeriod() {
        LocalDate periodDate = new LocalDate();
        LocalDate periodInitRange = new LocalDate();
        periodInitRange = periodInitRange.withWeekOfWeekyear(1);
        int count=0;
        do{
            count++;
            periodInitRange = periodInitRange.plusWeeks(2).withDayOfWeek(DateTimeConstants.MONDAY);
        } while (periodInitRange.isBefore(periodDate));
        return periodDate.getYear()+"BiW"+(count-1);
    }
}
