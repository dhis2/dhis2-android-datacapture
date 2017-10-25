package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;

public class FinancialYearJulyExpiryDayValidator extends SixMonthlyExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'Jul'";

    public FinancialYearJulyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int plusMonths() {
        return 12;
    }

    @Override
    protected int monthOfYear(int periodNumber) {
        return DateTimeConstants.JULY;
    }
}
