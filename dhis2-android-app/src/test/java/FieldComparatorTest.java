import org.dhis2.mobile.io.models.Field;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by george on 9/29/16.
 */

public class FieldComparatorTest {

    @Test
    public void comparisonTest(){
        Field field1 = new Field();
        field1.setLabel("b");
        Field field2 = new Field();
        field2.setLabel("a");
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(field1, field2));
        Collections.sort(fields, Field.COMPARATOR);
        assertThat(fields.get(0).getLabel(), is("a"));
        assertThat(fields.get(1).getLabel(), is("b"));
    }
}
