package org.hisp.dhis.mobile.datacapture.io;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;

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

    public static final String CREATE_INTERPRETATIONS_TABLE = "CREATE TABLE " + InterpretationColumns.TABLE_NAME + "(" +
            InterpretationColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            InterpretationColumns.ID + " TEXT NOT NULL UNIQUE," +
            InterpretationColumns.CREATED + " TEXT NOT NULL," +
            InterpretationColumns.LAST_UPDATED + " TEXT NOT NULL," +
            InterpretationColumns.STATE + " TEXT NOT NULL," +
            InterpretationColumns.ACCESS + " TEXT NOT NULL," +
            InterpretationColumns.TYPE + " TEXT NOT NULL," +
            InterpretationColumns.NAME + " TEXT," +
            InterpretationColumns.DISPLAY_NAME + " TEXT," +
            InterpretationColumns.TEXT + " TEXT," +
            InterpretationColumns.EXTERNAL_ACCESS + " INTEGER," +
            InterpretationColumns.MAP + " TEXT," +
            InterpretationColumns.CHART + " TEXT," +
            InterpretationColumns.REPORT_TABLE + " TEXT," +
            InterpretationColumns.DATASET + " TEXT," +
            InterpretationColumns.ORGANIZATION_UNIT + " TEXT," +
            InterpretationColumns.PERIOD + " TEXT," +
            InterpretationColumns.USER + " TEXT," +
            InterpretationColumns.COMMENTS + " TEXT" + ")";

    public static final String DROP_INTERPRETATIONS_TABLE = "DROP TABLE IF EXISTS " + InterpretationColumns.TABLE_NAME;

    public static final String CREATE_KEY_VALUE_TABLE = "CREATE TABLE " + KeyValueColumns.TABLE_NAME + "(" +
            DBContract.KeyValueColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBContract.KeyValueColumns.KEY + " TEXT NOT NULL," +
            DBContract.KeyValueColumns.TYPE + " TEXT NOT NULL," +
            DBContract.KeyValueColumns.VALUE + " TEXT" + ")";

    public static final String DROP_KEY_VALUE_TABLE = "DROP TABLE IF EXISTS " + KeyValueColumns.TABLE_NAME;


    public static final String CREATE_REPORTS_TABLE = "CREATE TABLE " + ReportColumns.TABLE_NAME + "(" +
            ReportColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReportColumns.ORG_UNIT_ID + " TEXT NOT NULL," +
            ReportColumns.DATASET_ID + " TEXT NOT NULL," +
            ReportColumns.PERIOD + " TEXT NOT NULL," +
            ReportColumns.COMPLETE_DATE + " TEXT," +
            ReportColumns.STATE + " TEXT NOT NULL," +
            " UNIQUE " + "(" +
                            ReportColumns.ORG_UNIT_ID + "," +
                            ReportColumns.DATASET_ID + "," +
                            ReportColumns.PERIOD +
                          ")" +
            ")";

    public static final String DROP_REPORTS_TABLE = "DROP TABLE IF EXISTS " + ReportColumns.TABLE_NAME;

    public static final String CREATE_REPORT_GROUP_TABLE = "CREATE TABLE " + ReportGroupColumns.TABLE_NAME + "(" +
            ReportGroupColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReportGroupColumns.LABEL + " TEXT NOT NULL," +
            ReportGroupColumns.DATA_ELEMENT_COUNT + " INTEGER," +
            ReportGroupColumns.REPORT_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + ReportGroupColumns.REPORT_DB_ID + ")" +
            " REFERENCES " + ReportColumns.TABLE_NAME + "(" + ReportColumns.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_REPORT_GROUP_TABLE = "DROP TABLE IF EXISTS " + ReportGroupColumns.TABLE_NAME;

    public static final String CREATE_REPORT_FIELDS_TABLE = "CREATE TABLE " + ReportFieldColumns.TABLE_NAME + "(" +
            ReportFieldColumns.DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReportFieldColumns.LABEL + " TEXT," +
            ReportFieldColumns.TYPE + " TEXT NOT NULL," +
            ReportFieldColumns.DATA_ELEMENT + " TEXT NOT NULL," +
            ReportFieldColumns.CATEGORY_OPTION_COMBO + " TEXT NOT NULL," +
            ReportFieldColumns.OPTION_SET + " TEXT," +
            ReportFieldColumns.VALUE + " TEXT," +
            ReportFieldColumns.GROUP_DB_ID + " INTEGER NOT NULL," +
            " FOREIGN KEY " + "(" + ReportFieldColumns.GROUP_DB_ID + ")" +
            " REFERENCES " + ReportGroupColumns.TABLE_NAME + "(" + ReportGroupColumns.DB_ID + ")" +
            " ON DELETE CASCADE " + ")";

    public static final String DROP_REPORT_FIELDS_TABLE = "DROP TABLE IF EXISTS " + ReportFieldColumns.TABLE_NAME;

}
