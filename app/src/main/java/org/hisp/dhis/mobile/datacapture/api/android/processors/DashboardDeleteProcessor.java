package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.android.events.DashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardDeleteEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.io.DBContract;

import java.util.ArrayList;

public class DashboardDeleteProcessor extends AsyncTask<Void, Void, OnDashboardDeleteEvent> {
    private DashboardDeleteEvent mEvent;
    private Context mContext;

    public DashboardDeleteProcessor(Context context, DashboardDeleteEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("DashboardDeleteEvent must not be null");
        }

        mContext = context;
        mEvent = event;
    }

    @Override
    protected OnDashboardDeleteEvent doInBackground(Void... params) {
        DBItemHolder<Dashboard> dbItem = mEvent.getDashboard();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(DashboardHandler.update(dbItem, dbItem.getItem(), State.DELETING));

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        return new OnDashboardDeleteEvent();
    }

    @Override
    protected void onPostExecute(OnDashboardDeleteEvent event) {
        BusProvider.getInstance().post(event);
    }
}
