package org.dhis2.mobile.processors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.holders.DataElementOperand;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;

import java.util.ArrayList;
import java.util.List;

public class DataElementOperandParser {

    public static List<DataElementOperand> parse(String jsonContent) throws ParsingException {
        List<DataElementOperand> dataElementOperandsList = parseToDataElementOperandsList(
                jsonContent);
        return dataElementOperandsList;
    }

    private static List<DataElementOperand> parseToDataElementOperandsList(String responseBody)
            throws ParsingException {
        if (responseBody != null) {
            List<DataElementOperand> dataElementOperandList = new ArrayList<>();
            JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
            JsonArray jsonArray = jsonForm.getAsJsonArray("compulsoryDataElementOperands");
            if (jsonArray.size() == 0) {
                return dataElementOperandList;
            }
            parseToDataElementOperandsList(dataElementOperandList, jsonArray);
            return dataElementOperandList;
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