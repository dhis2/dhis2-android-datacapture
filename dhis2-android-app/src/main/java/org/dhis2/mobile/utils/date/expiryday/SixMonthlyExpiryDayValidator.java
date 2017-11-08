package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class SixMonthlyExpiryDayValidator extends ExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyy'S'";

    public SixMonthlyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }


    @Override
    protected LocalDate getMaxDateCanEdit() {
        int periodNumber = Character.getNumericValue(period.charAt(period.length() - 1));
        LocalDate periodDate = LocalDate.parse(period.substring(0, period.length() - 2),
                DateTimeFormat.forPattern(DATE_FORMAT));
        periodDate = periodDate.withMonthOfYear(monthOfYear(periodNumber));
        periodDate = periodDate.plusMonths(plusMonths());
        return periodDate.plusDays(expiryDays - 2);
    }

    protected int plusMonths() {
        return 6;
    }

    protected int monthOfYear(int periodNumber) {
        if (periodNumber == 1) {
            return DateTimeConstants.JANUARY;
        } else {
            return DateTimeConstants.JULY;
        }
    }
}
