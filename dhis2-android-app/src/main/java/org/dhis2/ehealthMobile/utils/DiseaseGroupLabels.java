package org.dhis2.ehealthMobile.utils;

import android.content.Context;

import org.dhis2.ehealthMobile.io.models.eidsr.Disease;

import java.util.Map;

/**
 * Created by george on 10/25/16.
 */

public class DiseaseGroupLabels {
    private final Map diseases;

    public DiseaseGroupLabels(Context context){
        this.diseases = DiseaseImporter.importDiseases(context);
    }

    public Boolean hasGroup(String id){
        Boolean hasLabel= false;
        assert diseases != null;
        Disease disease = (Disease) diseases.get(id);
        if(disease != null && !disease.getGroupLabel().equals("")){
            hasLabel = true;
        }
        return hasLabel;
    }

    public String getLabel(String id){
        String label = "";
        assert diseases != null;
        Disease disease = (Disease) diseases.get(id);
        if(disease != null){
            label = disease.getGroupLabel();
        }
        return label;
    }

    public int getGroupSize(String label){
        int count = 0;

        assert diseases != null;
        for (Object key : diseases.keySet()) {
            Disease disease = (Disease) diseases.get(key);
            if(disease.getGroupLabel().equals(label)){
                count++;
            }
        }
        return count;
    }
}
