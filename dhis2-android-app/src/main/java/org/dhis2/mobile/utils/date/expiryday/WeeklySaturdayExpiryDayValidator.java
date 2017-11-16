package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;


public class WeeklySaturdayExpiryDayValidator extends WeeklyExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'SatW'ww";

    public WeeklySaturdayExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int weekStarts() {
        return DateTimeConstants.SATURDAY;
    }

    @Override
    protected String getDateFormat() {
        return DATE_FORMAT;
    }
}
