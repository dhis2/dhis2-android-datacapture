package org.dhis2.mobile.utils;

import java.util.ArrayList;


/**
 * Created by george on 8/26/16.
 * Generates a unique key based off the data element id.
 * Meant to be used in relation with DHIS2 SMS commands
 */
public class KeyGenerator {
    private ArrayList<String> ids = new ArrayList<String>();

    public KeyGenerator(){

    }

    public String generate(String dataElementId,String categoryId, int stop){

        String generatedId;

        generatedId = dataElementId.substring(0,stop)+categoryId.substring(0,stop);

        if (ids.indexOf(generatedId) == -1){
            ids.add(generatedId);
        }else{
            stop = stop + 1;
            generatedId = generate(dataElementId, categoryId, stop);
        }

        return generatedId;
    }

}
