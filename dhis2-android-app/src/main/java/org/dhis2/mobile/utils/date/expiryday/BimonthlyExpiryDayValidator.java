package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class BimonthlyExpiryDayValidator extends ExpiryDayValidator {

    protected static final String DATE_FORMAT = "yyyy";

    public BimonthlyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected LocalDate getMaxDateCanEdit() {
        int periodNumber = Integer.parseInt(period.substring(4, 6));
        LocalDate periodDate = LocalDate.parse(period.substring(0, 4),
                DateTimeFormat.forPattern(DATE_FORMAT));
        periodDate = periodDate.withMonthOfYear(monthOfYear(periodNumber));
        periodDate = periodDate.plusMonths(plusMonths());
        return periodDate.plusDays(expiryDays - 2);
    }


    protected int plusMonths() {
        return 2;
    }

    protected int monthOfYear(int periodNumber) {
        switch (periodNumber) {
            case 1:
                return DateTimeConstants.JANUARY;
            case 2:
                return DateTimeConstants.MARCH;
            case 3:
                return DateTimeConstants.MAY;
            case 4:
                return DateTimeConstants.JULY;
            case 5:
                return DateTimeConstants.SEPTEMBER;
            case 6:
                return DateTimeConstants.NOVEMBER;
            default:
                return DateTimeConstants.JANUARY;

        }
    }
}
