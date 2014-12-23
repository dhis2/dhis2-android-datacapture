package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;

public class OptionSetConverter implements IJsonConverter<OptionSet> {

    @Override
    public OptionSet deserialize(String source) {
        if (source == null) {
            throw new IllegalArgumentException("JSON String object cannot be null");
        }

        Gson gson = new Gson();
        return gson.fromJson(source, OptionSet.class);
    }

    @Override
    public String serialize(OptionSet object) {
        return null;
    }
}
