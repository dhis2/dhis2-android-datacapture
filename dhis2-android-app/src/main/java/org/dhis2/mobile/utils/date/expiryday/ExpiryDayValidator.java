package org.dhis2.mobile.utils.date.expiryday;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class ExpiryDayValidator {

    protected int expiryDays;
    protected String period;
    protected static final String DATE_FORMAT = "yyyyMMdd";


    public ExpiryDayValidator(int expiryDays, String period) {
        this.expiryDays = expiryDays;
        this.period = period;
    }


    public boolean canEdit() {
        LocalDate todayDate = new LocalDate();
        LocalDate maxDate = getMaxDateCanEdit();
        return todayDate.isBefore(maxDate) || todayDate.isEqual(maxDate);
    }

    protected LocalDate getMaxDateCanEdit() {
        LocalDate periodDate = LocalDate.parse(period, DateTimeFormat.forPattern(DATE_FORMAT));
        return periodDate.plusDays(expiryDays - 1);
    }


}
