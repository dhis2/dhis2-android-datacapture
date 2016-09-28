package org.dhis2.mobile.utils;

/**
 * Created by george on 9/1/16.
 */


import android.content.Context;
import android.widget.EditText;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.eidsr.Disease;

import java.util.Arrays;
import java.util.Map;

/**
 * Utility class that checks whether a field is disabled or not
 */
public class IsDisabled {

    /**
     * Disables fields based on the disabled fields in the diseases json file
     * @param editText EditText
     * @param field Field {@see Field}
     * @param context Context
     *
     */
    public static void setEnabled(final EditText editText, Field field, Context context){
        Boolean isEnabled = true;

        Map diseases = DiseaseImporter.importDiseases(context);
        assert diseases != null;
        Disease disease = (Disease) diseases.get(field.getDataElement());
        if(diseases.containsKey(field.getDataElement()) && disease.getDisabledFields().contains(field.getCategoryOptionCombo())){
            isEnabled = false;
        }

        editText.setEnabled(isEnabled);
    }
}
