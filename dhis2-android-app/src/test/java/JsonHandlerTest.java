import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.ehealthMobile.io.json.JsonHandler;
import org.dhis2.ehealthMobile.io.json.ParsingException;
import org.dhis2.ehealthMobile.io.models.OrganizationUnit;
import org.dhis2.ehealthMobile.network.Response;
import org.junit.Before;
import org.junit.Test;

import static org.dhis2.ehealthMobile.io.json.JsonHandler.fromJson;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by george on 9/6/16.
 */
public class JsonHandlerTest {
    private Response responseObject;

    @Before
    public void setup(){
        responseObject = new Response(200, DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE);
    }

    @Test(expected = ParsingException.class)
    public void ShouldNotBuildObjectWhenPassedEmptyString() throws ParsingException {
        JsonHandler.buildJsonObject("");
    }


    @Test
    public void shouldBuildJsonObject() throws ParsingException {
        assertThat(JsonHandler.buildJsonObject(responseObject.getBody()), instanceOf(JsonElement.class));
    }

    @Test
    public void shouldBuildJsonArray() throws ParsingException {
        assertThat(JsonHandler.buildJsonArray(DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE_ARRAY).get(0).toString(), is(DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE));
    }
    @Test(expected = ParsingException.class)
    public void shouldNotBuildJsonArray() throws ParsingException {
        //responseObject body is not an array. Should throw an error.
        JsonHandler.buildJsonArray(responseObject.getBody());
    }

    @Test
    public void shouldGetAsJsonObject() throws ParsingException {
        JsonElement jsonElement = JsonHandler.buildJsonObject(responseObject.getBody());
        assertThat(JsonHandler.getAsJsonObject(jsonElement), instanceOf(JsonObject.class));
        assertThat(JsonHandler.getAsJsonObject(jsonElement).has(DummyTestData.ID), is(true));

    }

    @Test(expected = ParsingException.class)
    public void shouldNotGetAsJsonObject() throws ParsingException{
        JsonElement jsonElement = JsonHandler.buildJsonArray(DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE_ARRAY);
        JsonHandler.getAsJsonObject(jsonElement);
    }

    @Test
    public void shouldGetAsJsonArray() throws ParsingException {
        JsonElement jsonElement = JsonHandler.buildJsonArray(DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE_ARRAY);
        assertThat(JsonHandler.getAsJsonArray(jsonElement), instanceOf(JsonArray.class));
        assertThat(JsonHandler.getAsJsonArray(jsonElement).get(0).toString(), is(DummyTestData.GOOD_GET_METHOD_FORM_RESPONSE));
    }

    @Test(expected = ParsingException.class)
    public void shouldNotGetAsJsonArray() throws ParsingException {
        JsonElement jsonElement = JsonHandler.buildJsonObject(responseObject.getBody());
       JsonHandler.getAsJsonArray(jsonElement);
    }

    @Test
    public void shouldCreateOrganisationSerializableFromArray() throws ParsingException {
        JsonArray jsonArray = JsonHandler.buildJsonArray(DummyTestData.ORG_UNITS);
        assertThat(JsonHandler.fromJson(jsonArray, OrganizationUnit[].class), instanceOf(OrganizationUnit[].class));
    }

    @Test(expected = ParsingException.class)
    public void shouldNotCreateSerializableFromArray() throws ParsingException{
        JsonArray jsonArray = JsonHandler.buildJsonArray(DummyTestData.ORG_UNITS);
        //should throw exeption because an object of OrganizationUnit.class is being passed in instead of an array
        assertThat(JsonHandler.fromJson(jsonArray, OrganizationUnit.class), instanceOf(OrganizationUnit.class));
    }





}