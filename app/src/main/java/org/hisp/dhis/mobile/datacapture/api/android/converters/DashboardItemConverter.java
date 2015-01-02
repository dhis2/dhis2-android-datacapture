package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;

public class DashboardItemConverter implements IJsonConverter<DashboardItem> {

    @Override
    public DashboardItem deserialize(String source) {
        if (source == null) {
            throw new IllegalArgumentException("Source String must not be null");
        }

        Gson gson = new Gson();
        return gson.fromJson(source, DashboardItem.class);
    }

    @Override
    public String serialize(DashboardItem object) {
        if (object == null) {
            throw new IllegalArgumentException("DashboardItem must not be null");
        }

        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
