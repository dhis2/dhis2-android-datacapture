package org.dhis2.ehealthMobile.processors;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.ui.activities.DataEntryActivity;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jasper on 10/31/16.
 */

public class SMSNumberProcessor {
    public static final String TAG = SMSNumberProcessor.class.getSimpleName();

    public static String SMS_NUMBER = "smsNumber";

    public static void download(Context context){
        String url  = buildUrl(context);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            String smsNumber;
            try {
                JSONObject obj = new JSONObject(response.getBody());
                smsNumber = obj.getString(SMS_NUMBER);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            PrefUtils.saveSmsNumber(context, smsNumber);
        }
    }

    private static String buildUrl(Context context){
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_STORE + "/" + URLConstants.SMS_NUMBER_URL ;
    }
}
