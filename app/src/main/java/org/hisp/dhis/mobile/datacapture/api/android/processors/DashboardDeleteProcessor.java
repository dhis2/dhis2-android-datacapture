package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

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
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;

public class DashboardDeleteProcessor extends AsyncTask<Void, Void, OnDashboardDeleteEvent> {
    private Context mContext;
    private int mDashboardDbId;

    public DashboardDeleteProcessor(Context context, DashboardDeleteEvent event) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("DashboardDeleteEvent must not be null");
        }

        mContext = context;
        mDashboardDbId = event.getDashboardDbId();
    }

    private DBItemHolder<Dashboard> readDashboard() {
        Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI, mDashboardDbId);
        Cursor cursor = mContext.getContentResolver().query(
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

    private void deleteDashboard() {
        Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI, mDashboardDbId);
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    protected OnDashboardDeleteEvent doInBackground(Void... params) {
        final DBItemHolder<Dashboard> dbItem = readDashboard();
        final ResponseHolder<String> holder = new ResponseHolder<>();
        final OnDashboardDeleteEvent event = new OnDashboardDeleteEvent();

        updateDashboardState(State.DELETING);
        DHISManager.getInstance().deleteDashboard(new ApiRequestCallback<String>() {
            @Override
            public void onSuccess(Response response, String string) {
                System.out.println("**** ****: " + string);
                holder.setItem(string);
                holder.setResponse(response);
                deleteDashboard();
            }

            @Override
            public void onFailure(APIException e) {
                e.printStackTrace();
                holder.setException(e);
            }
        }, dbItem.getItem().getId());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnDashboardDeleteEvent event) {
        BusProvider.getInstance().post(event);
    }

    private void updateDashboardState(State state) {
        Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI, mDashboardDbId);
        ContentValues values = new ContentValues();
        values.put(DashboardColumns.STATE, state.toString());
        mContext.getContentResolver().update(uri, values, null, null);
    }
}
