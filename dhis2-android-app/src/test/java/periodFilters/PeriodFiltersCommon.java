package periodFilters;


import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PeriodFiltersCommon {

    public static DateTime getDateTimeFromString(String dateAsString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateTime date = null;
        try {
            date = new DateTime(format.parse(dateAsString).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
