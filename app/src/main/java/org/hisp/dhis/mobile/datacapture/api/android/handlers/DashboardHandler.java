package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class DashboardHandler {

    public static final String[] PROJECTION = {
            DashboardColumns.DB_ID,
            DashboardColumns.ID,
            DashboardColumns.CREATED,
            DashboardColumns.LAST_UPDATED,
            DashboardColumns.ACCESS,
            DashboardColumns.NAME,
            DashboardColumns.ITEM_COUNT
    };

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int NAME = 5;
    private static final int ITEM_COUNT = 6;

    private DashboardHandler() {
    }

    public static ContentValues toContentValues(Dashboard dashboard) {
        if (dashboard == null) {
            throw new IllegalArgumentException("Dashboard object cannot be null");
        }

        Gson gson = new Gson();
        ContentValues values = new ContentValues();

        String created = dashboard.getCreated();
        String lastUpdated = dashboard.getLastUpdated();
        String access = gson.toJson(dashboard.getAccess());

        values.put(DashboardColumns.ID, dashboard.getId());
        values.put(DashboardColumns.CREATED, created);
        values.put(DashboardColumns.LAST_UPDATED, lastUpdated);
        values.put(DashboardColumns.ACCESS, access);
        values.put(DashboardColumns.NAME, dashboard.getName());
        values.put(DashboardColumns.ITEM_COUNT, dashboard.getItemCount());

        return values;
    }

    public static DBItemHolder<Dashboard> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Gson gson = new Gson();
        Dashboard dashboard = new Dashboard();

        String created = cursor.getString(CREATED);
        String lastUpdated = cursor.getString(LAST_UPDATED);
        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);

        dashboard.setId(cursor.getString(ID));
        dashboard.setCreated(created);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setAccess(access);
        dashboard.setName(cursor.getString(NAME));
        dashboard.setItemCount(cursor.getInt(ITEM_COUNT));

        DBItemHolder<Dashboard> holder = new DBItemHolder<>();
        holder.setDataBaseId(cursor.getInt(DB_ID));
        holder.setItem(dashboard);
        return holder;
    }

    private static boolean isCorrect(Dashboard dashboard) {
        return (dashboard != null &&
                dashboard.getAccess() != null &&
                !isEmpty(dashboard.getId()) &&
                !isEmpty(dashboard.getCreated()) &&
                !isEmpty(dashboard.getLastUpdated()));
    }

    public static ContentProviderOperation delete(DBItemHolder<Dashboard> dashboard) {
        Uri uri = ContentUris.withAppendedId(
                DashboardColumns.CONTENT_URI, dashboard.getDatabaseId()
        );
        return ContentProviderOperation.newDelete(uri).build();
    }

    public static ContentProviderOperation update(DBItemHolder<Dashboard> oldDashboard,
                                                  Dashboard newDashboard) {
        if (isCorrect(newDashboard)) {
            Uri uri = ContentUris.withAppendedId(DashboardColumns.CONTENT_URI,
                    oldDashboard.getDatabaseId());
            return ContentProviderOperation.newUpdate(uri)
                    .withValues(DashboardHandler.toContentValues(newDashboard)).build();
        } else {
            return null;
        }

        // Update dashboard itself, then update child dashboard items
        /*
        ops.add(ContentProviderOperation.newUpdate(uri)
                .withValues(DashboardHandler.toContentValues(newDashboard)).build());
        ops.addAll(updateDashboardItems(oldDashboard, newDashboard));
        */
    }


    public static ContentProviderOperation insert(Dashboard dashboard) {
        if (isCorrect(dashboard)) {
            return ContentProviderOperation.newInsert(DashboardColumns.CONTENT_URI)
                    .withValue(DashboardColumns.STATE, State.GETTING.toString())
                    .withValues(DashboardHandler.toContentValues(dashboard))
                    .build();
        } else {
            return null;
        }

        /*
        ops.add(ContentProviderOperation.newInsert(DashboardColumns.CONTENT_URI)
                .withValue(DashboardColumns.STATE, State.GETTING.toString())
                .withValues(DashboardHandler.toContentValues(dashboard)).build());

        List<DashboardItem> dashboardItems = dashboard.getDashboardItems();
        if (dashboardItems == null || dashboardItems.size() <= 0) {
            return;
        }

        int dashboardInsertionIndex = ops.size() - 1;
        for (DashboardItem dashboardItem : dashboardItems) {
            System.out.println("Inserting dashboard: " + dashboardItem.getType());
            if (!isDashboardItemCorrect(dashboardItem)) {
                continue;
            }
            ContentValues values = DashboardItemHandler.toContentValues(dashboardItem);
            ops.add(ContentProviderOperation.newInsert(DBContract.DashboardItemColumns.CONTENT_URI)
                    .withValueBackReference(DBContract.DashboardItemColumns.DASHBOARD_DB_ID, dashboardInsertionIndex)
                    .withValue(DBContract.DashboardItemColumns.STATE, State.GETTING.toString())
                    .withValues(values)
                    .build());
        }
        */
    }

    public static Map<String, Dashboard> toMap(List<Dashboard> dashboardList) {
        Map<String, Dashboard> dashboardMap = new HashMap<>();
        for (Dashboard dashboard : dashboardList) {
            dashboardMap.put(dashboard.getId(), dashboard);
        }
        return dashboardMap;
    }
}
