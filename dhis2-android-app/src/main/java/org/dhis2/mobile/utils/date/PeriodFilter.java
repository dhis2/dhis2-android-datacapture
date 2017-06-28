package org.dhis2.mobile.utils.date;


import org.dhis2.mobile.ui.models.Filter;
import org.joda.time.DateTime;

import java.io.Serializable;

public class PeriodFilter implements Filter,Serializable{
    protected final DateTime startDate;
    protected final DateTime endDate;
    protected DateTime selectedDate;

    public PeriodFilter(DateTime startDate, DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setSelectedDate(DateTime selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Override
    public boolean apply() {
        if ((startDate == null && endDate == null) || selectedDate == null) {
            return false;
        }

        if (startDate != null && endDate != null) {
            // return true, if criteria is not between two dates
            // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
            return !((selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate))
                    && (selectedDate.isBefore(endDate) || selectedDate.isEqual(endDate)));
        }

        if (startDate != null) {
            // return true, if criteria is before startDate
            // return startDate.isBefore(selectedDate);
            return !(selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate));
        }

        // return true, if criteria is after endDate
        // return endDate.isAfter(selectedDate);
        return !(selectedDate.isBefore(endDate));
    }
}