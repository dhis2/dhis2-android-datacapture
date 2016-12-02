package org.dhis2.ehealthMobile.processors;

import android.content.Context;
import android.util.Log;

import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by george on 11/25/16.
 */

public class ConfigFileProcessor {
    public static final String TAG = ConfigFileProcessor.class.getSimpleName();
    public static final String COMPULSORY_DISEASES = "compulsoryDiseases";
    public static final String DISEASE_CONFIGS = "diseaseConfigs";
    private static final String ID = "id";

    public static void download(Context context){
        String url  = buildUrl(context);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            String compulsoryDiseases, diseaseConfigs;
            try {
                JSONObject obj = new JSONObject(response.getBody());
                Iterator<String> keys = obj.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    String formId = obj.getJSONObject(key).getString(ID);
                    compulsoryDiseases = obj.getJSONObject(key).getString(COMPULSORY_DISEASES);
                    diseaseConfigs = obj.getJSONObject(key).getString(DISEASE_CONFIGS);
                    PrefUtils.saveCompulsoryDiseases(context, formId, compulsoryDiseases);
                    PrefUtils.saveDiseaseConfigs(context, formId, diseaseConfigs);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private static String buildUrl(Context context){
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_STORE + "/" + URLConstants.CONFIG_URL;
    }

}
