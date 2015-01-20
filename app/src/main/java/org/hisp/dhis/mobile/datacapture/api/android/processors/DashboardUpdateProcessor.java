package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
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
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;

public class DashboardUpdateProcessor extends AsyncTask<Void, Void, OnDashboardUpdateEvent> {
    private Context mContext;
    private DashboardUpdateEvent mEvent;

    public DashboardUpdateProcessor(Context context, DashboardUpdateEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("DashboardDeleteEvent must not be null");
        }

        mContext = context;
        mEvent = event;
    }

    private static void updateDashboard(Context context, int dashboardId,
                                        String dashboardName, State state) {
        Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI, dashboardId);
        ContentValues values = new ContentValues();
        values.put(DashboardColumns.NAME, dashboardName);
        values.put(DashboardColumns.STATE, state.toString());
        context.getContentResolver().update(uri, values, null, null);
    }

    private static DBItemHolder<Dashboard> readDashboard(Context context, int dashboardId) {
        Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI, dashboardId);
        Cursor cursor = context.getContentResolver().query(
                uri, DashboardHandler.PROJECTION, null, null, null
        );

        DBItemHolder<Dashboard> dashboard = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            dashboard = DashboardHandler.fromCursor(cursor);
            cursor.close();
        }

        return dashboard;
    }

    @Override
    protected OnDashboardUpdateEvent doInBackground(Void... params) {
        final DBItemHolder<Dashboard> dbItem = readDashboard(mContext, mEvent.getDataBaseId());
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardUpdateEvent event = new OnDashboardUpdateEvent();

        // change values in database first, including the state of record
        updateDashboard(mContext, dbItem.getDatabaseId(),
                mEvent.getName(), State.PUTTING);

        DHISManager.getInstance().updateDashboardName(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String s) {
                holder.setItem(s);
                holder.setResponse(response);

                // here updateDashboard is called only for updating
                // state of Dashboard record in database
                updateDashboard(mContext, dbItem.getDatabaseId(),
                        mEvent.getName(), State.GETTING);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, dbItem.getItem().getId(), mEvent.getName());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardUpdateEvent event) {
        BusProvider.getInstance().post(event);
    }
}
