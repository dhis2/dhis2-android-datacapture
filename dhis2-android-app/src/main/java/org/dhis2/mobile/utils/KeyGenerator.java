package org.dhis2.mobile.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;

import java.lang.reflect.Array;
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

    public String parse(ArrayList<Group> groups, String reportName){
        String message = "";
        message += reportName+" ";


        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if(!field.getValue().equals("")) {

                    message += generate(field.getDataElement(), field.getCategoryOptionCombo(), 2)+",";
                    message += field.getValue()+",";

                }
            }
        }

        Log.d("Generated ids", ids+"");


        return message;
    }

    private String generate(String dataElementId,String categoryId, int stop){

        String generatedId;

        generatedId = dataElementId.substring(0,stop)+categoryId.substring(0,stop);

        if (ids.indexOf(generatedId) == -1){
            ids.add(generatedId);
        }else{
            generatedId = dataElementId.substring(0,stop+1)+categoryId.substring(0,stop);
        }

        return generatedId;
    }

    public static JsonArray check(JsonArray values){

        return values;
    }


}
