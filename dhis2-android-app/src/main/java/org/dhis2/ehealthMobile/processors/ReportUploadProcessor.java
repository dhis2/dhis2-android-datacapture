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

package org.dhis2.ehealthMobile.processors;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.io.Constants;
import org.dhis2.ehealthMobile.io.handlers.ImportSummariesHandler;
import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.io.models.CategoryOption;
import org.dhis2.ehealthMobile.io.models.Field;
import org.dhis2.ehealthMobile.io.models.Group;
import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.NetworkUtils;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.utils.IsTimely;
import org.dhis2.ehealthMobile.utils.NotificationBuilder;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.dhis2.ehealthMobile.utils.TextFileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class ReportUploadProcessor {
    public static final String TAG = ReportUploadProcessor.class.getSimpleName();

    private ReportUploadProcessor() {
    }

    /**
     * Uploads a report to DHIS2 instance
     * @param context Context
     * @param info DatasetInfoHolder
     * @param groups ArrayList<Group>
     */

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
                String submissionId = info.getFormId()+info.getPeriod();
                DateTime dateTime = new DateTime();
                PrefUtils.saveCompletionDate(context, submissionId, dateTime.toString());
            } else {
                description = ImportSummariesHandler.getDescription(response.getBody(),
                        context.getString(R.string.import_failed));
            }

            String title = description;
            String message = String.format("(%s) %s", info.getPeriodLabel(), info.getFormLabel());
            NotificationBuilder.fireNotification(context, title, message);
        } else {
            saveDataset(context, data, info);
            if(response.getCode() != 401 || response.getCode() != 403){
                SendSmsProcessor.send(context, info, groups);
            }
        }
    }

    /**
     * Combines the dataset info and dataElements with their values into one JSON object and then returns it as a string
     * @param info DatasetInfoHolder
     * @param groups ArrayList<Group>
     * @return String
     */

    public static String prepareContent(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        String period = info.getPeriod();
        JsonArray values = putFieldValuesInJson(groups, period);

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

    private static JsonArray putFieldValuesInJson(ArrayList<Group> groups, String period) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if(!field.getDataElement().equals(Constants.RECEIPT_OF_FORM)){
                    JsonObject jField = new JsonObject();
                    jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                    addCategoryComboToField(field, jField);
                    jField.addProperty(Field.VALUE, getValue(field, period));
                    jFields.add(jField);
                }
            }
        }

        jFields.add(createDataElementObject(Constants.RECEIPT_OF_FORM,"", Constants.INTERNET_SUBMISSION));

        return jFields;
    }

    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }

    private static String getValue(Field field, String period){
        String value;
        switch (field.getDataElement()){
            case Constants.DATE_RECEIVED:
                // Retrieve current date
                LocalDate currentDate = new LocalDate();
                value = currentDate.toString(Constants.DATE_FORMAT);
                break;
            case Constants.TIMELY:
                //Check whether a timely report has already been sent
                if(!IsTimely.hasBeenSet(field)) {
                    //Check whether the report was timely or not
                    Boolean isTimely = IsTimely.check(new DateTime(), period);
                    value =  String.valueOf(isTimely);
                }else{
                    value = field.getValue();
                }
                break;
            default:
                value = field.getValue();
                break;
        }
        return value;

    }

    private static void addCategoryComboToField(Field field, JsonObject jField){
        if(!field.getCategoryOptionCombo().equals(Constants.DEFAULT_CATEGORY_COMBO)){
            jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
        }else{
            jField.addProperty(Field.CATEGORY_OPTION_COMBO, "");
        }
    }

    private static JsonObject createDataElementObject(String dataElement, String categoryCombo, String value){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Field.DATA_ELEMENT, dataElement);
        jsonObject.addProperty(Field.CATEGORY_OPTION_COMBO, categoryCombo);
        jsonObject.addProperty(Field.VALUE, value);

        return jsonObject;
    }

}
