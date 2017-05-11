package org.dhis2.mobile.processors;

import static org.dhis2.mobile.network.Response.CATEGORY_COMBOS_KEY;
import static org.dhis2.mobile.network.Response.CATEGORY_COMBO_KEY;
import static org.dhis2.mobile.network.Response.CATEGORY_OPTION_COMBOS_KEY;
import static org.dhis2.mobile.network.Response.DATA_ELEMENT_KEY;
import static org.dhis2.mobile.network.Response.DATA_SET_KEY;
import static org.dhis2.mobile.network.Response.ID_KEY;
import static org.dhis2.mobile.network.Response.SECTIONS_KEY;
import static org.dhis2.mobile.network.Response.SECTION_NAME_KEY;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.CategoryCombo;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryOptionRelationsByDataSetDownloadProcessor {

    public CategoryOptionRelationsByDataSetDownloadProcessor() {
    }

    public static void download(Context context,
            DatasetInfoHolder info) {
        String url = buildUrl(context, info);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            info.setDefaultCategoryCombo(addDefaultCategoryCombo(context,
                    response.getBody()));
            info.setCategoryComboByDataElement(parseCategoryComboDataElementRelations(
                    response.getBody()));
            info.setCategoryOptionComboUIdsBySection(parseCategoryComboBySectionRelations(
                    response.getBody()));
        }
    }

    private static String buildUrl(Context context, DatasetInfoHolder info) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.DATASET_VALUES_URL + "/" + info.getFormId() + "?"
                + URLConstants.CATEGORY_OPTION_DATA_ELEMENTS_PARAM;

        return url;
    }

    private static HashMap<String, List<String>> parseCategoryComboBySectionRelations(
            String responseBody) {
        if (responseBody != null) {
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray(SECTIONS_KEY);
                if (jsonArray.size() == 0) {
                    return new HashMap<>();
                }
                return parseDataElementCategoryComboSectionMap(jsonArray);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static HashMap<String, List<String>> parseDataElementCategoryComboSectionMap(
            JsonArray jsonArray) {
        HashMap<String, List<String>> dataElementCategoryComboRelationsBySection =
                new HashMap<>();

        for (JsonElement categoryComboElement : jsonArray) {
            JsonObject categoryComboObject = (JsonObject) categoryComboElement;
            String sectionName = getSectionNameFromJson(categoryComboObject);
            if (!(categoryComboObject.has(CATEGORY_COMBOS_KEY))) {
                dataElementCategoryComboRelationsBySection.put(sectionName, null);
                continue;
            }

            JsonArray categoryComboList = categoryComboObject.get(
                    CATEGORY_COMBOS_KEY).getAsJsonArray();

            for (JsonElement subCategoryComboElement : categoryComboList) {
                JsonObject subCategoryComboObject = (JsonObject) subCategoryComboElement;
                JsonArray categoryOptionComboList = subCategoryComboObject.get(
                        CATEGORY_OPTION_COMBOS_KEY).getAsJsonArray();
                List<String> categoryOptionComboUIds = new ArrayList<>();

                for (JsonElement categoryOptionComboElement : categoryOptionComboList) {
                    JsonObject categoryOptionComboObject = (JsonObject) categoryOptionComboElement;
                    categoryOptionComboUIds.add(
                            categoryOptionComboObject.get(ID_KEY).getAsString());
                }

                if (dataElementCategoryComboRelationsBySection.containsKey(sectionName)) {
                    dataElementCategoryComboRelationsBySection.put(sectionName,
                            categoryOptionComboUIds);
                } else {
                    dataElementCategoryComboRelationsBySection.put(sectionName,
                            categoryOptionComboUIds);
                }
            }
        }
        return dataElementCategoryComboRelationsBySection;
    }

    private static CategoryCombo addDefaultCategoryCombo(
            Context context, String responseBody) {
        String categoryComboUid = getDefaultCategoryComboUId(responseBody);
        if (categoryComboUid == null) {
            return null;
        }
        CategoryComboDownloadProcessor
                categoryComboDownloadProcessor = new CategoryComboDownloadProcessor();
        return categoryComboDownloadProcessor.download(context, categoryComboUid);
    }

    private static String getDefaultCategoryComboUId(String responseBody) {
        if (responseBody != null) {
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonObject jsonObject = jsonForm.getAsJsonObject(CATEGORY_COMBO_KEY);
                if (jsonObject == null) {
                    return null;
                }
                return jsonObject.get(ID_KEY).getAsString();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static HashMap<String, List<CategoryCombo>> parseCategoryComboDataElementRelations(
            String responseBody) {
        if (responseBody != null) {
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray(DATA_SET_KEY);
                if (jsonArray.size() == 0) {
                    return new HashMap<>();
                }
                return parseDataElementCategoryComboMap(jsonArray);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static HashMap<String, List<CategoryCombo>> parseDataElementCategoryComboMap(
            JsonArray jsonArray) {
        HashMap<String, CategoryCombo> localCategoryCombos = new HashMap<>();
        HashMap<String, List<CategoryCombo>> dataElementCategoryComboRelations = new HashMap<>();

        for (JsonElement item : jsonArray) {
            String dataElementUId = getDataElementUidFromJson(item);
            if (!((JsonObject) item).has(CATEGORY_COMBO_KEY)) {
                dataElementCategoryComboRelations.put(dataElementUId, null);
                continue;
            }

            JsonObject categoryCombo = ((JsonObject) item).get(
                    CATEGORY_COMBO_KEY).getAsJsonObject();
            String categoryComboUId = categoryCombo.get(ID_KEY).getAsString();

            if (dataElementCategoryComboRelations.containsKey(dataElementUId)) {
                addCategoryComboOnExistingDataElement(localCategoryCombos,
                        dataElementCategoryComboRelations, dataElementUId, categoryCombo,
                        categoryComboUId);
            } else {
                putDataElementAndCategoryComboIntoMap(localCategoryCombos,
                        dataElementCategoryComboRelations,
                        dataElementUId, categoryCombo, categoryComboUId);
            }
        }
        return dataElementCategoryComboRelations;
    }

    private static void putDataElementAndCategoryComboIntoMap(
            HashMap<String, CategoryCombo> localCategoryCombos,
            HashMap<String, List<CategoryCombo>> dataElementCategoryComboRelations,
            String dataElementUId, JsonObject categoryCombo, String categoryComboUId) {
        addCategoryOptionToLocalMap(localCategoryCombos, categoryCombo,
                categoryComboUId);

        List<CategoryCombo> categoryComboList = new ArrayList<>();
        categoryComboList.add(localCategoryCombos.get(categoryComboUId));
        dataElementCategoryComboRelations.put(dataElementUId, categoryComboList);
    }

    private static void addCategoryComboOnExistingDataElement(
            HashMap<String, CategoryCombo> localCategoryCombos,
            HashMap<String, List<CategoryCombo>> dataElementCategoryComboRelations,
            String dataElementUId, JsonObject categoryCombo, String categoryComboUId) {
        boolean isAdded = false;
        for (CategoryCombo categoryComboItem : dataElementCategoryComboRelations.get(
                dataElementUId)) {
            if (categoryComboItem.getId().equals(categoryComboUId)) {
                isAdded = true;
            }
        }
        if (!isAdded) {
            addCategoryOptionToLocalMap(localCategoryCombos, categoryCombo,
                    categoryComboUId);

            dataElementCategoryComboRelations.get(dataElementUId).add(
                    localCategoryCombos.get(categoryComboUId));
        }
    }

    private static void addCategoryOptionToLocalMap(
            HashMap<String, CategoryCombo> downloadedCategoryCombo, JsonObject categoryCombo,
            String categoryComboUId) {
        if (!downloadedCategoryCombo.containsKey(categoryComboUId)) {
            CategoryCombo newCategoryCombo = getCategoryComboFromJson(categoryCombo,
                    categoryComboUId);
            downloadedCategoryCombo.put(categoryComboUId, newCategoryCombo);
        }
    }

    private static String getSectionNameFromJson(JsonElement item) {
        return ((JsonObject) item).get(
                SECTION_NAME_KEY).getAsString();
    }

    private static String getDataElementUidFromJson(JsonElement item) {
        JsonObject dataElement = ((JsonObject) item).get(
                DATA_ELEMENT_KEY).getAsJsonObject();
        return dataElement.get(ID_KEY).getAsString();
    }

    private static CategoryCombo getCategoryComboFromJson(JsonObject categoryCombo,
            String categoryComboUid) {
        JsonArray categoryOptionCombos = categoryCombo.get(
                CATEGORY_OPTION_COMBOS_KEY).getAsJsonArray();
        List<String> categoryOptionComboUIds = new ArrayList<>();
        for (JsonElement categoryOptionCombo : categoryOptionCombos) {
            categoryOptionComboUIds.add(
                    ((JsonObject) categoryOptionCombo).get(ID_KEY).getAsString());
        }
        return new CategoryCombo(categoryComboUid, categoryOptionComboUIds);
    }
}