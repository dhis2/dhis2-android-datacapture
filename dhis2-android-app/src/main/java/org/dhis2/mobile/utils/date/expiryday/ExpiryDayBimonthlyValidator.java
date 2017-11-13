package org.dhis2.mobile.utils.date.expiryday;

public class ExpiryDayBimonthlyValidator extends MonthlyExpiryDayValidator {

    protected static final String DATE_FORMAT = "yyyyMM'B'";

    public ExpiryDayBimonthlyValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int plusMonths() {
        return 2;
    }

    @Override
    public String getDateFormat() {
        return DATE_FORMAT;
    }
}
