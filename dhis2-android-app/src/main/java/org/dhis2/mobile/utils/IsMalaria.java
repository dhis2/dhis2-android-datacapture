package org.dhis2.mobile.utils;

import org.dhis2.mobile.io.Constants;

/**
 * Created by george on 10/24/16.
 */

public class IsMalaria {

    public static Boolean check(String id){
        Boolean isMalaria = false;
        if(id.equals(Constants.TOTAL_CLINICAL_MALARIA_CASES) || id.equals(Constants.TOTAL_MALARIA_TESTED)
                || id.equals(Constants.TOTAL_MALARIA_POSITIVE)){
            isMalaria = true;
        }
        return isMalaria;
    }
}
