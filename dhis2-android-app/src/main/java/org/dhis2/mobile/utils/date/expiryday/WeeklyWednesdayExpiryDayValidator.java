package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class WeeklyWednesdayExpiryDayValidator extends WeeklyExpiryDayValidator {

    protected static final String DATE_FORMAT = "yyyy'WedW'ww";

    public WeeklyWednesdayExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int weekStarts() {
        return DateTimeConstants.WEDNESDAY;
    }

    @Override
    protected String getDateFormat() {
        return DATE_FORMAT;
    }
}
