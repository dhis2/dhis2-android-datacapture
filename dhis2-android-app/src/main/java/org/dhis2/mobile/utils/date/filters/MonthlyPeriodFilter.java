package org.dhis2.mobile.utils.date.filters;


import org.dhis2.mobile.utils.date.PeriodFilter;
import org.joda.time.DateTime;

public class MonthlyPeriodFilter extends PeriodFilter {
    public MonthlyPeriodFilter(DateTime startDate, DateTime endDate) {
        super(startDate, endDate);
    }

    @Override
    public boolean apply() {
        if ((startDate == null && endDate == null) || selectedDate == null) {
            return false;
        }

        if (startDate != null && endDate != null) {
            // return true, if criteria is not between two dates
            // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
            return !(getMonthAndYear(selectedDate)>=getMonthAndYear(startDate)
                    && (getMonthAndYear(selectedDate)<=getMonthAndYear(endDate)));
        }

        if (startDate != null) {
            // return true, if criteria is before startDate
            // return startDate.isBefore(selectedDate);
            return !(getMonthAndYear(selectedDate)>=(getMonthAndYear(startDate)));
        }

        // return true, if criteria is after endDate
        // return endDate.isAfter(selectedDate);
        return !(getMonthAndYear(selectedDate)<=(getMonthAndYear(endDate)));
    }

    public int getMonthAndYear(DateTime date){
        String month =  date.getMonthOfYear()+"";
        if(month.length()==1){
            month=0+month;
        }
       return new Integer(date.getYear() + "" + month);
    }
}
