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
    private final Map diseases;

    public IsDisabled(Context context){
        this.diseases = DiseaseImporter.importDiseases(context);
    }

    /**
     * Disables fields based on the disabled fields in the diseases json file
     * @param editText EditText
     * @param field Field {@see Field}
     *
     */
    public void setEnabled(final EditText editText, Field field){
        Boolean isEnabled = true;

        assert diseases != null;
        Disease disease = (Disease) diseases.get(field.getDataElement());
        if(diseases.containsKey(field.getDataElement()) && disease.getDisabledFields().contains(field.getCategoryOptionCombo())){
            isEnabled = false;
        }

        editText.setEnabled(isEnabled);
    }

    public Boolean check(Field field){
        Boolean isEnabled = false;

        assert diseases != null;
        Disease disease = (Disease) diseases.get(field.getDataElement());
        if(diseases.containsKey(field.getDataElement()) && disease.getDisabledFields().contains(field.getCategoryOptionCombo())){
            isEnabled = true;
        }

        return isEnabled;
    }
}
