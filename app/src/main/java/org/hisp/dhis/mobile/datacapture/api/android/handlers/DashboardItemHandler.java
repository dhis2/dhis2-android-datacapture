package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Access;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class DashboardItemHandler {

    public static final String[] PROJECTION = {
            DashboardItemColumns.DB_ID,
            DashboardItemColumns.ID,
            DashboardItemColumns.CREATED,
            DashboardItemColumns.LAST_UPDATED,
            DashboardItemColumns.ACCESS,
            DashboardItemColumns.TYPE,
            DashboardItemColumns.CONTENT_COUNT,
            DashboardItemColumns.MESSAGES,
            DashboardItemColumns.USERS,
            DashboardItemColumns.REPORTS,
            DashboardItemColumns.RESOURCES,
            DashboardItemColumns.REPORT_TABLES,
            DashboardItemColumns.CHART,
            DashboardItemColumns.EVENT_CHART,
            DashboardItemColumns.REPORT_TABLE,
            DashboardItemColumns.MAP
    };

    private static final int DATABASE_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int ACCESS = 4;
    private static final int TYPE = 5;
    private static final int CONTENT_COUNT = 6;
    private static final int MESSAGES = 7;
    private static final int USERS = 8;
    private static final int REPORTS = 9;
    private static final int RESOURCES = 10;
    private static final int REPORT_TABLES = 11;
    private static final int CHART = 12;
    private static final int EVENT_CHART = 13;
    private static final int REPORT_TABLE = 14;
    private static final int MAP = 15;

    private DashboardItemHandler() {
    }

    public static DBItemHolder<DashboardItem> fromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor object cannot be null");
        }

        Gson gson = new Gson();

        Access access = gson.fromJson(cursor.getString(ACCESS), Access.class);

        Type usersType = new TypeToken<List<User>>() { }.getType();
        Type dashboardType = new TypeToken<List<DashboardItemElement>>() { }.getType();

        List<User> users = gson.fromJson(cursor.getString(USERS), usersType);
        List<DashboardItemElement> reports = gson.fromJson(cursor.getString(REPORTS), dashboardType);
        List<DashboardItemElement> resources = gson.fromJson(cursor.getString(RESOURCES), dashboardType);
        List<DashboardItemElement> reportTables = gson.fromJson(cursor.getString(REPORT_TABLES), dashboardType);

        DashboardItemElement chart = gson.fromJson(cursor.getString(CHART), DashboardItemElement.class);
        DashboardItemElement eventChart = gson.fromJson(cursor.getString(EVENT_CHART), DashboardItemElement.class);
        DashboardItemElement reportTable = gson.fromJson(cursor.getString(REPORT_TABLE), DashboardItemElement.class);
        DashboardItemElement map = gson.fromJson(cursor.getString(MAP), DashboardItemElement.class);

        String created = cursor.getString(CREATED);
        String lastUpdated = cursor.getString(LAST_UPDATED);

        DashboardItem dashboardItem = new DashboardItem();

        dashboardItem.setId(cursor.getString(ID));
        dashboardItem.setCreated(created);
        dashboardItem.setLastUpdated(lastUpdated);
        dashboardItem.setAccess(access);
        dashboardItem.setType(cursor.getString(TYPE));
        dashboardItem.setContentCount(cursor.getInt(CONTENT_COUNT));
        dashboardItem.setMessages(cursor.getInt(MESSAGES) == 1);

        dashboardItem.setUsers(users);
        dashboardItem.setReports(reports);
        dashboardItem.setResources(resources);
        dashboardItem.setReportTables(reportTables);
        dashboardItem.setChart(chart);
        dashboardItem.setEventChart(eventChart);
        dashboardItem.setReportTable(reportTable);
        dashboardItem.setMap(map);

        DBItemHolder<DashboardItem> holder = new DBItemHolder<>();
        holder.setDataBaseId(cursor.getInt(DATABASE_ID));
        return holder;
    }

    public static ContentValues toContentValues(DashboardItem dashboardItem) {
        if (dashboardItem == null) {
            throw new IllegalArgumentException("DashboardItem object cannot be null");
        }

        Gson gson = new Gson();
        ContentValues values = new ContentValues();

        String created = dashboardItem.getCreated();
        String lastUpdated = dashboardItem.getLastUpdated();

        String access = gson.toJson(dashboardItem.getAccess());
        String users = gson.toJson(dashboardItem.getUsers());
        String reports = gson.toJson(dashboardItem.getReports());
        String resources = gson.toJson(dashboardItem.getResources());
        String reportTables = gson.toJson(dashboardItem.getReportTables());
        String chart = gson.toJson(dashboardItem.getChart());
        String eventChart = gson.toJson(dashboardItem.getEventChart());
        String reportTable = gson.toJson(dashboardItem.getReportTable());
        String map = gson.toJson(dashboardItem.getMap());

        values.put(DashboardItemColumns.ID, dashboardItem.getId());
        values.put(DashboardItemColumns.CREATED, created);
        values.put(DashboardItemColumns.LAST_UPDATED, lastUpdated);
        values.put(DashboardItemColumns.ACCESS, access);
        values.put(DashboardItemColumns.TYPE, dashboardItem.getType());
        values.put(DashboardItemColumns.CONTENT_COUNT, dashboardItem.getContentCount());
        values.put(DashboardItemColumns.MESSAGES, dashboardItem.isMessages() ? 1 : 0);
        values.put(DashboardItemColumns.USERS, users);
        values.put(DashboardItemColumns.REPORTS, reports);
        values.put(DashboardItemColumns.RESOURCES, resources);
        values.put(DashboardItemColumns.REPORT_TABLES, reportTables);
        values.put(DashboardItemColumns.CHART, chart);
        values.put(DashboardItemColumns.EVENT_CHART, eventChart);
        values.put(DashboardItemColumns.REPORT_TABLE, reportTable);
        values.put(DashboardItemColumns.MAP, map);

        return values;
    }

    public static ContentProviderOperation delete(DBItemHolder<DashboardItem> dashboardItem) {
        Uri uri = ContentUris.withAppendedId(
                DashboardItemColumns.CONTENT_URI, dashboardItem.getDatabaseId()
        );
        return ContentProviderOperation.newDelete(uri).build();
    }

    public static ContentProviderOperation update(DBItemHolder<DashboardItem> oldItem,
                                                  DashboardItem newItem) {
        if (isCorrect(newItem)) {
            Uri uri = ContentUris.withAppendedId(
                    DashboardItemColumns.CONTENT_URI, oldItem.getDatabaseId()
            );
            return ContentProviderOperation.newUpdate(uri)
                    .withValues(toContentValues(newItem)).build();
        } else {
            return null;
        }
    }

    public static ContentProviderOperation insert(DBItemHolder<Dashboard> dashboard,
                                                  DashboardItem dashboardItem) {
        if (isCorrect(dashboardItem)) {
            return ContentProviderOperation.newInsert(DashboardItemColumns.CONTENT_URI)
                    .withValue(DashboardItemColumns.DASHBOARD_DB_ID, dashboard.getDatabaseId())
                    .withValue(DashboardItemColumns.STATE, State.GETTING)
                    .withValues(toContentValues(dashboardItem))
                    .build();
        } else {
            return null;
        }
    }

    // This method is intended to work with an array of Dashboard items,
    // where dashboardIndex is the index of Dashboard to which we want to attach Dashboard Item
    public static ContentProviderOperation insertWithBackReference(int dashboardIndex,
                                                                   DashboardItem dashboardItem) {
        if (isCorrect(dashboardItem)) {
            return ContentProviderOperation.newInsert(DashboardItemColumns.CONTENT_URI)
                    .withValueBackReference(DashboardItemColumns.DB_ID, dashboardIndex)
                    .withValues(toContentValues(dashboardItem))
                    .withValue(DashboardItemColumns.STATE, State.GETTING.toString())
                    .build();
        } else {
            return null;
        }
    }

    private static boolean isCorrect(DashboardItem dashboardItem) {
        return (dashboardItem != null && dashboardItem.getAccess() != null &&
                !isEmpty(dashboardItem.getId()) && !isEmpty(dashboardItem.getCreated()) &&
                !isEmpty(dashboardItem.getLastUpdated()) && !isEmpty(dashboardItem.getType()));
    }

    public static Map<String, DashboardItem> toMap(List<DashboardItem> dashboardItems) {
        Map<String, DashboardItem> dashboardItemMap = new HashMap<>();
        for (DashboardItem dashboard : dashboardItems) {
            dashboardItemMap.put(dashboard.getId(), dashboard);
        }
        return dashboardItemMap;
    }
}
