import org.dhis2.mobile.io.handlers.ImportSummariesHandler;
import org.dhis2.mobile.network.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by george on 9/2/16.
 */

public class ImportSummariesHandlerTest {
    private Response mBadResponse;
    private Response mGoodResponse;
    private String errorDesc = "Import process was aborted";
    private String successDesc = "Import process completed successfully";

    @Before
    public  void createResponse(){
        mBadResponse = new Response(200, DummyDataTest.ERROR_IMPORT_RESPONSE);
        mGoodResponse = new Response(200, DummyDataTest.SUCCESSFUL_IMPORT_RESPONSE);
    }

    @Test
    public void unsuccessfulImportSummary(){
        assertThat(ImportSummariesHandler.isSuccess(mBadResponse.getBody()), is(false));
    }
    @Test
    public void successfulImportSummary(){
        assertThat(ImportSummariesHandler.isSuccess(mGoodResponse.getBody()), is(true));
    }
    @Test
    public void checkDescriptionForBadResponse(){
        assertThat(ImportSummariesHandler.getDescription(mBadResponse.getBody(), errorDesc), is(errorDesc));
    }
    @Test
    public void checkDescriptionForGoodResponse(){
        assertThat(ImportSummariesHandler.getDescription(mGoodResponse.getBody(), errorDesc), is(successDesc));
    }

}
