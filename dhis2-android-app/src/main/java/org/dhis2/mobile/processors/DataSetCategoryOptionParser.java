package org.dhis2.mobile.processors;

import static org.dhis2.mobile.network.Response.CATEGORY_COMBOS_KEY;
import static org.dhis2.mobile.network.Response.CATEGORY_COMBO_KEY;
import static org.dhis2.mobile.network.Response.CATEGORY_OPTION_COMBOS_KEY;
import static org.dhis2.mobile.network.Response.DATA_ELEMENT_KEY;
import static org.dhis2.mobile.network.Response.DATA_SET_KEY;
import static org.dhis2.mobile.network.Response.ID_KEY;
import static org.dhis2.mobile.network.Response.SECTIONS_KEY;
import static org.dhis2.mobile.network.Response.SECTION_NAME_KEY;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.holders.DataSetCategoryOptions;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.CategoryCombo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSetCategoryOptionParser {

    public static DataSetCategoryOptions parse(String jsonContent) throws ParsingException {
        DataSetCategoryOptions dataSetCategoryOptions = new DataSetCategoryOptions();
        dataSetCategoryOptions.setDefaultCategoryCombo(addDefaultCategoryCombo(jsonContent));
        dataSetCategoryOptions.setCategoryComboByDataElement(parseCategoryComboDataElementRelations(
                jsonContent));
        dataSetCategoryOptions.setCategoryOptionComboUIdsBySection(
                parseCategoryComboBySectionRelations(
                        jsonContent));
        return dataSetCategoryOptions;
    }

    private static HashMap<String, List<String>> parseCategoryComboBySectionRelations(
            String responseBody) throws ParsingException {
        if (responseBody != null) {
            JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
            JsonArray jsonArray = jsonForm.getAsJsonArray(SECTIONS_KEY);
            if (jsonArray.size() == 0) {
                return new HashMap<>();
            }
            return parseDataElementCategoryComboSectionMap(jsonArray);
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

    private static CategoryCombo addDefaultCategoryCombo(String responseBody)
            throws ParsingException {
        CategoryCombo categoryCombo = getDefaultCategoryComboUId(responseBody);
        if (categoryCombo == null) {
            throw new ParsingException("Wrong params");
        }
        return categoryCombo;
    }

    private static CategoryCombo getDefaultCategoryComboUId(String responseBody)
            throws ParsingException {
        if (responseBody != null) {
            JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
            JsonObject jsonObject = jsonForm.getAsJsonObject(CATEGORY_COMBO_KEY);
            if (jsonObject == null) {
                return null;
            }
            String categoryComboUid = jsonObject.get(ID_KEY).getAsString();
            CategoryCombo categoryCombo = new CategoryCombo();
            List<String> categoryOptionComboUIds = parseCategoryOptionCombo(jsonObject);
            categoryCombo.setId(categoryComboUid);
            categoryCombo.setCategoryOptionComboUIdList(categoryOptionComboUIds);
            return categoryCombo;
        }
        return null;
    }

    private static List<String> parseCategoryOptionCombo(JsonObject jsonObject) {
        List<String> categoryOptionComboUIds = new ArrayList<>();
        if (jsonObject != null) {
            try {
                JsonArray jsonArray = jsonObject.getAsJsonArray(
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
            }
        }
        return categoryOptionComboUIds;
    }

    private static HashMap<String, List<CategoryCombo>> parseCategoryComboDataElementRelations(
            String responseBody) throws ParsingException {
        if (responseBody != null) {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                JsonArray jsonArray = jsonForm.getAsJsonArray(DATA_SET_KEY);
                if (jsonArray.size() == 0) {
                    return new HashMap<>();
                }
                return parseDataElementCategoryComboMap(jsonArray);
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