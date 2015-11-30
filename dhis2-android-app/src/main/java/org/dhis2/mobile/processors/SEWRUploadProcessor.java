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

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.handlers.ImportSummariesHandler;
import org.dhis2.mobile.io.holders.ProgramInfoHolder;
import org.dhis2.mobile.io.models.Coordinates;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.NotificationBuilder;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;
import java.util.Random;

public class SEWRUploadProcessor {
    private static final String TAG = "org.dhis2.mobile.processors.SEWRUploadProcessor";
    private SEWRUploadProcessor() { }

    public static void upload(Context context, ProgramInfoHolder info, ArrayList<Group> groups) {
        String data = prepareContent(context, info, groups);

        if (!NetworkUtils.checkConnection(context)) {
            saveReport(context, data, info);
            return;
        }

        String url = PrefUtils.getServerURL(context) + URLConstants.EVENT_URL;
        String creds = PrefUtils.getCredentials(context);
        Response response = HTTPClient.post(url, creds, data);

        String log = String.format("[%s] %s", response.getCode(), response.getBody());
        Log.i(TAG, log);

        if (!HTTPClient.isError(response.getCode())) {
            String description;
            if (ImportSummariesHandler.isSuccess(response.getBody())) {
                description = ImportSummariesHandler.getDescription(response.getBody(),
                        context.getString(R.string.import_successfully_completed));
            } else {
                description = ImportSummariesHandler.getDescription(response.getBody(),
                        context.getString(R.string.import_failed));
            }

            String title = description;
            String message = String.format("(%s) %s", info.getEventDate(), info.getFormLabel());
            NotificationBuilder.fireNotification(context, title, message);
        } else {
            saveReport(context, data, info);
        }
    }

    private static String prepareContent(Context context, ProgramInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson(groups);

        //Should finish with STATUS field
        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
        content.addProperty(Constants.PROGRAM_ID, info.getFormId());
        content.addProperty(Constants.EVENT_DATE, info.getEventDate());
        content.addProperty(Constants.STORED_BY, PrefUtils.getUserName(context));
        content.add(Constants.DATA_VALUES, values);

        Coordinates coords = info.getCoordinates();
        if (coords != null && coords.hasCoordValues()){
            JsonObject jCoords = new JsonObject();

            jCoords.addProperty(Coordinates.LONGITUDE, coords.getLongitude());
            jCoords.addProperty(Coordinates.LATITUDE, coords.getLatitude());

            content.add(Constants.COORDINATE, jCoords);
        }

        return content.toString();
    }

    private static String findDistinctKey(Context context, ProgramInfoHolder info) {
        String key = info.getOrgUnitId() + info.getFormId() + info.getEventDate();
        String temp = key;
        Random rand = new Random();
        while (TextFileUtils.doesFileExist(context, TextFileUtils.Directory.OFFLINE_ANONYMOUS_EVENTS, temp)) {
            temp = key;
            temp += rand.nextInt();
        }
        return temp;
    }

    private static JsonArray putFieldValuesInJson(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.VALUE, field.getValue());
                jFields.add(jField);
            }
        }
        return jFields;
    }

    private static void saveReport(Context context, String data, ProgramInfoHolder info) {
        String key = findDistinctKey(context, info);
        String jsonReportInfo = (new Gson()).toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_ANONYMOUS_EVENTS, key, data);
    }
}
