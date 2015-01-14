package org.hisp.dhis.mobile.datacapture.api.android.converters;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;

public class RawConverter implements IJsonConverter<String> {

    @Override
    public String deserialize(String s) {
        return s;
    }

    @Override
    public String serialize(String s) {
        return s;
    }
}
