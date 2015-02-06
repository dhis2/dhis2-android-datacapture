package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DataSetColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.FieldColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.GroupColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OptionColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OptionSetColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OrganizationUnitColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;


public class DBContentProvider extends ContentProvider {

    /**
     * TODO Join Dashboard and Dashboard Item tables
     * for simpler handling in query method
     */

    private static final int DASHBOARDS = 600;
    private static final int DASHBOARD_ID = 601;

    private static final int DASHBOARD_ITEMS = 701;
    private static final int DASHBOARD_ITEM_ID = 702;

    private static final int INTERPRETATIONS = 800;
    private static final int INTERPRETATIONS_ID = 801;

    private static final int KEY_VALUES = 1000;
    private static final int KEY_VALUE_ID = 1001;

    private static final int REPORTS = 1100;
    private static final int REPORTS_WITH_GROUPS = 1101;
    private static final int REPORT_ID = 1102;

    private static final int REPORT_GROUPS = 1200;
    private static final int REPORT_GROUP_ID = 1201;

    private static final int REPORT_FIELDS = 1300;
    private static final int REPORT_FIELD_ID = 1301;

    private static final int OPTION_SETS = 1400;
    private static final int OPTION_SET_ID = 1401;

    private static final int OPTIONS = 1500;
    private static final int OPTION_ID = 1501;

    private static final int FIELDS = 1600;
    private static final int FIELDS_WITH_OPTION_SETS = 1602;
    private static final int FIELD_ID = 1601;

    private static final int GROUPS = 1700;
    private static final int GROUP_ID = 1701;

    private static final int ORGANIZATION_UNITS = 1800;
    private static final int ORGANIZATION_UNIT_ID = 1801;
    private static final int ORGANIZATION_UNIT_WITH_DATASETS = 1802;

    private static final int DATASETS = 1900;
    private static final int DATASET_ID = 1901;
    private static final int DATASET_ID_WITH_GROUPS = 1902;

    private static final UriMatcher URI_MATCHER = buildMatcher();

    private DBOpenHelper mDBHelper;
    private ThreadLocal<Boolean> mIsInBatchMode;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DBContract.AUTHORITY, DashboardColumns.PATH, DASHBOARDS);
        matcher.addURI(DBContract.AUTHORITY, DashboardColumns.PATH + "/#", DASHBOARD_ID);

        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH, DASHBOARD_ITEMS);
        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH + "/#", DASHBOARD_ITEM_ID);

        matcher.addURI(DBContract.AUTHORITY, InterpretationColumns.PATH, INTERPRETATIONS);
        matcher.addURI(DBContract.AUTHORITY, InterpretationColumns.PATH + "/#", INTERPRETATIONS_ID);

        matcher.addURI(DBContract.AUTHORITY, KeyValueColumns.PATH, KEY_VALUES);
        matcher.addURI(DBContract.AUTHORITY, KeyValueColumns.PATH + "/#", KEY_VALUE_ID);

        matcher.addURI(DBContract.AUTHORITY, ReportColumns.PATH, REPORTS);
        matcher.addURI(DBContract.AUTHORITY, ReportColumns.PATH_WITH_GROUPS, REPORTS_WITH_GROUPS);
        matcher.addURI(DBContract.AUTHORITY, ReportColumns.PATH + "/#", REPORT_ID);

        matcher.addURI(DBContract.AUTHORITY, ReportGroupColumns.PATH, REPORT_GROUPS);
        matcher.addURI(DBContract.AUTHORITY, ReportGroupColumns.PATH + "/#", REPORT_GROUP_ID);

        matcher.addURI(DBContract.AUTHORITY, ReportFieldColumns.PATH, REPORT_FIELDS);
        matcher.addURI(DBContract.AUTHORITY, ReportFieldColumns.PATH + "/#", REPORT_FIELD_ID);

        matcher.addURI(DBContract.AUTHORITY, OptionSetColumns.PATH, OPTION_SETS);
        matcher.addURI(DBContract.AUTHORITY, OptionSetColumns.PATH + "/#", OPTION_SET_ID);

        matcher.addURI(DBContract.AUTHORITY, OptionColumns.PATH, OPTIONS);
        matcher.addURI(DBContract.AUTHORITY, OptionColumns.PATH + "/#", OPTION_ID);

        matcher.addURI(DBContract.AUTHORITY, FieldColumns.PATH, FIELDS);
        matcher.addURI(DBContract.AUTHORITY, FieldColumns.PATH + "/#", FIELD_ID);
        matcher.addURI(DBContract.AUTHORITY,
                FieldColumns.PATH_WITH_OPTION_SETS,
                FIELDS_WITH_OPTION_SETS);

        matcher.addURI(DBContract.AUTHORITY, GroupColumns.PATH, GROUPS);
        matcher.addURI(DBContract.AUTHORITY, GroupColumns.PATH + "/#", GROUP_ID);

        matcher.addURI(DBContract.AUTHORITY, OrganizationUnitColumns.PATH, ORGANIZATION_UNITS);
        matcher.addURI(DBContract.AUTHORITY, OrganizationUnitColumns.PATH + "/#", ORGANIZATION_UNIT_ID);
        matcher.addURI(DBContract.AUTHORITY,
                OrganizationUnitColumns.PATH_WITH_DATASETS + "/#",
                ORGANIZATION_UNIT_WITH_DATASETS);

        matcher.addURI(DBContract.AUTHORITY, DataSetColumns.PATH, DATASETS);
        matcher.addURI(DBContract.AUTHORITY, DataSetColumns.PATH + "/#", DATASET_ID);
        matcher.addURI(DBContract.AUTHORITY,
                DataSetColumns.PATH_WITH_GROUPS + "/#",
                DATASET_ID_WITH_GROUPS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBOpenHelper(getContext());
        mIsInBatchMode = new ThreadLocal<>();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return DashboardColumns.CONTENT_TYPE;
            case DASHBOARD_ID:
                return DashboardColumns.CONTENT_ITEM_TYPE;

            case DASHBOARD_ITEMS:
                return DashboardItemColumns.CONTENT_TYPE;
            case DASHBOARD_ITEM_ID:
                return DashboardItemColumns.CONTENT_ITEM_TYPE;

            case INTERPRETATIONS:
                return InterpretationColumns.CONTENT_TYPE;
            case INTERPRETATIONS_ID:
                return InterpretationColumns.CONTENT_ITEM_TYPE;

            case KEY_VALUES:
                return KeyValueColumns.CONTENT_TYPE;
            case KEY_VALUE_ID:
                return KeyValueColumns.CONTENT_ITEM_TYPE;

            case REPORTS:
                return ReportColumns.CONTENT_TYPE;
            case REPORTS_WITH_GROUPS:
                return ReportColumns.CONTENT_TYPE_WITH_GROUPS;
            case REPORT_ID:
                return ReportColumns.CONTENT_ITEM_TYPE;

            case REPORT_GROUPS:
                return ReportGroupColumns.CONTENT_TYPE;
            case REPORT_GROUP_ID:
                return ReportGroupColumns.CONTENT_ITEM_TYPE;

            case REPORT_FIELDS:
                return ReportFieldColumns.CONTENT_TYPE;
            case REPORT_FIELD_ID:
                return ReportFieldColumns.CONTENT_ITEM_TYPE;

            case OPTION_SETS:
                return OptionSetColumns.CONTENT_TYPE;
            case OPTION_SET_ID:
                return OptionSetColumns.CONTENT_ITEM_TYPE;

            case OPTIONS:
                return OptionColumns.CONTENT_TYPE;
            case OPTION_ID:
                return OptionColumns.CONTENT_ITEM_TYPE;

            case FIELDS:
                return FieldColumns.CONTENT_TYPE;
            case FIELDS_WITH_OPTION_SETS:
                return FieldColumns.CONTENT_TYPE_WITH_OPTION_SETS;
            case FIELD_ID:
                return FieldColumns.CONTENT_ITEM_TYPE;

            case GROUPS:
                return GroupColumns.CONTENT_TYPE;
            case GROUP_ID:
                return GroupColumns.CONTENT_ITEM_TYPE;

            case ORGANIZATION_UNITS:
                return OrganizationUnitColumns.CONTENT_TYPE;
            case ORGANIZATION_UNIT_WITH_DATASETS:
                return OrganizationUnitColumns.CONTENT_TYPE_WITH_DATASETS;
            case ORGANIZATION_UNIT_ID:
                return OrganizationUnitColumns.CONTENT_ITEM_TYPE;

            case DATASETS:
                return DataSetColumns.CONTENT_TYPE;
            case DATASET_ID:
                return DataSetColumns.CONTENT_ITEM_TYPE;
            case DATASET_ID_WITH_GROUPS:
                return DataSetColumns.CONTENT_ITEM_TYPE_WITH_GROUPS;

            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return query(uri, DashboardColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case DASHBOARD_ID:
                return queryId(uri, DashboardColumns.TABLE_NAME, DashboardColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case DASHBOARD_ITEMS:
                return query(uri, DashboardItemColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case DASHBOARD_ITEM_ID:
                return queryId(uri, DashboardItemColumns.TABLE_NAME, DashboardItemColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case INTERPRETATIONS:
                return query(uri, InterpretationColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case INTERPRETATIONS_ID:
                return queryId(uri, InterpretationColumns.TABLE_NAME, InterpretationColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case KEY_VALUES:
                return query(uri, KeyValueColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case KEY_VALUE_ID:
                return queryId(uri, KeyValueColumns.TABLE_NAME, KeyValueColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case REPORTS:
                return query(uri, ReportColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case REPORT_ID:
                return queryId(uri, ReportColumns.TABLE_NAME, ReportColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);
            case REPORTS_WITH_GROUPS: {
                String table = ReportColumns.TABLE_NAME +
                        " LEFT OUTER JOIN " + ReportGroupColumns.TABLE_NAME +
                        " ON " + ReportColumns.TABLE_NAME + "." + ReportColumns.DB_ID +
                        " = " + ReportGroupColumns.TABLE_NAME + "." + ReportGroupColumns.REPORT_DB_ID;
                return query(uri, table, projection, selection, selectionArgs, sortOrder);
            }

            case REPORT_GROUPS:
                return query(uri, ReportGroupColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case REPORT_GROUP_ID:
                return queryId(uri, ReportGroupColumns.TABLE_NAME, ReportGroupColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case REPORT_FIELDS:
                return query(uri, ReportFieldColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case REPORT_FIELD_ID:
                return queryId(uri, ReportFieldColumns.TABLE_NAME, ReportFieldColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case OPTION_SETS:
                return query(uri, OptionSetColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case OPTION_SET_ID:
                return queryId(uri, OptionSetColumns.TABLE_NAME, OptionSetColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case OPTIONS:
                return query(uri, OptionColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case OPTION_ID:
                return queryId(uri, OptionColumns.TABLE_NAME, OptionColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case FIELDS:
                return query(uri, FieldColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case FIELDS_WITH_OPTION_SETS: {
                String table = FieldColumns.TABLE_NAME +
                        " FULL OUTER JOIN " + OptionSetColumns.TABLE_NAME +
                        " ON " + FieldColumns.TABLE_NAME + "." + FieldColumns.OPTION_SET +
                        " = " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.ID +
                        " FULL OUTER JOIN " + OptionColumns.TABLE_NAME +
                        " ON " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.DB_ID +
                        " = " + OptionColumns.TABLE_NAME + "." + OptionColumns.OPTION_SET_DB_ID;
                return query(uri, table, projection,
                        selection, selectionArgs, sortOrder);
            }
            case FIELD_ID:
                return queryId(uri, FieldColumns.TABLE_NAME, FieldColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case GROUPS:
                return query(uri, GroupColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case GROUP_ID:
                return queryId(uri, GroupColumns.TABLE_NAME, GroupColumns.DB_ID,
                        projection, selection, selectionArgs, sortOrder);

            case ORGANIZATION_UNITS:
                return query(uri, OrganizationUnitColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case ORGANIZATION_UNIT_WITH_DATASETS: {
                String table = OrganizationUnitColumns.TABLE_NAME +
                        " FULL OUTER JOIN " + DataSetColumns.TABLE_NAME +
                        " ON " + OrganizationUnitColumns.TABLE_NAME + "." + OrganizationUnitColumns.DB_ID +
                        " = " + DataSetColumns.TABLE_NAME + "." + DataSetColumns.ORGANIZATION_UNIT_DB_ID;
                return query(uri, table, projection,
                        selection, selectionArgs, sortOrder);
            }
            case ORGANIZATION_UNIT_ID:
                return queryId(uri, OrganizationUnitColumns.TABLE_NAME,
                        OrganizationUnitColumns.DB_ID, projection, selection,
                        selectionArgs, sortOrder);

            case DATASETS:
                return query(uri, DataSetColumns.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            case DATASET_ID:
                return queryId(uri, DataSetColumns.TABLE_NAME,
                        DataSetColumns.DB_ID, projection, selection,
                        selectionArgs, sortOrder);
            case DATASET_ID_WITH_GROUPS: {
                String table = DataSetColumns.TABLE_NAME +
                        " FULL OUTER JOIN " + GroupColumns.TABLE_NAME +
                        " ON " + DataSetColumns.TABLE_NAME + "." + DataSetColumns.DB_ID +
                        " = " + GroupColumns.TABLE_NAME + "." + GroupColumns.DATA_SET_DB_ID +
                        " FULL OUTER JOIN " + FieldColumns.TABLE_NAME +
                        " ON " + GroupColumns.TABLE_NAME + "." + GroupColumns.DB_ID +
                        " = " + FieldColumns.TABLE_NAME + "." + FieldColumns.GROUP_DB_ID +
                        " FULL OUTER JOIN " + OptionSetColumns.TABLE_NAME +
                        " ON " + FieldColumns.TABLE_NAME + "." + FieldColumns.OPTION_SET +
                        " = " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.ID +
                        " FULL OUTER JOIN " + OptionColumns.TABLE_NAME +
                        " ON " + OptionSetColumns.TABLE_NAME + "." + OptionSetColumns.DB_ID +
                        " = " + OptionColumns.TABLE_NAME + "." + OptionColumns.OPTION_SET_DB_ID;
                return queryId(uri, table,DataSetColumns.DB_ID,
                        projection, selection,selectionArgs, sortOrder);
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return insert(DashboardColumns.TABLE_NAME, values, uri);
            case DASHBOARD_ITEMS:
                return insert(DashboardItemColumns.TABLE_NAME, values, uri);
            case INTERPRETATIONS:
                return insert(InterpretationColumns.TABLE_NAME, values, uri);
            case KEY_VALUES:
                return insert(KeyValueColumns.TABLE_NAME, values, uri);
            case REPORTS:
                return insert(ReportColumns.TABLE_NAME, values, uri);
            case REPORT_GROUPS:
                return insert(ReportGroupColumns.TABLE_NAME, values, uri);
            case REPORT_FIELDS:
                return insert(ReportFieldColumns.TABLE_NAME, values, uri);
            case OPTION_SETS:
                return insert(OptionSetColumns.TABLE_NAME, values, uri);
            case OPTIONS:
                return insert(OptionColumns.TABLE_NAME, values, uri);
            case FIELDS:
                return insert(FieldColumns.TABLE_NAME, values, uri);
            case GROUPS:
                return insert(GroupColumns.TABLE_NAME, values, uri);
            case ORGANIZATION_UNITS:
                return insert(OrganizationUnitColumns.TABLE_NAME, values, uri);
            case DATASETS:
                return insert(DataSetColumns.TABLE_NAME, values, uri);
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return delete(uri, DashboardColumns.TABLE_NAME, selection, selectionArgs);
            case DASHBOARD_ID:
                return deleteId(uri, DashboardColumns.TABLE_NAME,
                        DashboardColumns.DB_ID, selection, selectionArgs);

            case DASHBOARD_ITEMS:
                return delete(uri, DashboardItemColumns.TABLE_NAME, selection, selectionArgs);
            case DASHBOARD_ITEM_ID:
                return deleteId(uri, DashboardItemColumns.TABLE_NAME,
                        DashboardItemColumns.DB_ID, selection, selectionArgs);

            case INTERPRETATIONS:
                return delete(uri, InterpretationColumns.TABLE_NAME, selection, selectionArgs);
            case INTERPRETATIONS_ID:
                return deleteId(uri, InterpretationColumns.TABLE_NAME,
                        InterpretationColumns.DB_ID, selection, selectionArgs);

            case KEY_VALUES:
                return delete(uri, KeyValueColumns.TABLE_NAME, selection, selectionArgs);
            case KEY_VALUE_ID:
                return deleteId(uri, KeyValueColumns.TABLE_NAME,
                        KeyValueColumns.DB_ID, selection, selectionArgs);

            case REPORTS:
                return delete(uri, ReportColumns.TABLE_NAME, selection, selectionArgs);
            case REPORT_ID:
                return deleteId(uri, ReportColumns.TABLE_NAME,
                        ReportColumns.DB_ID, selection, selectionArgs);

            case REPORT_GROUPS:
                return delete(uri, ReportGroupColumns.TABLE_NAME, selection, selectionArgs);
            case REPORT_GROUP_ID:
                return deleteId(uri, ReportGroupColumns.TABLE_NAME,
                        ReportGroupColumns.DB_ID, selection, selectionArgs);

            case REPORT_FIELDS:
                return delete(uri, ReportFieldColumns.TABLE_NAME, selection, selectionArgs);
            case REPORT_FIELD_ID:
                return deleteId(uri, ReportFieldColumns.TABLE_NAME,
                        ReportFieldColumns.DB_ID, selection, selectionArgs);

            case OPTION_SETS:
                return delete(uri, OptionSetColumns.TABLE_NAME, selection, selectionArgs);
            case OPTION_SET_ID:
                return deleteId(uri, OptionSetColumns.TABLE_NAME,
                        OptionSetColumns.DB_ID, selection, selectionArgs);

            case OPTIONS:
                return delete(uri, OptionColumns.TABLE_NAME, selection, selectionArgs);
            case OPTION_ID:
                return deleteId(uri, OptionColumns.TABLE_NAME,
                        OptionColumns.DB_ID, selection, selectionArgs);

            case FIELDS:
                return delete(uri, FieldColumns.TABLE_NAME, selection, selectionArgs);
            case FIELD_ID:
                return deleteId(uri, FieldColumns.TABLE_NAME,
                        FieldColumns.DB_ID, selection, selectionArgs);

            case GROUPS:
                return delete(uri, GroupColumns.TABLE_NAME, selection, selectionArgs);
            case GROUP_ID:
                return deleteId(uri, GroupColumns.TABLE_NAME,
                        GroupColumns.DB_ID, selection, selectionArgs);

            case ORGANIZATION_UNITS:
                return delete(uri, OrganizationUnitColumns.TABLE_NAME, selection, selectionArgs);
            case ORGANIZATION_UNIT_ID:
                return deleteId(uri, OrganizationUnitColumns.TABLE_NAME,
                        OrganizationUnitColumns.DB_ID, selection, selectionArgs);

            case DATASETS:
                return delete(uri, DataSetColumns.TABLE_NAME, selection, selectionArgs);
            case DATASET_ID:
                return deleteId(uri, DataSetColumns.TABLE_NAME,
                        DataSetColumns.DB_ID, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS:
                return update(uri, DashboardColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case DASHBOARD_ID:
                return updateId(uri, DashboardColumns.TABLE_NAME,
                        DashboardColumns.DB_ID, selection, selectionArgs, values);

            case DASHBOARD_ITEMS:
                return update(uri, DashboardItemColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case DASHBOARD_ITEM_ID:
                return updateId(uri, DashboardItemColumns.TABLE_NAME,
                        DashboardItemColumns.DB_ID, selection, selectionArgs, values);

            case INTERPRETATIONS:
                return update(uri, InterpretationColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case INTERPRETATIONS_ID:
                return updateId(uri, InterpretationColumns.TABLE_NAME,
                        InterpretationColumns.DB_ID, selection, selectionArgs, values);

            case KEY_VALUES:
                return update(uri, KeyValueColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case KEY_VALUE_ID:
                return updateId(uri, KeyValueColumns.TABLE_NAME,
                        KeyValueColumns.DB_ID, selection, selectionArgs, values);

            case REPORTS:
                return update(uri, ReportColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case REPORT_ID:
                return updateId(uri, ReportColumns.TABLE_NAME,
                        ReportColumns.DB_ID, selection, selectionArgs, values);

            case REPORT_GROUPS:
                return update(uri, ReportGroupColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case REPORT_GROUP_ID:
                return updateId(uri, ReportGroupColumns.TABLE_NAME,
                        ReportGroupColumns.DB_ID, selection, selectionArgs, values);

            case REPORT_FIELDS:
                return update(uri, ReportFieldColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case REPORT_FIELD_ID:
                return updateId(uri, ReportFieldColumns.TABLE_NAME,
                        ReportFieldColumns.DB_ID, selection, selectionArgs, values);

            case OPTION_SETS:
                return update(uri, OptionSetColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case OPTION_SET_ID:
                return updateId(uri, OptionSetColumns.TABLE_NAME,
                        OptionSetColumns.DB_ID, selection, selectionArgs, values);

            case OPTIONS:
                return update(uri, OptionColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case OPTION_ID:
                return updateId(uri, OptionColumns.TABLE_NAME,
                        OptionColumns.DB_ID, selection, selectionArgs, values);

            case FIELDS:
                return update(uri, FieldColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case FIELD_ID:
                return updateId(uri, FieldColumns.TABLE_NAME,
                        FieldColumns.DB_ID, selection, selectionArgs, values);

            case GROUPS:
                return update(uri, GroupColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case GROUP_ID:
                return updateId(uri, GroupColumns.TABLE_NAME,
                        GroupColumns.DB_ID, selection, selectionArgs, values);

            case ORGANIZATION_UNITS:
                return update(uri, OrganizationUnitColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case ORGANIZATION_UNIT_ID:
                return updateId(uri, OrganizationUnitColumns.TABLE_NAME,
                        OrganizationUnitColumns.DB_ID, selection, selectionArgs, values);

            case DATASETS:
                return update(uri, DataSetColumns.TABLE_NAME,
                        selection, selectionArgs, values);
            case DATASET_ID:
                return updateId(uri, DataSetColumns.TABLE_NAME,
                        DataSetColumns.DB_ID, selection, selectionArgs, values);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private Cursor query(Uri uri, String tableName, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryId(Uri uri, String tableName, String colId, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        long id = parseId(uri);
        qBuilder.setTables(tableName);
        qBuilder.appendWhere(colId + " = " + id);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insert(String tableName, ContentValues values, Uri uri) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insertOrThrow(tableName, null, values);
        if (!isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return withAppendedId(uri, id);
    }

    private int delete(Uri uri, String tableName,
                       String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int count = db.delete(tableName, selection, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int deleteId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int update(Uri uri, String tableName, String selection,
                       String[] selectionArgs, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private int updateId(Uri uri, String tableName, String colId,
                         String selection, String[] selectionArgs,
                         ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id = parseId(uri);
        String where = colId + " = " + id;
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final Set<Uri> contentUris = new HashSet<>();
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        mIsInBatchMode.set(true);
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];

            for (int i = 0; i < numOperations; i++) {
                contentUris.add(operations.get(i).getUri());
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
            mIsInBatchMode.remove();
            ContentResolver resolver = getContext().getContentResolver();
            for (Uri uri : contentUris) {
                resolver.notifyChange(uri, null);
            }
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }
}