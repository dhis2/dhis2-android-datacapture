package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;

public class DashboardNameConverter implements IJsonConverter<String> {
    private static final String NAME = "name";

    @Override
    public String deserialize(String source) {
        return source;
    }

    @Override
    public String serialize(String name) {
        Gson gson = new Gson();
        JsonObject jBody = new JsonObject();
        jBody.addProperty(NAME, name);
        return gson.toJson(jBody);
    }
}
