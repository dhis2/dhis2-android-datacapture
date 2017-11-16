package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;

public class YearlyPeriodFilter extends PeriodFilter {

    public YearlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(startDate,endDate);
    }

    @Override
    public boolean apply() {
        if ((startDate == null && endDate == null) || selectedDate == null) {
            return false;
        }

        if (startDate != null && endDate != null) {
            // return true, if criteria is not between two dates
            // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
            return !((selectedDate.getYear()>=(startDate.getYear()))
                    && (selectedDate.getYear()<=(endDate.getYear())));
        }

        if (startDate != null) {
            // return true, if criteria is before startDate
            // return startDate.isBefore(selectedDate);
            return !(selectedDate.getYear()>=(startDate.getYear()));
        }

        // return true, if criteria is after endDate
        // return endDate.isAfter(selectedDate);
        return !(selectedDate.getYear()<=(endDate.getYear()));
    }
}
