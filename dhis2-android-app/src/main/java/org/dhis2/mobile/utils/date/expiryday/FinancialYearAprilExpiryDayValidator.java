package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class FinancialYearAprilExpiryDayValidator extends ExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'April'";

    public FinancialYearAprilExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected LocalDate getMaxDateCanEdit() {
        LocalDate periodDate = LocalDate.parse(period,
                DateTimeFormat.forPattern(DATE_FORMAT));
        periodDate = periodDate.withMonthOfYear(monthOfYear());
        periodDate = periodDate.plusMonths(plusMonths());
        return periodDate.plusDays(expiryDays - 2);
    }

    protected int plusMonths() {
        return 12;
    }


    protected int monthOfYear() {
        return DateTimeConstants.APRIL;
    }
}
