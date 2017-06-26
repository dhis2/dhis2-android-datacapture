package org.dhis2.mobile.processors;


import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.gson.JsonObject;

import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.PrefUtils;

import java.net.HttpURLConnection;

public class ServerInfoProcessor {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";


    public static boolean pullServerInfo(Context context, String server,
            String creds) {

        if (context == null || server == null
                || creds == null) {
            Log.i(ServerInfoProcessor.class.getName(), "Pull server info fail");
            return false;
        }

        String url = prepareUrl(server, creds);

        Response resp = tryToGetServerInfo(url, creds);

        // Checking validity of server URL
        if (!URLUtil.isValidUrl(url)) {
            return false;
        }

        // If credentials and address is correct,
        // user information will be saved to internal storage
        if (!HTTPClient.isError(resp.getCode())) {
            String version;
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(resp);
                version = jsonForm.get("version").getAsString();
                if(version==null || version.equals("")){
                    throw new ParsingException("Version not found");
                }
            } catch (ParsingException e) {
                e.printStackTrace();
                return false;
            }
            PrefUtils.initServerData(context, version);
        }
        return true;
    }

    private static String prepareUrl(String initialUrl, String creds) {
        if (initialUrl.contains(HTTPS) || initialUrl.contains(HTTP)) {
            return initialUrl;
        }

        // try to use https
        Response response = tryToGetServerInfo(HTTPS + initialUrl, creds);
        if (response.getCode() != HttpURLConnection.HTTP_MOVED_PERM) {
            return HTTPS + initialUrl;
        } else {
            return HTTP + initialUrl;
        }
    }

    private static Response tryToGetServerInfo(String server, String creds) {
        String url = server + URLConstants.API_SERVER_INFO;
        return HTTPClient.get(url, creds);
    }
}
