package org.dhis2.ehealthMobile.utils;

import android.content.Context;

import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.io.models.eidsr.Disease;

import java.util.Map;

/**
 * Created by george on 9/27/16.
 */

public class IsAdditionalDisease {
    private final Map diseases;
    public IsAdditionalDisease(Context context, DatasetInfoHolder info){
        this.diseases = DiseaseImporter.importDiseases(context, info.getFormId());
    }

    public Boolean check(String id){
        Boolean isAdditional = false;
        assert diseases != null;
        Disease disease = (Disease) diseases.get(id);
        if(disease != null && disease.isAdditionalDisease()){
            isAdditional = true;
        }
        return isAdditional;
    }
}
