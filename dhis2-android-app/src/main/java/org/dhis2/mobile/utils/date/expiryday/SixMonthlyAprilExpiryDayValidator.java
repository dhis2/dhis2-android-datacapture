package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class SixMonthlyAprilExpiryDayValidator extends SixMonthlyExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'AprilS'";

    public SixMonthlyAprilExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int plusMonths() {
        return 6;
    }

    @Override
    protected int monthOfYear(int period) {
        if (period == 1) {
            return DateTimeConstants.APRIL;
        } else {
            return DateTimeConstants.OCTOBER;
        }
    }

}
