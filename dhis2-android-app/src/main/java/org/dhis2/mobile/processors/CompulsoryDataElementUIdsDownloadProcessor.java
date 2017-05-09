package org.dhis2.mobile.processors;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public static List<String> download(Context context, DatasetInfoHolder info) {
        String url = buildUrl(context, info);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        List<String> uIdList = null;
        if (response.getCode() >= 200 && response.getCode() < 300) {
            uIdList = parseCompulsoryUIds(response.getBody());
        }
        return uIdList;
    }

    private static String buildUrl(Context context, DatasetInfoHolder info) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.DATASET_VALUES_URL + "/" + info.getFormId() + "?"
                + URLConstants.COMPULSORY_DATA_ELEMENTS_PARAM;

        return url;
    }

    private static List<String> parseCompulsoryUIds(String responseBody) {
        if (responseBody != null) {
            try {
                List<String> list = new ArrayList<>();
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray("compulsoryDataElementOperands");
                if (jsonArray.size() == 0) {
                    return list;
                }
                for (JsonElement item : jsonArray) {
                    JsonObject dataElement = ((JsonObject) item).get(
                            "dataElement").getAsJsonObject();
                    String uid = dataElement.get("id").getAsString();
                    list.add(uid);
                }
                return list;
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}