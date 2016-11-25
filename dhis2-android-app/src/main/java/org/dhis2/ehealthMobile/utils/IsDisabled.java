package org.dhis2.ehealthMobile.utils;

/**
 * Created by george on 9/1/16.
 */


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.io.models.Field;
import org.dhis2.ehealthMobile.io.models.eidsr.Disease;

import java.util.Map;

/**
 * Utility class that checks whether a field is disabled or not
 */
public class IsDisabled {
    private final Map diseases;
    private final Context context;

    public IsDisabled(Context context, DatasetInfoHolder info){
        this.diseases = DiseaseImporter.importDiseases(context, info.getFormId());
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
            editText.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.ic_cancel));
            editText.setAlpha(0.2f);
        }else {
            editText.setAlpha(1f);
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
