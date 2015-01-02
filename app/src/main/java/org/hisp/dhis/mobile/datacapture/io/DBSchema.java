package org.hisp.dhis.mobile.datacapture.io;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;

public interface DBSchema {

    /**
     * TODO Handle collisions with 'replace on conflict' operator
     */
    public static final String CREATE_DASHBOARD_TABLE = "CREATE TABLE " + DashboardColumns.TABLE_NAME + "(" +
            DashboardColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DashboardColumns.ID + " TEXT NOT NULL UNIQUE," +
            DashboardColumns.CREATED + " TEXT NOT NULL," +
            DashboardColumns.LAST_UPDATED + " TEXT NOT NULL," +
            DashboardColumns.STATE + " TEXT NOT NULL," +
            DashboardColumns.ACCESS + " TEXT NOT NULL," +
            DashboardColumns.NAME + " TEXT," +
            DashboardColumns.ITEM_COUNT + " INTEGER" + ")";

    public static final String DROP_DASHBOARD_TABLE = "DROP TABLE IF EXISTS " + DashboardColumns.TABLE_NAME;

    public static final String CREATE_DASHBOARD_ITEMS_TABLE = "CREATE TABLE " + DashboardItemColumns.TABLE_NAME + "(" +
            DashboardItemColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DashboardItemColumns.DASHBOARD_DB_ID + " INTEGER NOT NULL," +
            DashboardItemColumns.ID + " TEXT NOT NULL," +
            DashboardItemColumns.CREATED + " TEXT NOT NULL," +
            DashboardItemColumns.LAST_UPDATED + " TEXT NOT NULL," +
            DashboardItemColumns.STATE + " TEXT NOT NULL," +
            DashboardItemColumns.ACCESS + " TEXT NOT NULL," +
            DashboardItemColumns.TYPE + " TEXT NOT NULL," +
            DashboardItemColumns.CONTENT_COUNT + " INTEGER," +
            DashboardItemColumns.MESSAGES + " INTEGER," +
            DashboardItemColumns.USERS + " TEXT," +
            DashboardItemColumns.REPORTS + " TEXT," +
            DashboardItemColumns.RESOURCES + " TEXT," +
            DashboardItemColumns.REPORT_TABLES + " TEXT," +
            DashboardItemColumns.CHART + " TEXT," +
            DashboardItemColumns.EVENT_CHART + " TEXT," +
            DashboardItemColumns.REPORT_TABLE + " TEXT," +
            DashboardItemColumns.MAP + " TEXT," +
            " FOREIGN KEY" + "(" + DashboardItemColumns.DASHBOARD_DB_ID + ")" +
            " REFERENCES " + DashboardColumns.TABLE_NAME + "(" + DashboardColumns.DB_ID + ")" +
            " ON DELETE CASCADE" + ")";

    public static final String DROP_DASHBOARD_ITEMS_TABLE = "DROP TABLE IF EXISTS " + DashboardItemColumns.TABLE_NAME;
}
