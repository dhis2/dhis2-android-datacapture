package org.dhis2.mobile.processors;

import android.content.Context;

import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkException;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class DataSetApprovals {

    public static final String APPROVED_HERE = "APPROVED_HERE";
    public static final String APPROVED_ELSEWHERE = "APPROVED_ELSEWHERE";

    public static boolean download(Context context,
            String dataSet, String period, String ou) throws NetworkException {
        String url = buildUrl(context, dataSet, period, ou);
        System.out.println("Recovering dataSetApproval " + url);
        String credentials = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, credentials);
        if (response.getCode() >= 200 && response.getCode() < 300) {
             return parseApprovalState(response.getBody());
        }else if(response.getCode() == 409){
            System.out.println("Conflict recovering dataSetApproval in dataset "+ dataSet +" period "+ period + " ou "+ ou);
            return false;
        }else{
            throw new NetworkException(response.getCode());
        }
    }

    private static boolean parseApprovalState(String body) {
        if(body.contains("state")){
            String state = null;
            try {
                JSONObject jsonObject = new JSONObject(body);
                state = jsonObject.get("state").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(state.equals(APPROVED_HERE) || state.equals(APPROVED_ELSEWHERE)) {
                return true;
            }
        }
        return false;
    }


    public static String buildUrl(Context context, String dataSetId, String period, String ou) {
        String server = PrefUtils.getServerURL(context);
        return server + URLConstants.DATA_APPROVALS_URL + URLConstants.DATA_SET_PARAM + dataSetId
                + URLConstants.PERIOD_PARAM + period
                + URLConstants.ORG_UNIT_PARAM + ou;
    }
}
