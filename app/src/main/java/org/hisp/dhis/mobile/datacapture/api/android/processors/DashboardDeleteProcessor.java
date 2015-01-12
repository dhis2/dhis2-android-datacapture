package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardDeleteEvent;
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

public class DashboardDeleteProcessor extends AsyncTask<Void, Void, OnDashboardDeleteEvent> {
    private DBItemHolder<Dashboard> mDbItem;
    private Context mContext;

    public DashboardDeleteProcessor(Context context, DashboardDeleteEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("DashboardDeleteEvent must not be null");
        }

        mContext = context;
        mDbItem = event.getDashboard();
    }

    @Override
    protected OnDashboardDeleteEvent doInBackground(Void... params) {
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardDeleteEvent event = new OnDashboardDeleteEvent();

        updateDashboardState(State.DELETING);

        DHISManager.getInstance().deleteDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String string) {
                holder.setItem(string);
                holder.setResponse(response);
                deleteDashboard();
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, mDbItem.getItem().getId());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardDeleteEvent event) {
        BusProvider.getInstance().post(event);
    }

    private void updateDashboardState(State state) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(DashboardHandler.update(mDbItem, mDbItem.getItem(), state));

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    private void deleteDashboard() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(DashboardHandler.delete(mDbItem));

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
