package org.dhis2.mobile.processors;

import static org.dhis2.mobile.network.Response.CATEGORY_OPTION_COMBOS_KEY;
import static org.dhis2.mobile.network.Response.ID_KEY;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.CategoryCombo;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class SectionCategoryComboUIdDownloadProcessor {

    public SectionCategoryComboUIdDownloadProcessor() {
    }

    public static CategoryCombo download(Context context, String categoryComboUId) {
        String url = buildUrl(context, categoryComboUId);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            List<String> categoryOptionComboUIds = parseCategoryOptionCombo(response.getBody());
            CategoryCombo categoryCombo = new CategoryCombo();
            categoryCombo.setId(categoryComboUId);
            categoryCombo.setCategoryOptionComboUIdList(categoryOptionComboUIds);
            return categoryCombo;
        }
        return null;
    }

    private static List<String> parseCategoryOptionCombo(String responseBody) {
        List<String> categoryOptionComboUIds = new ArrayList<>();
        if (responseBody != null) {
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray(
                        CATEGORY_OPTION_COMBOS_KEY);
                if (jsonArray.size() == 0) {
                    return categoryOptionComboUIds;
                }
                for (JsonElement item : jsonArray) {
                    categoryOptionComboUIds.add(
                            ((JsonObject) item).get(ID_KEY).getAsString());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return categoryOptionComboUIds;
    }

    private static String buildUrl(Context context, String categoryComboUId) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.CATEGORY_COMBOS_URL + "/" + categoryComboUId;

        return url;
    }
}
