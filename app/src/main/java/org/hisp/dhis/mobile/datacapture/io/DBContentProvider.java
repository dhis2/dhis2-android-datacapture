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
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        return matcher;
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
            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBOpenHelper(getContext());
        mIsInBatchMode = new ThreadLocal<>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                qBuilder.setTables(DashboardColumns.TABLE_NAME);
                break;
            }

            case DASHBOARD_ID: {
                long id = parseId(uri);
                qBuilder.setTables(DashboardColumns.TABLE_NAME);
                qBuilder.appendWhere(DashboardColumns.DB_ID + " = " + id);
                break;
            }

            case DASHBOARD_ITEMS: {
                qBuilder.setTables(DashboardItemColumns.TABLE_NAME);
                break;
            }

            case DASHBOARD_ITEM_ID: {
                long id = parseId(uri);
                qBuilder.setTables(DashboardItemColumns.TABLE_NAME);
                qBuilder.appendWhere(DashboardItemColumns.DB_ID + " = " + id);
                break;
            }

            case INTERPRETATIONS: {
                qBuilder.setTables(InterpretationColumns.TABLE_NAME);
                break;
            }

            case INTERPRETATIONS_ID: {
                long id = parseId(uri);
                qBuilder.setTables(InterpretationColumns.TABLE_NAME);
                qBuilder.appendWhere(InterpretationColumns.DB_ID + " = " + id);
                break;
            }

            case KEY_VALUES: {
                qBuilder.setTables(KeyValueColumns.TABLE_NAME);
                break;
            }

            case KEY_VALUE_ID: {
                long id = parseId(uri);
                qBuilder.setTables(KeyValueColumns.TABLE_NAME);
                qBuilder.appendWhere(KeyValueColumns.DB_ID + " = " + id);
                break;
            }

            case REPORTS: {
                qBuilder.setTables(ReportColumns.TABLE_NAME);
                break;
            }

            case REPORT_ID: {
                long id = parseId(uri);
                qBuilder.setTables(ReportColumns.TABLE_NAME);
                qBuilder.appendWhere(ReportColumns.DB_ID + " = " + id);
                break;
            }

            case REPORTS_WITH_GROUPS: {
                qBuilder.setTables(
                        ReportColumns.TABLE_NAME +
                                " LEFT OUTER JOIN " + ReportGroupColumns.TABLE_NAME +
                                " ON " + ReportColumns.TABLE_NAME + "." + ReportColumns.DB_ID +
                                " = " + ReportGroupColumns.TABLE_NAME + "." + ReportGroupColumns.REPORT_DB_ID
                );
                break;
            }

            case REPORT_GROUPS: {
                qBuilder.setTables(ReportGroupColumns.TABLE_NAME);
                break;
            }

            case REPORT_GROUP_ID: {
                long id = parseId(uri);
                qBuilder.setTables(ReportGroupColumns.TABLE_NAME);
                qBuilder.appendWhere(ReportGroupColumns.DB_ID + " = " + id);
                break;
            }

            case REPORT_FIELDS: {
                qBuilder.setTables(ReportFieldColumns.TABLE_NAME);
                break;
            }

            case REPORT_FIELD_ID: {
                long id = parseId(uri);
                qBuilder.setTables(ReportFieldColumns.TABLE_NAME);
                qBuilder.appendWhere(ReportFieldColumns.DB_ID + " = " + id);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                long id = db.insertOrThrow(DashboardColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case DASHBOARD_ITEMS: {
                long id = db.insertOrThrow(DashboardItemColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case INTERPRETATIONS: {
                long id = db.insertOrThrow(InterpretationColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case KEY_VALUES: {
                long id = db.insertOrThrow(KeyValueColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case REPORTS: {
                long id = db.insertOrThrow(ReportColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case REPORT_GROUPS: {
                long id = db.insertOrThrow(ReportGroupColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            case REPORT_FIELDS: {
                long id = db.insertOrThrow(ReportFieldColumns.TABLE_NAME, null, values);
                if (!isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return withAppendedId(uri, id);
            }

            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String where;
        String table;

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                table = DashboardColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case DASHBOARD_ID: {
                long id = parseId(uri);
                table = DashboardColumns.TABLE_NAME;
                where = DashboardColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case DASHBOARD_ITEMS: {
                table = DashboardItemColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case DASHBOARD_ITEM_ID: {
                long id = parseId(uri);
                table = DashboardItemColumns.TABLE_NAME;
                where = DashboardItemColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case INTERPRETATIONS: {
                table = InterpretationColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case INTERPRETATIONS_ID: {
                long id = parseId(uri);
                table = InterpretationColumns.TABLE_NAME;
                where = InterpretationColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case KEY_VALUES: {
                table = KeyValueColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case KEY_VALUE_ID: {
                long id = parseId(uri);
                table = KeyValueColumns.TABLE_NAME;
                where = KeyValueColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORTS: {
                table = ReportColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_ID: {
                long id = parseId(uri);
                table = ReportColumns.TABLE_NAME;
                where = ReportColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORT_GROUPS: {
                table = ReportGroupColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_GROUP_ID: {
                long id = parseId(uri);
                table = ReportGroupColumns.TABLE_NAME;
                where = ReportGroupColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORT_FIELDS: {
                table = ReportFieldColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_FIELD_ID: {
                long id = parseId(uri);
                table = ReportFieldColumns.TABLE_NAME;
                where = ReportFieldColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int count = db.delete(table, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String table;
        String where;

        switch (URI_MATCHER.match(uri)) {
            case DASHBOARDS: {
                table = DashboardColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case DASHBOARD_ID: {
                long id = parseId(uri);
                table = DashboardColumns.TABLE_NAME;
                where = DashboardColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case DASHBOARD_ITEMS: {
                table = DashboardItemColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case DASHBOARD_ITEM_ID: {
                long id = parseId(uri);
                table = DashboardItemColumns.TABLE_NAME;
                where = DashboardItemColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case INTERPRETATIONS: {
                table = InterpretationColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case INTERPRETATIONS_ID: {
                long id = parseId(uri);
                table = InterpretationColumns.TABLE_NAME;
                where = InterpretationColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case KEY_VALUES: {
                table = KeyValueColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case KEY_VALUE_ID: {
                long id = parseId(uri);
                table = KeyValueColumns.TABLE_NAME;
                where = KeyValueColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORTS: {
                table = ReportColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_ID: {
                long id = parseId(uri);
                table = ReportColumns.TABLE_NAME;
                where = ReportColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORT_GROUPS: {
                table = ReportGroupColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_GROUP_ID: {
                long id = parseId(uri);
                table = ReportGroupColumns.TABLE_NAME;
                where = ReportGroupColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            case REPORT_FIELDS: {
                table = ReportFieldColumns.TABLE_NAME;
                where = selection;
                break;
            }

            case REPORT_FIELD_ID: {
                long id = parseId(uri);
                table = ReportFieldColumns.TABLE_NAME;
                where = ReportFieldColumns.DB_ID + " = " + id;
                if (!isEmpty(selection)) {
                    where += " AND " + selection;
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int count = db.update(table, values, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final Set<Uri> contentUris = new HashSet<Uri>();
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
