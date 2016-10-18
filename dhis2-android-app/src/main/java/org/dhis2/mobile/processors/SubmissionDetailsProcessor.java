package org.dhis2.mobile.processors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.utils.PrefUtils;

/**
 * Created by george on 10/14/16.
 */

public class SubmissionDetailsProcessor {
    public static String SUBMISSION_DETAILS = "submissionDetails";

    public static void download(Context context, DatasetInfoHolder info){
        String url  = buildUrl(context, info);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);

        if (response.getCode() >= 200 && response.getCode() < 300) {
            String completionDate = getCompletionDate(response.getBody());
            Intent intent  = new Intent(DataEntryActivity.TAG);
            intent.putExtra(Response.CODE, response.getCode());
            intent.putExtra(SUBMISSION_DETAILS, completionDate);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static String buildUrl(Context context, DatasetInfoHolder info){
         String server = PrefUtils.getServerURL(context);

         return server + URLConstants.DATASET_UPLOAD_URL+ "?"+URLConstants.DATASET_PARAM + info.getFormId()
                + "&" + URLConstants.ORG_UNIT_PARAM + info.getOrgUnitId() + URLConstants.PERIOD_PARAM_2 + info.getPeriod()
                + "&" + URLConstants.LIMIT_PARAM+ "0";

    }

    private static String getCompletionDate(String responseBody){
        String completeDate = "completeDate";

        if (responseBody != null && responseBody.contains(completeDate)) {
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(responseBody);
                return jsonForm.get(completeDate).getAsString();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
