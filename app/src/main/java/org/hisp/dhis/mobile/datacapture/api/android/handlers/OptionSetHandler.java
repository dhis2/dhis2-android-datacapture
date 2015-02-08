package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Option;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.OptionSets;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.utils.Utils.isNull;

public final class OptionSetHandler {
    public static final String[] PROJECTION = new String[] {
            OptionSets.DB_ID,
            OptionSets.ID,
            OptionSets.CREATED,
            OptionSets.LAST_UPDATED,
            OptionSets.NAME,
            OptionSets.DISPLAY_NAME
    };

    private static final int DB_ID = 0;
    private static final int ID = 1;
    private static final int CREATED = 2;
    private static final int LAST_UPDATED = 3;
    private static final int NAME = 4;
    private static final int DISPLAY_NAME = 5;

    private Context mContext;

    public OptionSetHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    public static ContentValues toContentValues(OptionSet optionSet) {
        isNull(optionSet, "OptionSet object must not be null");

        ContentValues values = new ContentValues();
        values.put(OptionSets.ID, optionSet.getId());
        values.put(OptionSets.CREATED, optionSet.getCreated());
        values.put(OptionSets.LAST_UPDATED, optionSet.getLastUpdated());
        values.put(OptionSets.NAME, optionSet.getName());
        values.put(OptionSets.DISPLAY_NAME, optionSet.getDisplayName());
        return values;
    }

    public static DbRow<OptionSet> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<OptionSet> row = new DbRow<>();
        OptionSet optionSet = new OptionSet();
        optionSet.setId(cursor.getString(ID));
        optionSet.setCreated(cursor.getString(CREATED));
        optionSet.setLastUpdated(cursor.getString(LAST_UPDATED));
        optionSet.setName(cursor.getString(NAME));
        optionSet.setDisplayName(cursor.getString(DISPLAY_NAME));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(optionSet);
        return row;
    }

    /*
    public static List<OptionSet> fromCursorToList(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        List<OptionSet> optionSets = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                OptionSet optionSet = fromCursor(cursor);
                optionSets.add(optionSet);
            } while(cursor.moveToNext());
        }
        return optionSets;
    }
    */

    private static void insert(List<ContentProviderOperation> ops,
                               OptionSet optionSet) {
        isNull(ops, "List<ContentProviderOperation> object must not be null");
        ops.add(ContentProviderOperation.newInsert(OptionSets.CONTENT_URI)
                .withValues(toContentValues(optionSet))
                .build());
        int index = ops.size() - 1;

        if (optionSet.getOptions() != null) {
            for (Option option: optionSet.getOptions()) {
                OptionHandler.insertWithReference(ops, index, option);
            }
        }
    }

    public void bulkInsert(List<OptionSet> optionSets) {
        if (optionSets == null || !(optionSets.size() > 0)) {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (OptionSet optionSet: optionSets) {
            insert(ops, optionSet);
        }

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static ContentProviderOperation delete(int id) {
        Uri uri = ContentUris.withAppendedId(OptionSets.CONTENT_URI, id);
        return ContentProviderOperation.newDelete(uri).build();
    }

    public DbRow<OptionSet> query(String selection) {
        Cursor cursor = mContext.getContentResolver().query(
                OptionSets.CONTENT_URI, PROJECTION, selection, null, null
        );

        return query(cursor, true);
    }

    public DbRow<OptionSet> query(Cursor cursor, boolean closeCursor) {
        DbRow<OptionSet> row = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            row = fromCursor(cursor);

            if (closeCursor) {
                cursor.close();
            }
        }
        return row;
    }

    public List<DbRow<OptionSet>> queryList(String selection) {
        Cursor cursor = mContext.getContentResolver().query(
                OptionSets.CONTENT_URI, PROJECTION, selection, null, null
        );
        return queryList(cursor, true);
    }

    public List<DbRow<OptionSet>> queryList(Cursor cursor, boolean closeCursor) {
        List<DbRow<OptionSet>> rows = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                DbRow<OptionSet> row = fromCursor(cursor);
                rows.add(row);
            } while(cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }
        return rows;
    }
}