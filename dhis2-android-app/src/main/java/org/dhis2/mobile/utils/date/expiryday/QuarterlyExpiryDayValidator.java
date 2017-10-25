package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class QuarterlyExpiryDayValidator extends SixMonthlyExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'Q'";

    public QuarterlyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int plusMonths() {
        return 3;
    }

    @Override
    protected int monthOfYear(int periodNumber) {
        switch (periodNumber) {
            case 1:
                return DateTimeConstants.JANUARY;
            case 2:
                return DateTimeConstants.APRIL;
            case 3:
                return DateTimeConstants.JULY;
            case 4:
                return DateTimeConstants.OCTOBER;
            default:
                return DateTimeConstants.JANUARY;

        }
    }
}
