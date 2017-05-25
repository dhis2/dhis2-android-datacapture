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
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.models.CategoryOption;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.NotificationBuilder;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class ReportUploadProcessor {
    public static final String TAG = ReportUploadProcessor.class.getSimpleName();

    private ReportUploadProcessor() {
    }

    public static void upload(Context context, DatasetInfoHolder info, ArrayList<Group> groups) {
        String data = prepareContent(info, groups);

        if (!NetworkUtils.checkConnection(context)) {
            saveDataset(context, data, info);
            return;
        }

        String url = PrefUtils.getServerURL(context) + URLConstants.DATASET_UPLOAD_URL;
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

            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.LOG,
                    TextFileUtils.FileNames.LOG.toString(),
                    info.getLogMessage(context, false) + description,
                    info.getFormattedDate());

            NotificationBuilder.fireNotification(context, description, info.getNotification());


        } else {
            String message = info.getErrorMessage(context, false, response);

            NotificationBuilder.fireNotification(context,
                    context.getString(R.string.network_error) + " " + response.getCode(), message);

            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.LOG,
                    TextFileUtils.FileNames.LOG.toString(),
                    message,
                    info.getFormattedDate());

            saveDataset(context, data, info);
        }
    }

    private static String prepareContent(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson(groups);

        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);

        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
        content.addProperty(Constants.DATA_SET_ID, info.getFormId());
        content.addProperty(Constants.PERIOD, info.getPeriod());
        content.addProperty(Constants.COMPLETE_DATE, completeDate);
        content.add(Constants.DATA_VALUES, values);

        JsonArray categoryOptions = putCategoryOptionsInJson(info.getCategoryOptions());
        if (categoryOptions != null) {
            content.add(Constants.ATTRIBUTE_CATEGORY_OPTIONS, categoryOptions);
        }

        return content.toString();
    }

    private static JsonArray putCategoryOptionsInJson(List<CategoryOption> categoryOptions) {
        if (categoryOptions != null && !categoryOptions.isEmpty()) {
            JsonArray jsonOptions = new JsonArray();

            // processing category options
            for (CategoryOption categoryOption : categoryOptions) {
                jsonOptions.add(categoryOption.getId());
            }

            return jsonOptions;
        }

        return null;
    }

    private static JsonArray putFieldValuesInJson(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());
                jFields.add(jField);
            }
        }
        return jFields;
    }

    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }
}
