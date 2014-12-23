package org.hisp.dhis.mobile.datacapture.api.android.converters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.hisp.dhis.mobile.datacapture.api.managers.IJsonConverter;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.api.models.Program;
import org.hisp.dhis.mobile.datacapture.api.models.ProgramHolder;
import org.hisp.dhis.mobile.datacapture.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramsConverter implements IJsonConverter<ProgramHolder> {
    private static final String ORGANIZATION_UNITS = "organisationUnits";
    private static final String FORMS = "forms";

    @Override
    public ProgramHolder deserialize(String source) {
        JsonObject jDocument = JsonUtils.buildJsonObject(source);
        JsonObject jOrganizationUnits = jDocument.getAsJsonObject(ORGANIZATION_UNITS);
        JsonObject jForms = jDocument.getAsJsonObject(FORMS);

        Gson gson = new Gson();
        List<OrganisationUnit> units = new ArrayList<OrganisationUnit>();
        for (Map.Entry<String, JsonElement> entry : jOrganizationUnits.entrySet()) {
            OrganisationUnit unit = gson.fromJson(entry.getValue(), OrganisationUnit.class);
            units.add(unit);
        }

        List<Program> programs = new ArrayList<Program>();
        for (Map.Entry<String, JsonElement> entry : jForms.entrySet()) {
            Program program = gson.fromJson(entry.getValue(), Program.class);
            programs.add(program);
        }

        return new ProgramHolder(units, programs);
    }

    @Override
    public String serialize(ProgramHolder object) {
        return null;
    }
}
