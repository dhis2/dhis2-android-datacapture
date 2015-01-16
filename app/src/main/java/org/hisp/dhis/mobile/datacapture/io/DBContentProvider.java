package org.hisp.dhis.mobile.datacapture.io;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;

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
    private static final UriMatcher URI_MATCHER = buildMatcher();

    private DBOpenHelper mDBHelper;
    private ThreadLocal<Boolean> mIsInBatchMode;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DBContract.AUTHORITY, DashboardColumns.PATH, DASHBOARDS);
        matcher.addURI(DBContract.AUTHORITY, DashboardColumns.PATH + "/#", DASHBOARD_ID);
        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH, DASHBOARD_ITEMS);
        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH + "/#", DASHBOARD_ITEM_ID);
        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH, INTERPRETATIONS);
        matcher.addURI(DBContract.AUTHORITY, DashboardItemColumns.PATH + "/#", INTERPRETATIONS_ID);
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

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int count = db.update(table, values, where, selectionArgs);
        if (count > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }
}
