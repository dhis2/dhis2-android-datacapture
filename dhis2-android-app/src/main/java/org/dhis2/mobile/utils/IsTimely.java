package org.dhis2.mobile.utils;

import java.util.Calendar;

/**
 * Created by George on 8/26/16.
 */

/**
 * Checks whether the report made is timely or not.
 * Being timely is based on whether the report was submitted before 12 o'lock on Monday
 */
public class IsTimely {
    public IsTimely(){

    }
    public static Boolean check(int period){
        Boolean timely = false;

        int weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(period == weekNumber -2 && currentDay == Calendar.MONDAY && currentHour < 12){
            timely = true;
        }
        return timely;
    }
}