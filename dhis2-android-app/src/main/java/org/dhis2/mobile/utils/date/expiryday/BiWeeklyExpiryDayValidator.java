package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class BiWeeklyExpiryDayValidator extends ExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'BiW'ww";

    public BiWeeklyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected LocalDate getMaxDateCanEdit() {
        String periodFixed = period.replace("Bi","");
        int weeks = Integer.parseInt(periodFixed.substring(periodFixed.lastIndexOf("W")+1));
        int year = Integer.parseInt(periodFixed.substring(0, periodFixed.lastIndexOf("W")));
        int count =0;
        LocalDate checkDate = new LocalDate( new LocalDate().withYear(year).withWeekOfWeekyear(1).withDayOfWeek(1));
        while(count<weeks){
            checkDate = checkDate.plusWeeks(2);
            count++;
        }
        return checkDate.minusDays(1).plusDays(expiryDays - 1);
    }

    protected int weekStarts() {
        return DateTimeConstants.MONDAY;
    }

    protected String getDateFormat() {
        return DATE_FORMAT;
    }
}
