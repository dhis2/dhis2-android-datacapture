package org.dhis2.mobile.processors;

import android.content.Context;
import android.support.annotation.NonNull;

import org.dhis2.mobile.io.holders.DataElementOperand;
import org.dhis2.mobile.io.holders.DataSetCategoryOptions;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.CategoryCombo;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkException;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataSetMetaData {

    public static String download(Context context,
            String formId, boolean oldApi) throws NetworkException {
        String url = buildUrl(context, formId, oldApi);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);
        if (response.getCode() >= 200 && response.getCode() < 300) {
            return response.getBody();
        }else{
            throw new NetworkException(response.getCode());
        }
    }

    public static String buildUrl(Context context, String formId, boolean olderApi) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.DATASET_VALUES_URL + "/" + formId + "?";
                if(olderApi) {
                    url += URLConstants.DATA_SET_DATA_ELEMENTS_META_DATA_API_25_PARAM;
                }else{
                    url += URLConstants.DATA_SET_DATA_ELEMENTS_META_DATA_PARAM;
                }

        return url;
    }


    public static void addCompulsoryDataElements(List<DataElementOperand> compulsoryUIds,
            Form form) {
        if (form == null && compulsoryUIds == null) {
            return;
        }
        for (DataElementOperand dataElementOperand : compulsoryUIds) {
            for (Group group : form.getGroups()) {
                addCompulsoryDataElements(dataElementOperand, group);
            }
        }
    }

    private static void addCompulsoryDataElements(DataElementOperand dataElementOperand,
            Group group) {
        for (Field field : group.getFields()) {
            if (field.getDataElement().equals(dataElementOperand.getDataElementUid())) {
                if (field.getCategoryOptionCombo().equals(
                        dataElementOperand.getCategoryOptionComboUid())) {
                    field.setCompulsory(true);
                }
            }
        }
    }


    public static Form removeFieldsWithInvalidCategoryOptionRelation(Form form,
            DataSetCategoryOptions dataSetCategoryOptions) {
        for (Group group : form.getGroups()) {
            ArrayList<Field> validFields = getValidatedFieldList(
                    group, dataSetCategoryOptions);
            group.setFields(validFields);
        }
        return form;
    }

    @NonNull
    private static ArrayList<Field> getValidatedFieldList(
            Group group, DataSetCategoryOptions dataSetCategoryOptions) {
        ArrayList<Field> validFields = new ArrayList<>();
        for (int i = 0; i < group.getFields().size(); i++) {
            Field field = group.getFields().get(i);
            checkIfAFieldIsValid(validFields, field, group.getLabel(), dataSetCategoryOptions);
        }
        return validFields;
    }

    private static void checkIfAFieldIsValid(
            ArrayList<Field> validFields, Field field, String section,
            DataSetCategoryOptions dataSetCategoryOptions) {
        HashMap<String, List<CategoryCombo>> categoryComboByDataElement =
                dataSetCategoryOptions.getCategoryComboByDataElement();
        HashMap<String, List<String>> categoryOptionComboBySection =
                dataSetCategoryOptions.getCategoryComboDataElementBySection();

        if (categoryComboByDataElement.containsKey(field.getDataElement())) {
            if (isValidField(field, section, dataSetCategoryOptions, categoryComboByDataElement,
                    categoryOptionComboBySection)) {
                validFields.add(field);
            }
        }
    }

    private static boolean isValidField(Field field, String section,
            DataSetCategoryOptions dataSetCategoryOptions,
            HashMap<String, List<CategoryCombo>> categoryComboByDataElement,
            HashMap<String, List<String>> categoryOptionComboBySection) {
        if (categoryComboByDataElement.get(field.getDataElement()) == null) {
            if (isValidDefaultField(field, dataSetCategoryOptions)) return true;
            if (isValidSectionField(field, section, categoryOptionComboBySection)) return true;
        } else if (isAValidCategoryOptionCombo(categoryComboByDataElement, field)) {
            return true;
        }
        return false;
    }

    private static boolean isValidDefaultField(Field field,
            DataSetCategoryOptions dataSetCategoryOptions) {
        if (dataSetCategoryOptions.getDefaultCategoryCombo().getCategoryOptionComboUIdList()
                .contains(
                        field.getCategoryOptionCombo())) {
            return true;
        }
        return false;
    }

    private static boolean isValidSectionField(Field field, String section,
            HashMap<String, List<String>> categoryOptionComboBySection) {
        if (categoryOptionComboBySection != null && categoryOptionComboBySection.containsKey(
                section)) {
            for (String validCategoryOptionUId : categoryOptionComboBySection.get(section)) {
                if (field.getCategoryOptionCombo().equals(validCategoryOptionUId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isAValidCategoryOptionCombo(
            HashMap<String, List<CategoryCombo>> dataElementCategoryOptionRelation, Field field) {
        for (CategoryCombo categoryCombo : dataElementCategoryOptionRelation.get(
                field.getDataElement())) {
            for (String categoryOptionComboUId : categoryCombo.getCategoryOptionComboUIdList()) {
                if (field.getCategoryOptionCombo().equals(categoryOptionComboUId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addDataInputPeriods(Form form, String jsonContent) throws ParsingException {
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            if (jsonContent.contains("dataInputPeriods")) {
                JSONArray jsonArray = jsonObject.getJSONArray("dataInputPeriods");
                List<String> dataInputPeriods = new ArrayList<>();

                String[] openingDate = new String[jsonArray.length()];
                String[] closingDate = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataInputPeriods.add(jsonArray.getJSONObject(i).getJSONObject(
                            "period").getString("id"));
                    if(jsonArray.getJSONObject(i).has("openingDate")){
                        openingDate[i] = jsonArray.getJSONObject(i).get("openingDate") != null ?
                                jsonArray.getJSONObject(i).get("openingDate").toString(): "";
                    }else{
                        openingDate[i] = "";
                    }
                    if(jsonArray.getJSONObject(i).has("closingDate")){
                        closingDate[i] = jsonArray.getJSONObject(i).get("closingDate") != null ?
                                jsonArray.getJSONObject(i).get("closingDate").toString(): "";
                    }else{
                        closingDate[i] = "";
                    }

                }

                validationDataInputPeriods(form, openingDate, closingDate, dataInputPeriods);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ParsingException("Error while parsing dataInputPeriods.");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void validationDataInputPeriods(Form form, String[] openingDate, String[] closingDate,
                                            List<String> dataInputPeriods) throws ParseException {
        List<String> dataInputPeriodsHelper = new ArrayList<>();
        Calendar calendarOpening = Calendar.getInstance();
        Calendar calendarClosing = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = formatter.format(currentCalendar.getTime());
        currentCalendar.setTime(formatter.parse(currentDate));

        for(int i = 0; i < openingDate.length; i++){
            String strOpening = openingDate[i].split("T")[0];
            String strClosing = closingDate[i].split("T")[0];
            boolean hasOpening = false;
            boolean hasClosing = false;
            if(!strOpening.equals("")){
                Date dateOpening = formatter.parse(strOpening);
                calendarOpening.setTime(dateOpening);
                hasOpening = true;
            }
            if(!strClosing.equals("")){
                Date dateClosing = formatter.parse(strClosing);
                calendarClosing.setTime(dateClosing);
                hasClosing = true;
            }
            if((!hasOpening || (hasOpening && (calendarOpening.compareTo(currentCalendar))<= 0))
                    && (!hasClosing || (hasClosing && (calendarClosing.compareTo(currentCalendar)>=0)))){
                dataInputPeriodsHelper.add(dataInputPeriods.get(i));
            }
        }
        form.getOptions().setDataInputPeriods(dataInputPeriodsHelper.toArray(new String[dataInputPeriodsHelper.size()]));

    }
}
