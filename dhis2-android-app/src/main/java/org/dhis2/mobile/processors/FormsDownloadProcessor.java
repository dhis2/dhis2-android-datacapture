/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.processors;

import static org.dhis2.mobile.io.json.JsonHandler.buildJsonObject;
import static org.dhis2.mobile.io.json.JsonHandler.fromJson;
import static org.dhis2.mobile.io.json.JsonHandler.getAsJsonObject;
import static org.dhis2.mobile.io.json.JsonHandler.getJsonArray;
import static org.dhis2.mobile.io.json.JsonHandler.getJsonObject;
import static org.dhis2.mobile.io.json.JsonHandler.getString;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.io.models.OptionSet;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkException;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.ui.activities.LoginActivity;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FormsDownloadProcessor {
    private static final String TAG = FormsDownloadProcessor.class.getSimpleName();

    private static final String ID = "id";
    private static final String FORMS = "forms";
    private static final String ORG_UNITS = "organisationUnits";

    private static final String DATASETS = "dataSets";
    private static final String OPTIONS = "options";
    private static final String CATEGORY_COMBO = "categoryCombo";

    public static void updateDatasets(Context context, boolean isFirstPull) {
        PrefUtils.setResourceState(context,
                PrefUtils.Resources.DATASETS,
                PrefUtils.State.REFRESHING);

        int networkStatusCode = HttpURLConnection.HTTP_OK;
        int parsingStatusCode = JsonHandler.PARSING_OK_CODE;

        try {
            downloadDatasets(context, PrefUtils.getServerVersion(context).equals(Constants.API_25));
        } catch (NetworkException e) {
            e.printStackTrace();
            networkStatusCode = e.getErrorCode();
        } catch (NullPointerException e) {
            e.printStackTrace();
            parsingStatusCode = JsonHandler.PARSING_FAILED_CODE;
        } catch (ParsingException e) {
            e.printStackTrace();
            parsingStatusCode = JsonHandler.PARSING_FAILED_CODE;
        }

        if (networkStatusCode == HttpURLConnection.HTTP_OK
                && parsingStatusCode == JsonHandler.PARSING_OK_CODE) {
            PrefUtils.setResourceState(context,
                    PrefUtils.Resources.DATASETS,
                    PrefUtils.State.UP_TO_DATE);
        } else {
            PrefUtils.setResourceState(context,
                    PrefUtils.Resources.DATASETS,
                    PrefUtils.State.ATTEMPT_TO_REFRESH_IS_MADE);
        }

        Log.i(TAG, "Download finished");
        Intent intent;
        if(isFirstPull) {
            intent = new Intent(LoginActivity.TAG);
            intent.putExtra(Response.CODE, networkStatusCode);
            intent.putExtra(LoginActivity.IS_FIRST_PULL, isFirstPull);
        }else{
            intent = new Intent(AggregateReportFragment.TAG);
            intent.putExtra(Response.CODE, networkStatusCode);
            intent.putExtra(JsonHandler.PARSING_STATUS_CODE, parsingStatusCode);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private static void downloadDatasets(Context context, boolean oldApi) throws NetworkException, ParsingException {
        System.out.println("is old api"+oldApi);
        final String creds = PrefUtils.getCredentials(context);
        final String server = PrefUtils.getServerURL(context);
        final String datasetsURL = server + URLConstants.DATASETS_URL;

        Response response = download(datasetsURL, creds);
        JsonObject jSource = buildJsonObject(response);

        if (!jSource.has(ORG_UNITS) || !jSource.has(FORMS)) {
            TextFileUtils.removeFile(context,
                    TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
            PrefUtils.setResourceState(context,
                    PrefUtils.Resources.DATASETS,
                    PrefUtils.State.ATTEMPT_TO_REFRESH_IS_MADE);
            return;
        }

        JsonObject jUnits = getJsonObject(jSource, ORG_UNITS);
        JsonObject jDatasets = getJsonObject(jSource, FORMS);

        OrganizationUnit[] units = handleUnitsWithDatasets(jUnits, jDatasets);
        HashMap<String, Form> forms = handleForms(jDatasets);
        for (Map.Entry<String, Form> entry : forms.entrySet()) {
            Form form = forms.get(entry.getKey());
            form = addMetaData(context, form, entry.getKey(), oldApi);
            forms.put(entry.getKey(), form);
            addDataInputPeriodsToOrgUnits(form, units, entry.getKey());
        }
        HashSet<String> optionSetIds = getOptionSetIds(forms);
        updateOptionSets(context, optionSetIds);

        Gson gson = new Gson();
        for (String key : forms.keySet()) {
            Form form = forms.get(key);
            String jForm = gson.toJson(form);
            TextFileUtils.writeTextFile(context,
                    TextFileUtils.Directory.DATASETS, key, jForm);
        }

        String orgUnitsWithDatasets = gson.toJson(units);
        TextFileUtils.writeTextFile(context,
                TextFileUtils.Directory.ROOT,
                TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS,
                orgUnitsWithDatasets);
    }

    private static void addDataInputPeriodsToOrgUnits(Form form, OrganizationUnit[] units,
            String key) {
        if (form.getOptions().getDataInputPeriods() != null
                && form.getOptions().getDataInputPeriods().length > 0) {
            for (OrganizationUnit organizationUnit : units) {
                for (Form orgUnitForm : organizationUnit.getForms()) {
                    if (orgUnitForm.getId().equals(key)) {
                        orgUnitForm.getOptions().setDataInputPeriods(
                                form.getOptions().getDataInputPeriods());
                    }
                }
            }
        }
    }

    private static OrganizationUnit[] handleUnitsWithDatasets(
            JsonObject jUnits, JsonObject jDatasets) throws ParsingException {
        JsonArray modifiedOrgUnits = new JsonArray();

        for (Map.Entry<String, JsonElement> entry : jUnits.entrySet()) {
            JsonObject jUnit = getAsJsonObject(entry.getValue());
            JsonArray unitForms = getJsonArray(jUnit, DATASETS);

            for (int i = 0; i < unitForms.size(); i++) {
                JsonObject jForm = getAsJsonObject(unitForms.get(i));
                String id = getString(jForm, ID);

                JsonObject jDataset = getJsonObject(jDatasets, id);
                JsonObject options = getJsonObject(jDataset, OPTIONS);

                if (jDataset.has(CATEGORY_COMBO)) {
                    JsonObject categoryCombo = getJsonObject(jDataset, CATEGORY_COMBO);
                    jForm.add(CATEGORY_COMBO, categoryCombo);
                }

                jForm.add(OPTIONS, options);
            }

            jUnit.remove(DATASETS);
            jUnit.add(FORMS, unitForms);

            modifiedOrgUnits.add(jUnit);
        }

        // Deserialize organization units, sort them and their forms
        // in alphabetical order, and serialize back into json string
        OrganizationUnit[] orgUnits = fromJson(modifiedOrgUnits, OrganizationUnit[].class);
        Arrays.sort(orgUnits, OrganizationUnit.COMPARATOR);

        for (OrganizationUnit orgUnit : orgUnits) {
            Collections.sort(orgUnit.getForms(), Form.COMPARATOR);
        }

        return orgUnits;
    }

    private static HashMap<String, Form> handleForms(JsonObject jForms) throws ParsingException {
        try {
            HashMap<String, Form> forms = new HashMap<String, Form>();
            Gson gson = new Gson();
            for (Map.Entry<String, JsonElement> entry : jForms.entrySet()) {
                Form form = gson.fromJson(entry.getValue(), Form.class);
                forms.put(entry.getKey(), form);
            }
            return forms;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new ParsingException("The incoming Json is bad/malicious");
        }
    }

    private static Form addMetaData(Context context, Form form, String uid, boolean oldApi)  throws NetworkException, ParsingException {
        String jsonContent = DataSetMetaData.download(context, uid, oldApi);
        DataSetMetaData.addCompulsoryDataElements(DataElementOperandParser.parse(jsonContent), form);
        DataSetMetaData.removeFieldsWithInvalidCategoryOptionRelation(form, DataSetCategoryOptionParser.parse(jsonContent));
        DataSetMetaData.addDataInputPeriods(form, jsonContent);
        return form;

    }

    private static HashSet<String> getOptionSetIds(HashMap<String, Form> forms) throws ParsingException {
        HashSet<String> ids = new HashSet<String>();
        try {
            for (String key : forms.keySet()) {
                Form form = forms.get(key);
                for (Group group : form.getGroups()) {
                    for (Field field : group.getFields()) {
                        if (field.hasOptionSet()) {
                            ids.add(field.getOptionSet());
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new ParsingException("Bad Form object");
        }

        return ids;
    }

    private static void updateOptionSets(Context context, HashSet<String> ids)
            throws NetworkException, ParsingException {
        if (ids == null || ids.size() == 0) {
            return;
        }

        final String creds = PrefUtils.getCredentials(context);
        final String server = PrefUtils.getServerURL(context);

        ArrayList<OptionSet> optionSets = new ArrayList<OptionSet>();
        Gson gson = new Gson();

        try {
            for (String id : ids) {
                String url = server + URLConstants.OPTION_SET_URL + "/" +
                        id + URLConstants.OPTION_SET_PARAM;

                Response response = download(url, creds);

                System.out.println(response.getBody());
                JsonObject jOptionset = buildJsonObject(response);
                OptionSet optionSet = gson.fromJson(jOptionset, OptionSet.class);
                optionSets.add(optionSet);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new ParsingException("The incoming Json is bad/malicious");
        }

        for (OptionSet optionSet : optionSets) {
            String jOptionSet = gson.toJson(optionSet);
            TextFileUtils.writeTextFile(context,
                    TextFileUtils.Directory.OPTION_SETS, optionSet.getId(), jOptionSet);
        }
    }

    private static Response download(String url, String creds) throws NetworkException {
        Response response = HTTPClient.get(url, creds);

        if (!HTTPClient.isError(response.getCode())) {
            return response;
        } else {
            throw new NetworkException(response.getCode());
        }
    }
}