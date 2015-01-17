package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;

import java.lang.reflect.Type;
import java.util.List;

public class InterpretationConverter implements IJsonConverter<List<Interpretation>> {

    @Override
    public List<Interpretation> deserialize(String s) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Interpretation>>() { }.getType();
        return gson.fromJson(s, type);
    }

    @Override
    public String serialize(List<Interpretation> interpretations) {
        Gson gson = new Gson();
        return gson.toJson(interpretations);
    }
}
