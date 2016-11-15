import android.content.Context;

import org.dhis2.ehealthMobile.io.handlers.UserAccountHandler;
import org.dhis2.ehealthMobile.io.models.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;

/**
 * Created by george on 9/6/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAccountHandlerTest {
    private ArrayList<Field> fields;


    @Mock
    Context mMockContext;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        fields = UserAccountHandler.toFields(mMockContext, DummyTestData.PROFILE_DATA);
    }

    @Test
    public void checkThatUserDataIsNotEmpty(){
        assertThat(fields.isEmpty(), is(false));
    }

    @Test
    public void checkFirstDataElement(){
        assertThat(fields.get(0).getDataElement(), is(DummyTestData.FIRST_DATA_ELEMENT_IN_USER_ACCOUNT_FIELDS));
    }

    @Test
    public void checkFirstName(){
        assertThat(fields.get(0).getValue(), is(DummyTestData.FIRST_NAME));
    }

    @Test
    public void checkProfileFields(){
        assertThat(UserAccountHandler.fromFields(fields), is(DummyTestData.PROCESSED_PROFILE_DATA));
    }


}
