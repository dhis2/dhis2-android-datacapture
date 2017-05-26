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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSetMetaData {

    public static String download(Context context,
            String formId) throws NetworkException {
        String url = buildUrl(context, formId);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);
        if (response.getCode() >= 200 && response.getCode() < 300) {
            return response.getBody();
        }else{
            throw new NetworkException(response.getCode());
        }
    }

    private static String buildUrl(Context context, String formId) {
        String server = PrefUtils.getServerURL(context);
        String url = server
                + URLConstants.DATASET_VALUES_URL + "/" + formId + "?"
                + URLConstants.DATA_SET_DATA_ELEMENTS_META_DATA_PARAM;

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
}
