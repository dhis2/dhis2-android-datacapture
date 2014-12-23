package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.Report;

public class ReportConverter implements IJsonConverter<Report> {

    @Override
    public Report deserialize(String source) {
        return null;
    }

    @Override
    public String serialize(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Report object must not be null");
        }

        Gson gson = new Gson();
        return gson.toJson(report);
    }
}
