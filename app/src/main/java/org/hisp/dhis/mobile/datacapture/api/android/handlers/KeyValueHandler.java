package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValues;

public final class KeyValueHandler {
    public static final String[] PROJECTION = new String[]{
            KeyValues.DB_ID,
            KeyValues.KEY,
            KeyValues.TYPE,
            KeyValues.VALUE
    };

    private static final int DB_ID = 0;
    private static final int KEY = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;

    private KeyValueHandler() {
    }

    public static ContentValues toContentValues(KeyValue keyValue) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KeyValues.KEY, keyValue.getKey());
        contentValues.put(KeyValues.TYPE, keyValue.getType().toString());
        contentValues.put(KeyValues.VALUE, keyValue.getValue());

        return contentValues;
    }

    public static DbRow<KeyValue> fromCursor(Cursor cursor) {
        DbRow<KeyValue> holder = new DbRow<>();
        KeyValue keyValue = new KeyValue();

        KeyValue.Type type = KeyValue.Type.valueOf(cursor.getString(TYPE));
        keyValue.setKey(cursor.getString(KEY));
        keyValue.setType(type);
        keyValue.setValue(cursor.getString(VALUE));

        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(keyValue);

        return holder;
    }

    public static ContentProviderOperation insert(KeyValue value) {
        return ContentProviderOperation.newInsert(KeyValues.CONTENT_URI)
                .withValues(toContentValues(value))
                .build();
    }
}
