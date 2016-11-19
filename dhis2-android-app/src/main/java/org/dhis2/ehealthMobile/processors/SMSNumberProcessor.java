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

/**
 * Created by jasper on 10/31/16.
 */

public class SMSNumberProcessor {

    public static String SMS_NUMBER = "smsNumber";

    public static void download(Context context){
        Log.d("SMSNumerProcessor", "downloading sms number...");

        String url  = buildUrl(context);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            saveDataset(context, response.getBody());
//            Intent intent  = new Intent(DataEntryActivity.TAG);
//            intent.putExtra(Response.CODE, response.getCode());
//            intent.putExtra(SMS_NUMBER, response.getBody());
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static String buildUrl(Context context){
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_STORE + "/" + URLConstants.SMS_NUMBER_URL ;
    }

    private static void saveDataset(Context context, String data) {
        Log.d("SMSNumerProcessor", "saveDataset: data is " + data);
        PrefUtils.saveSmsNumber(context, data);
    }
}
