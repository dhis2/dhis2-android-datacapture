package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardCreateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;

public class DashboardCreateProcessor extends AsyncTask<Void, Void, OnDashboardCreateEvent> {
    private DashboardCreateEvent mEvent;

    public DashboardCreateProcessor(DashboardCreateEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("DashboardCreateEvent must not be null");
        }
        mEvent = event;
    }

    @Override
    protected OnDashboardCreateEvent doInBackground(Void... params) {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardCreateEvent event = new OnDashboardCreateEvent();

        DHISManager.getInstance().postDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, mEvent.getDashboardName());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardCreateEvent event) {
        BusProvider.getInstance().post(event);
    }
}
