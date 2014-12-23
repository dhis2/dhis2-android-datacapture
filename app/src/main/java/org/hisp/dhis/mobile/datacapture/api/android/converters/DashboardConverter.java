package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.List;

public class DashboardConverter implements IJsonConverter<List<Dashboard>> {

    @Override
    public List<Dashboard> deserialize(String source) {
        JsonArray jDocument = JsonUtils.buildJsonArray(source);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Dashboard>>() { }.getType();
        return gson.fromJson(jDocument, type);
    }

    @Override
    public String serialize(List<Dashboard> object) {
        return null;
    }
}
