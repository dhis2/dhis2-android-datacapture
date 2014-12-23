package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetHolder;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSetConverter implements IJsonConverter<DataSetHolder> {
    private static final String ORGANIZATION_UNITS = "organisationUnits";
    private static final String FORMS = "forms";


    @Override
    public DataSetHolder deserialize(String source) {
        JsonObject jDocument = JsonUtils.buildJsonObject(source);
        JsonObject jOrganizationUnits = jDocument.getAsJsonObject(ORGANIZATION_UNITS);
        JsonObject jForms = jDocument.getAsJsonObject(FORMS);

        Gson gson = new Gson();
        List<OrganisationUnit> units = new ArrayList<OrganisationUnit>();
        for (Map.Entry<String, JsonElement> entry : jOrganizationUnits.entrySet()) {
            OrganisationUnit unit = gson.fromJson(entry.getValue(), OrganisationUnit.class);
            units.add(unit);
        }

        List<DataSet> dataSets = new ArrayList<DataSet>();
        for (Map.Entry<String, JsonElement> entry : jForms.entrySet()) {
            DataSet dataSet = gson.fromJson(entry.getValue(), DataSet.class);
            dataSets.add(dataSet);
        }

        return new DataSetHolder(units, dataSets);
    }

    @Override
    public String serialize(DataSetHolder object) {
        return null;
    }
}
