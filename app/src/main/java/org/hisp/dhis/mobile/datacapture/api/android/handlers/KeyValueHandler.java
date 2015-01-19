package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;

public final class KeyValueHandler {
    public static final String[] PROJECTION = new String[]{
            KeyValueColumns.DB_ID,
            KeyValueColumns.KEY,
            KeyValueColumns.TYPE,
            KeyValueColumns.VALUE
    };

    private static final int DB_ID = 0;
    private static final int KEY = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;

    private KeyValueHandler() {
    }

    public static ContentValues toContentValues(KeyValue keyValue) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KeyValueColumns.KEY, keyValue.getKey());
        contentValues.put(KeyValueColumns.TYPE, keyValue.getType().toString());
        contentValues.put(KeyValueColumns.VALUE, keyValue.getValue());

        return contentValues;
    }

    public static DBItemHolder<KeyValue> fromCursor(Cursor cursor) {
        DBItemHolder<KeyValue> holder = new DBItemHolder<>();
        KeyValue keyValue = new KeyValue();

        KeyValue.Type type = KeyValue.Type.valueOf(cursor.getString(TYPE));
        keyValue.setKey(cursor.getString(KEY));
        keyValue.setType(type);
        keyValue.setValue(cursor.getString(VALUE));

        holder.setDataBaseId(cursor.getInt(DB_ID));
        holder.setItem(keyValue);

        return holder;
    }

    public static ContentProviderOperation insert(KeyValue value) {
        return ContentProviderOperation.newInsert(KeyValueColumns.CONTENT_URI)
                .withValues(toContentValues(value))
                .build();
    }
}
