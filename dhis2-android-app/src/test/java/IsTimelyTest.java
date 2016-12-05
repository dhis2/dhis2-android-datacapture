import org.dhis2.ehealthMobile.io.Constants;
import org.dhis2.ehealthMobile.io.models.Field;
import org.dhis2.ehealthMobile.io.models.Group;
import org.dhis2.ehealthMobile.utils.IsTimely;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by george on 10/4/16.
 */
public class IsTimelyTest {
    private final String period = "2016W39";

    @Test
    public void hasBeenSetShouldReturnTrue(){
        Field field = new Field();
        field.setLabel("Timely");
        field.setDataElement(Constants.TIMELY);
        field.setValue("false");
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);
        ArrayList<Group> data = new ArrayList<>();
        Group group = new Group("Test group", fields);
        data.add(group);

//        assertThat(IsTimely.hasBeenSet(data), is(true));
    }

    @Test
    public void hasBeenSetShouldReturnFalse(){
        Field field = new Field();
        field.setLabel("Something that's not Timely");
        field.setDataElement(Constants.UNDER_FIVE_CASES);
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);
        ArrayList<Group> data = new ArrayList<>();
        Group group = new Group("Test group", fields);
        data.add(group);

//        assertThat(IsTimely.hasBeenSet(data), is(false));
    }

    @Test
    public void timelyCheckShouldReturnTrue(){
        //Current date set to Monday 3rd October 2016. 11AM. Week Number = 40
        DateTime dt = new DateTime(2016,10,3,11, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(true));
    }

    @Test
    public void timelyCheckShouldReturnFalseIfHourHasPast12(){
        //Current date set to Monday 3rd October 2016. 1PM. Week Number = 40
        DateTime dt = new DateTime(2016,10,3,13, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(false));
    }

    @Test
    public void timelyCheckShouldReturnFalseIfDayIsNotMonday(){
        //Current date set to Tuesday 4th October 2016. 11AM. Week Number = 40
        DateTime dt = new DateTime(2016,10,4,11, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(false));
    }

    @Test
    public void timelyCheckShouldReturnFalseIfPeriodYearIsGreaterThanCurrentYear(){
        String period = "2345W39";
        //Current date set to Monday 3rd October 2016. 11AM. Week Number = 40
        DateTime dt = new DateTime(2016,10,3,11, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(false));
    }

    @Test
    public void timelyCheckShouldReturnFalseIfPeriodWeekNumberIsGreaterThanCurrentYearWeekNumber(){
        String period = "2016W41";
        //Current date set to Monday 3rd October 2016. 11AM. Week Number = 40
        DateTime dt = new DateTime(2016,10,3,11, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(false));
    }

    @Test
    public void timelyCheckShouldReturnFalseIfDifferenceBetweenPeriodWeekAndCurrentWeekExceeds7Days(){
        String period = "2016W37";
        //Current date set to Monday 3rd October 2016. 11AM. Week Number = 40
        DateTime dt = new DateTime(2016,10,3,11, 0, 0, 0);
        Boolean isTimely = IsTimely.check(dt, period);

        assertThat(isTimely, is(false));
    }

}
