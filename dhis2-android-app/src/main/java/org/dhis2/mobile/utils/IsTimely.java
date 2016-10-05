package org.dhis2.mobile.utils;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
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
    public static Boolean check(DateTime currentDateTime, String period){
        Boolean timely = false;

        MutableDateTime periodTime = new MutableDateTime();
        int periodWeek = Integer.parseInt(period.substring(5));
        int periodYear = Integer.parseInt(period.substring(0,4));
        periodTime.setYear(periodYear);
        periodTime.setWeekOfWeekyear(periodWeek);

        if(currentDateTime.isAfter(periodTime) && Days.daysBetween(periodTime, currentDateTime).isLessThan(Days.SEVEN) &&
                currentDateTime.dayOfWeek().getAsText().equals("Monday") && currentDateTime.hourOfDay().get() < 12){
            timely = true;
        }

        return timely;
    }

    public static Boolean hasBeenSet(ArrayList<Group> groups){

        for(Group group : groups){
            for(Field field : group.getFields()){
                if(field.getDataElement().equals(Constants.TIMELY) && field.getValue() != null){
                    return true;
                }
            }
        }

        return false;
    }

}
