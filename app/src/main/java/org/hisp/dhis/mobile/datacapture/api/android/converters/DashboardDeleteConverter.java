package org.hisp.dhis.mobile.datacapture.api.android.converters;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;

public class DashboardDeleteConverter implements IJsonConverter<String> {

    @Override
    public String deserialize(String source) {
        return source;
    }

    @Override
    public String serialize(String object) {
        return object;
    }
}
