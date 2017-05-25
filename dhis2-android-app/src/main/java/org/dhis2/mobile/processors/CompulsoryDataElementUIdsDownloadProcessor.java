package org.dhis2.mobile.processors;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.holders.DataElementOperand;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class CompulsoryDataElementUIdsDownloadProcessor {

    public CompulsoryDataElementUIdsDownloadProcessor() {
    }

    public static List<DataElementOperand> download(Context context, DatasetInfoHolder info) {
        String url = buildUrl(context, info);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        List<DataElementOperand> dataElementOperandsList = null;
        if (response.getCode() >= 200 && response.getCode() < 300) {
            dataElementOperandsList = parseToDataElementOperandsList(response.getBody());
        }
        return dataElementOperandsList;
    }

    private static String buildUrl(Context context, DatasetInfoHolder info) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.DATASET_VALUES_URL + "/" + info.getFormId() + "?"
                + URLConstants.COMPULSORY_DATA_ELEMENTS_PARAM;

        return url;
    }

    private static List<DataElementOperand> parseToDataElementOperandsList(String responseBody) {
        if (responseBody != null) {
            try {
                List<DataElementOperand> dataElementOperandList = new ArrayList<>();
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray("compulsoryDataElementOperands");
                if (jsonArray.size() == 0) {
                    return dataElementOperandList;
                }
                parseToDataElementOperandsList(dataElementOperandList, jsonArray);
                return dataElementOperandList;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void parseToDataElementOperandsList(List<DataElementOperand> list, JsonArray jsonArray) {
        for (JsonElement item : jsonArray) {
            JsonObject jsonDataElementOperand = (JsonObject) item;
            JsonObject categoryOptionCombo = jsonDataElementOperand.get(
                    "categoryOptionCombo").getAsJsonObject();
            JsonObject dataElement = jsonDataElementOperand.get(
                    "dataElement").getAsJsonObject();
            DataElementOperand dataElementOperand = new DataElementOperand(categoryOptionCombo.get("id").getAsString(), dataElement.get("id").getAsString());
            list.add(dataElementOperand);
        }
    }
}