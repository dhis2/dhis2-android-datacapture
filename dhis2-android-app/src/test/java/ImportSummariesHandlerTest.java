import org.dhis2.ehealthMobile.io.handlers.ImportSummariesHandler;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by george on 9/2/16.
 */

public class ImportSummariesHandlerTest {
    private String errorDesc = "Import process was aborted";
    private String successDesc = "Import process completed successfully";

    @Test
    public void unsuccessfulImportSummary(){
        assertThat(ImportSummariesHandler.isSuccess(DummyTestData.ERROR_IMPORT_RESPONSE), is(false));
    }
    @Test
    public void successfulImportSummary(){
        assertThat(ImportSummariesHandler.isSuccess(DummyTestData.SUCCESSFUL_IMPORT_RESPONSE), is(true));
    }
    @Test
    public void checkDescriptionForBadResponse(){
        assertThat(ImportSummariesHandler.getDescription(DummyTestData.ERROR_IMPORT_RESPONSE, errorDesc), is(errorDesc));
    }
    @Test
    public void checkDescriptionForGoodResponse(){
        assertThat(ImportSummariesHandler.getDescription(DummyTestData.SUCCESSFUL_IMPORT_RESPONSE, errorDesc), is(successDesc));
    }

}
