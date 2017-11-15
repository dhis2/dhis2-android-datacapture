package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class WeeklyWednesdayExpiryDayValidator extends WeeklyExpiryDayValidator {
    public WeeklyWednesdayExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int weekStarts() {
        return DateTimeConstants.WEDNESDAY;
    }
}
