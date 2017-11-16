package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class WeeklyThursdayExpiryDayValidator extends WeeklyExpiryDayValidator {
    public WeeklyThursdayExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int weekStarts() {
        return DateTimeConstants.THURSDAY;
    }
}
