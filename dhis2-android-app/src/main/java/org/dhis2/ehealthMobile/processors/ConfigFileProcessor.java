package org.dhis2.ehealthMobile.processors;

import android.content.Context;
import android.util.Log;

import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by george on 11/25/16.
 */

public class ConfigFileProcessor {
    public static final String TAG = ConfigFileProcessor.class.getSimpleName();
    public static String COMPULSORY_DISEASES = "compulsoryDiseases";
    public static String DISEASE_CONFIGS = "diseaseConfigs";

    public static void download(Context context, String formId){
        String url  = buildUrl(context, formId);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            String compulsoryDiseases, diseaseConfigs;
            try {
                JSONObject obj = new JSONObject(response.getBody());
                compulsoryDiseases = obj.getString(COMPULSORY_DISEASES);
                diseaseConfigs = obj.getString(DISEASE_CONFIGS);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                return;
            }
            PrefUtils.saveCompulsoryDiseases(context, formId, compulsoryDiseases);
            PrefUtils.saveDiseaseConfigs(context, formId, diseaseConfigs);
        }
    }

    private static String buildUrl(Context context, String formId){
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_STORE + "/" + formId + "/" + URLConstants.CONFIG_URL;
    }

}
