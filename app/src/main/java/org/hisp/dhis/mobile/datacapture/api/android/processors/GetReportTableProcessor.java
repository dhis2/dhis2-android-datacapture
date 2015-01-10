package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.spdy.Header;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.GetReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnGotReportTableEvent;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetReportTableProcessor extends AsyncTask<Void, Void, OnGotReportTableEvent> {
    private static final long TIME_OUT = 1500;
    private Request mRequest;

    public GetReportTableProcessor(GetReportTableEvent event) {
        Request.Builder builder = new Request.Builder();

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", DHISManager.getInstance().getCredentials()));
        headers.add(new Header("Accept", "text/html"));

        mRequest = builder.url(event.getUrl()).get()
                .addHeader("Authorization", DHISManager.getInstance().getCredentials())
                .addHeader("Accept", "text/html")
                .build();
    }

    @Override
    protected OnGotReportTableEvent doInBackground(Void... params) {
        OnGotReportTableEvent event = new OnGotReportTableEvent();

        Response response = null;
        try {
            response = request();
            event.setReportTable(response.body().string());
        } catch (IOException networkException) {
            event.setApiException(APIException.networkError(mRequest.url().toString(),
                    networkException));
            return event;
        } catch (Exception unknownException) {
            event.setApiException(APIException.unexpectedError(mRequest.url().toString(),
                    unknownException));
            return event;
        }

        if (response == null) {
            event.setApiException(APIException.unexpectedError(mRequest.url().toString(),
                    new RuntimeException("Response cannot be null")));
            return event;
        }

        if (!isSuccessful(response.code())) {
            event.setApiException(APIException.httpError(mRequest.url().toString(), null));
            return event;
        }

        return event;
    }

    @Override
    protected void onPostExecute(OnGotReportTableEvent result) {
        BusProvider.getInstance().post(result);
    }

    private static boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    private Response request() throws IOException {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        return client.newCall(mRequest).execute();
    }
}
