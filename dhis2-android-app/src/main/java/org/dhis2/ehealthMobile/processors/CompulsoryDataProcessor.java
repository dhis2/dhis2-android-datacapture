package org.dhis2.ehealthMobile.processors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.network.URLConstants;
import org.dhis2.ehealthMobile.ui.activities.DataEntryActivity;
import org.dhis2.ehealthMobile.utils.PrefUtils;

/**
 * Created by george on 10/31/16.
 */

public class CompulsoryDataProcessor {

    public static String COMPULSORY_DATA = "compulsoryData";

    public static void download(Context context, DatasetInfoHolder info){
        String url  = buildUrl(context, info);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            saveDataset(context, response.getBody(), info);
            Intent intent  = new Intent(DataEntryActivity.TAG);
            intent.putExtra(Response.CODE, response.getCode());
            intent.putExtra(COMPULSORY_DATA, response.getBody());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static String buildUrl(Context context, DatasetInfoHolder info){
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_STORE + "/" +info.getFormId() + "/" + URLConstants.COMPULSORY_URL ;
    }

    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        PrefUtils.saveCompulsoryData(context, info.getFormId(), data);
    }
}
