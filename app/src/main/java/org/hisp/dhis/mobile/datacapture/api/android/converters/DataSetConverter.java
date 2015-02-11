package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.DataSetHolder;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.utils.JsonUtils;

import java.lang.reflect.Type;
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
        Type orgUnitType = new TypeToken<Map<String, OrganisationUnit>>() { }.getType();
        Type dataSetType = new TypeToken<Map<String, DataSet>>() { }.getType();

        Map<String, OrganisationUnit> units = gson.fromJson(jOrganizationUnits, orgUnitType);
        Map<String, DataSet> dataSets = gson.fromJson(jForms, dataSetType);

        for (Map.Entry<String, OrganisationUnit> entry: units.entrySet()) {
            entry.getValue().setId(entry.getKey());
        }

        for (Map.Entry<String, DataSet> entry: dataSets.entrySet()) {
            entry.getValue().setId(entry.getKey());
        }
        return new DataSetHolder(units, dataSets);
    }

    @Override
    public String serialize(DataSetHolder object) {
        return null;
    }
}
