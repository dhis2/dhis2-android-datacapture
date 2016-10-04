import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.utils.IsTimely;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by george on 10/4/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IsTimely.class})
public class IsTimelyTest {
    private final String period = "2016W37";

    @Test
    public void hasBeenSetShouldReturnTrue(){
        Field field = new Field();
        field.setLabel("Timely");
        field.setDataElement(Constants.TIMELY);
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);
        ArrayList<Group> data = new ArrayList<>();
        Group group = new Group("Test group", fields);
        data.add(group);

        assertThat(IsTimely.hasBeenSet(data), is(true));
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

        assertThat(IsTimely.hasBeenSet(data), is(false));
    }

    @Test
    public void timelyCheckShouldReturnTrue(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.WEEK_OF_YEAR, 39);
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        PowerMockito.mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        Boolean isTimely = IsTimely.check(calendar, period);
        assertThat(isTimely, is(true));
        assertThat(Calendar.getInstance(), is(calendar));
        PowerMockito.verifyStatic();

    }

    @Test
    public void timelyCheckShouldReturnFalseIfHourHasPast12(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.WEEK_OF_YEAR, 39);
        calendar.set(Calendar.HOUR_OF_DAY,13);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        PowerMockito.mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        Boolean isTimely = IsTimely.check(calendar, period);
        assertThat(isTimely, is(false));
        assertThat(Calendar.getInstance(), is(calendar));
        PowerMockito.verifyStatic();

    }

    @Test
    public void timelyCheckShouldReturnFalseIfDayIsNotMonday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.WEEK_OF_YEAR, 39);
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

        PowerMockito.mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        Boolean isTimely = IsTimely.check(calendar, period);
        assertThat(isTimely, is(false));
        assertThat(Calendar.getInstance(), is(calendar));
        PowerMockito.verifyStatic();

    }

    @Test
    public void timelyCheckShouldReturnFalse(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.WEEK_OF_YEAR, 37);
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

        PowerMockito.mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        Boolean isTimely = IsTimely.check(calendar, period);
        assertThat(isTimely, is(false));
        assertThat(Calendar.getInstance(), is(calendar));
        PowerMockito.verifyStatic();

    }

}
