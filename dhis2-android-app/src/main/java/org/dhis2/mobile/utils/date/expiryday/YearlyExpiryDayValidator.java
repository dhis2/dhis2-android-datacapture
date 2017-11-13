package org.dhis2.mobile.utils.date.expiryday;

public class YearlyExpiryDayValidator extends MonthlyExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy";

    public YearlyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected int plusMonths() {
        return 12;
    }

    @Override
    public String getDateFormat() {
        return DATE_FORMAT;
    }
}
