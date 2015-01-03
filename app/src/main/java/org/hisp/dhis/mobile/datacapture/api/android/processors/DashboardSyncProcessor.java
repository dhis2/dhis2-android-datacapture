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
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;
import org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter.deserializeDateTime;

// TODO Think about where you download Dashoards and DashboardItems (in updateDashboards and updateDashboardItems methods)
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

        List<DBItemHolder<Dashboard>> oldDashboards = readDashboards();
        Map<String, Dashboard> newDashboards = DashboardHandler.toMap(newDashboardList);

        if (oldDashboards != null && oldDashboards.size() > 0) {
            for (DBItemHolder<Dashboard> oldDashboard : oldDashboards) {
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
            // modify this part of code in order to use insertDashboard method

            Dashboard dashboard = newDashboards.get(key);
            ContentProviderOperation op = DashboardHandler.insert(dashboard);
            if (op != null) {
                Log.d(TAG, "Inserting new dashboard {id, name}: " + dashboard.getName() + " " + dashboard.getId());
                ops.add(op);
            }
        }

        return ops;
    }

    private List<ContentProviderOperation> insertDashboard(Dashboard dashboard) throws APIException {
        final List<ContentProviderOperation> ops = new ArrayList<>();
        final List<DashboardItem> dashboardItems = new ArrayList<>();

        ContentProviderOperation dashboardInsertOp = DashboardHandler.insert(dashboard);
        if (dashboardInsertOp == null) {
            return ops;
        }

        ops.add(dashboardInsertOp);
        if (dashboard.getDashboardItems() == null || dashboard.getDashboardItems().size() <= 0) {
            return ops;
        }

        for (DashboardItem shortDashItem : dashboard.getDashboardItems()) {
            dashboardItems.add(getDashboardItem(shortDashItem.getId()));
        }

        if (dashboardItems.size() <= 0) {
            return ops;
        }

        final int dashboardIndex = ops.size() - 1;
        for (DashboardItem dashboardItem : dashboardItems) {
            ContentProviderOperation op = DashboardItemHandler.insertWithBackReference(dashboardIndex, dashboardItem);
            if (op != null) {
                ops.add(op);
            }
        }

        return ops;
    }

    private List<ContentProviderOperation> updateDashboard(DBItemHolder<Dashboard> oldDashboard,
                                                           Dashboard newDashboard) throws APIException {
        List<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation dashboardUpdateOp = DashboardHandler.update(oldDashboard, newDashboard);

        if (dashboardUpdateOp == null) {
            return ops;
        }

        ops.add(dashboardUpdateOp);

        List<DBItemHolder<DashboardItem>> oldDashboardItems = readDashboardItems(oldDashboard.getDatabaseId());
        Map<String, DashboardItem> newDashboardItems = DashboardItemHandler.toMap(newDashboard.getDashboardItems());

        if (oldDashboardItems != null && oldDashboardItems.size() > 0) {
            for(DBItemHolder<DashboardItem> oldDashboardItem: oldDashboardItems) {
                DashboardItem newDashboardItem = newDashboardItems.get(oldDashboardItem.getItem().getId());

                if (newDashboardItem == null) {
                    Log.i(TAG, "Removing dashboard item {id}: " + oldDashboardItem.getItem().getId());
                    ops.add(deleteDashboardItem(oldDashboardItem));
                    continue;
                }

                DateTime oldItemLastUpdated = deserializeDateTime(oldDashboardItem.getItem().getLastUpdated());
                DateTime newItemLastUpdated = deserializeDateTime(newDashboardItem.getLastUpdated());

                if (newItemLastUpdated.isAfter(oldItemLastUpdated)) {
                    // newDashboardItem is actually a short version of DashboardItem from server,
                    // We need to download a larger version from API, and then persist it
                    DashboardItem newFullItem = getDashboardItem(oldDashboardItem.getItem().getId());
                    ContentProviderOperation op = updateDashboardItem(oldDashboardItem, newFullItem);
                    if (op != null) {
                        Log.d(TAG, "Updating dashboard item {id}: " + oldDashboard.getItem().getId());
                        ops.add(op);
                    }
                }

                newDashboardItems.remove(oldDashboardItem.getItem().getId());
            }
        }

        // implement inserting of dashboard items
        // remove unnecessary helper methods for dashboard items
        // refactor code with APIException in mind
        // Clean up the code
        // Test it


        // read old dashboard items from database
        // start comparing them to new ones
        // download new dashboard item if needed
        return ops;
    }

    private ContentProviderOperation deleteDashboard(DBItemHolder<Dashboard> dashboard) {
        return DashboardHandler.delete(dashboard);
    }

    private ContentProviderOperation deleteDashboardItem(DBItemHolder<DashboardItem> item) {
        return DashboardItemHandler.delete(item);
    }

    private ContentProviderOperation updateDashboardItem(DBItemHolder<DashboardItem> oldItem,
                                                         DashboardItem newItem) {
        return DashboardItemHandler.update(oldItem, newItem);
    }

    private List<DBItemHolder<Dashboard>> readDashboards() {
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

    private List<DBItemHolder<DashboardItem>> readDashboardItems(int dashboardId) {
        final String SELECTION = DashboardItemColumns.DASHBOARD_DB_ID + " = " + dashboardId + " AND " +
                DashboardItemColumns.STATE + " = " + '"' + State.GETTING.toString() + '"';
        Cursor cursor = mContext.getContentResolver().query(
                DashboardItemColumns.CONTENT_URI, DashboardItemHandler.PROJECTION, SELECTION, null, null
        );

        List<DBItemHolder<DashboardItem>> items = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                items.add(DashboardItemHandler.fromCursor(cursor));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    private static DashboardItem getDashboardItem(String id) throws APIException {
        final ResponseHolder<DashboardItem> holder = new ResponseHolder<>();

        DHISManager.getInstance().getDashboardItem(new ApiRequestCallback<DashboardItem>() {
            @Override
            public void onSuccess(Response response, DashboardItem item) {
                holder.setResponse(response);
                holder.setItem(item);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, id);

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
    }

    private static List<Dashboard> getDashboards() throws APIException {
        final ResponseHolder<List<Dashboard>> holder = new ResponseHolder<>();

        DHISManager.getInstance().getDashboards(new ApiRequestCallback<List<Dashboard>>() {
            @Override
            public void onSuccess(Response response, List<Dashboard> dashboards) {
                holder.setResponse(response);
                holder.setItem(dashboards);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        });

        if (holder.getException() != null) {
            throw holder.getException();
        } else {
            return holder.getItem();
        }
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