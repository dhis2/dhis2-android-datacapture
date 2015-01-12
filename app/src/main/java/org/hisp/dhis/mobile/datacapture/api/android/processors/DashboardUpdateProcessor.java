package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardUpdateEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract;

import java.util.ArrayList;

public class DashboardUpdateProcessor extends AsyncTask<Void, Void, OnDashboardUpdateEvent> {
    private DBItemHolder<Dashboard> mDbItem;
    private Dashboard mDashboard;
    private Context mContext;

    public DashboardUpdateProcessor(Context context, DashboardUpdateEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("DashboardDeleteEvent must not be null");
        }

        mContext = context;
        mDbItem = event.getDbItem();
        mDashboard = event.getDashboard();
    }

    @Override
    protected OnDashboardUpdateEvent doInBackground(Void... params) {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardUpdateEvent event = new OnDashboardUpdateEvent();
        updateDashboard(State.PUTTING);

        DHISManager.getInstance().updateDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);
                updateDashboard(State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, mDashboard);

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardUpdateEvent event) {
        BusProvider.getInstance().post(event);
    }

    private void updateDashboard(State state) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(DashboardHandler.update(mDbItem, mDashboard, state));

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
