package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDashboardsSyncedEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter.deserializeDateTime;

public class DashboardSyncProcessor extends AsyncTask<Void, Void, OnDashboardsSyncedEvent> {
    private static final String TAG = DashboardSyncProcessor.class.getSimpleName();
    private Context mContext;

    public DashboardSyncProcessor(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }

        mContext = context;
    }

    private ArrayList<ContentProviderOperation> updateDashboards(List<Dashboard> newDashboardList) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        List<DBItemHolder<Dashboard>> oldDashboards = readDashboardsFromDB();
        Map<String, Dashboard> newDashboards = DashboardHandler.toMap(newDashboardList);

        if (oldDashboards != null && oldDashboards.size() > 0) {
            for (DBItemHolder<Dashboard> oldDashboard: oldDashboards) {
                Dashboard newDashboard = newDashboards.get(oldDashboard.getItem().getId());

                // if dashboard which we have recently got from server is empty,
                // it means it has been removed
                if (newDashboard == null) {
                    Log.d(TAG, "Removing dashboard {id, name}: " +
                            oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                    ops.add(DashboardHandler.delete(oldDashboard));
                    continue;
                }

                DateTime newLastUpdated = deserializeDateTime(newDashboard.getLastUpdated());
                DateTime oldLastUpdated = deserializeDateTime(oldDashboard.getItem().getLastUpdated());

                if (newLastUpdated.isAfter(oldLastUpdated)) {
                    ContentProviderOperation op = DashboardHandler.update(oldDashboard, newDashboard);
                    if (op != null) {
                        Log.d(TAG, "Updating dashboard {id, name}: " +
                                oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                        ops.add(op);
                    }
                }

                newDashboards.remove(oldDashboard.getItem().getId());
            }
        }


        // Inserting new items here
        for (String key : newDashboards.keySet()) {
            Dashboard dashboard = newDashboards.get(key);
            ContentProviderOperation op = DashboardHandler.insert(dashboard);
            if (op != null) {
                Log.d(TAG, "Inserting new dashboard {id, name}: " + dashboard.getName() + " " + dashboard.getId());
                ops.add(op);
            }
        }

        return ops;
    }

    private List<DBItemHolder<Dashboard>> readDashboardsFromDB() {
        final String SELECTION = DashboardColumns.STATE + " = " + '"' + State.GETTING.toString() + '"';
        Cursor cursor = mContext.getContentResolver().query(
                DashboardColumns.CONTENT_URI, DashboardHandler.PROJECTION, SELECTION, null, null
        );

        List<DBItemHolder<Dashboard>> dashboards = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                dashboards.add(DashboardHandler.fromCursor(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return dashboards;
    }

    @Override
    protected OnDashboardsSyncedEvent doInBackground(Void... params) {
        return syncDashboards();
    }

    @Override
    protected void onPostExecute(OnDashboardsSyncedEvent event) {
        BusProvider.getInstance().post(event);
    }

    private OnDashboardsSyncedEvent syncDashboards() {
        final OnDashboardsSyncedEvent event = new OnDashboardsSyncedEvent();
        DHISManager.getInstance().getDashboards(new ApiRequestCallback<List<Dashboard>>() {
            @Override
            public void onSuccess(Response response, List<Dashboard> dashboardList) {
                ArrayList<ContentProviderOperation> ops = updateDashboards(dashboardList);
                try {
                    mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(APIException e) {
                event.setException(e);
            }
        });

        return event;
    }
}







    /*
    private List<ContentProviderOperation> updateDashboards(List<Dashboard> dashboardList) {
        List<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, Dashboard> newDashboardsMap = DashboardHandler.toMap(dashboardList);

        final String SELECTION = DashboardColumns.STATE + " = " + '"' + State.GETTING.toString() + '"';
        Cursor cursor = mContext.getContentResolver().query(DashboardColumns.CONTENT_URI,
                DashboardHandler.PROJECTION, SELECTION, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                DBItemHolder<Dashboard> oldDashboard = DashboardHandler.fromCursor(cursor);
                Dashboard newDashboard = newDashboardsMap.get(oldDashboard.getItem().getId());

                if (newDashboard == null) {
                    Log.d(TAG, "Removing dashboard {id, name}: " +
                            oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                    putDashboardDeleteOperation(ops, oldDashboard);
                    continue;
                }

                DateTime newLastUpdated = deserializeDateTime(newDashboard.getLastUpdated());
                DateTime oldLastUpdated = deserializeDateTime(oldDashboard.getItem().getLastUpdated());

                if (newLastUpdated.isAfter(oldLastUpdated)) {
                    Log.d(TAG, "Updating dashboard {id, name}: " +
                            oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                    putDashboardUpdateOperation(ops, oldDashboard, newDashboard);
                }

                newDashboardsMap.remove(oldDashboard.getItem().getId());
            } while (cursor.moveToNext());
        }

        // Inserting new items here
        for (String key : newDashboardsMap.keySet()) {
            Dashboard dashboard = newDashboardsMap.get(key);
            Log.d(TAG, "Inserting new dashboard {id, name}: " + dashboard.getName() + " " + dashboard.getId());
            putDashboardInsertOperation(ops, dashboard);
        }

        return ops;
    }
    */

//Iterator<DBItemHolder<Dashboard>> iterator = oldDashboards.iterator();
            /*do {
                DBItemHolder<Dashboard> oldDashboard = iterator.next();
                Dashboard newDashboard = newDashboards.get(oldDashboard.getItem().getId());

                // if dashboard which we have recently got from server is empty,
                // it means it has been removed
                if (newDashboard == null) {
                    Log.d(TAG, "Removing dashboard {id, name}: " +
                            oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                    ops.add(DashboardHandler.delete(oldDashboard));
                    continue;
                }

                DateTime newLastUpdated = deserializeDateTime(newDashboard.getLastUpdated());
                DateTime oldLastUpdated = deserializeDateTime(oldDashboard.getItem().getLastUpdated());

                if (newLastUpdated.isAfter(oldLastUpdated)) {
                    ContentProviderOperation op = DashboardHandler.update(oldDashboard, newDashboard);
                    if (op != null) {
                        Log.d(TAG, "Updating dashboard {id, name}: " +
                                oldDashboard.getItem().getId() + " : " + oldDashboard.getItem().getName());
                        ops.add(op);
                    }
                }

                newDashboards.remove(oldDashboard.getItem().getId());
            } while (iterator.hasNext()); */