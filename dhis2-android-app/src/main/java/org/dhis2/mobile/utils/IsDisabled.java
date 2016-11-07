package org.dhis2.mobile.utils;

/**
 * Created by george on 9/1/16.
 */


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;

import org.dhis2.mobile.R;
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
    private final Context context;

    public IsDisabled(Context context){
        this.diseases = DiseaseImporter.importDiseases(context);
        this.context = context;
    }

    /**
     * Disables fields based on the disabled fields in the diseases json file
     * @param editText EditText
     * @param field Field {@see Field}
     *
     */
    public void setEnabled(final EditText editText, Field field){
        Boolean isEnabled = true;

        if(check(field)){
            isEnabled = false;
           editText.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color));
           // editText.setAlpha(0.2f);
        }else {
           // editText.setAlpha(1f);
            editText.setBackgroundResource(R.drawable.editbox_background);
        }

        editText.setEnabled(isEnabled);
    }

    public Boolean check(Field field){
        Boolean isDisabled = false;

        assert diseases != null;
        Disease disease = (Disease) diseases.get(field.getDataElement());
        if(diseases.containsKey(field.getDataElement()) && disease.getDisabledFields().contains(field.getCategoryOptionCombo())){
            isDisabled = true;

        }

        return isDisabled;
    }
}
