package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class WeeklyExpiryDayValidator extends ExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'W'ww";

    public WeeklyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected LocalDate getMaxDateCanEdit() {
        LocalDate periodDate = LocalDate.parse(period, DateTimeFormat.forPattern(getDateFormat()));
        periodDate = periodDate.withDayOfWeek(weekStarts());
        periodDate = periodDate.plusDays(6);
        return periodDate.plusDays(expiryDays - 1);
    }

    protected int weekStarts() {
        return DateTimeConstants.MONDAY;
    }

    protected String getDateFormat() {
        return DATE_FORMAT;
    }
}
