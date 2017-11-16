package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class WeeklySundayExpiryDayValidator extends WeeklyExpiryDayValidator {

    protected static final String DATE_FORMAT = "yyyy'SunW'ww";

    public WeeklySundayExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int weekStarts() {
        return DateTimeConstants.SUNDAY;
    }

    @Override
    protected String getDateFormat() {
        return DATE_FORMAT;
    }
}
