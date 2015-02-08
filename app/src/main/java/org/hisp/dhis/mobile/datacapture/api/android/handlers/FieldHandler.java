package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Fields;

import java.util.List;

import static org.hisp.dhis.mobile.datacapture.utils.Utils.isNull;

public final class FieldHandler {
    public static final String[] PROJECTION = new String[] {
            Fields.DB_ID,
            Fields.LABEL,
            Fields.TYPE,
            Fields.DATA_ELEMENT,
            Fields.CATEGORY_OPTION_COMBO,
            Fields.VALUE,
            Fields.OPTION_SET
    };

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int TYPE = 2;
    private static final int DATA_ELEMENT = 3;
    private static final int CATEGORY_OPTION_COMBO = 4;
    private static final int VALUE = 5;
    private static final int OPTION_SET = 6;

    public static ContentValues toContentValues(Field field) {
        isNull(field, "Field object must not be null");

        ContentValues values = new ContentValues();
        values.put(Fields.LABEL, field.getLabel());
        values.put(Fields.TYPE, field.getType());
        values.put(Fields.DATA_ELEMENT, field.getDataElement());
        values.put(Fields.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
        values.put(Fields.VALUE, field.getValue());
        values.put(Fields.OPTION_SET, field.getOptionSet());
        return values;
    }

    public static DbRow<Field> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<Field> row = new DbRow<>();
        Field field = new Field();
        field.setLabel(cursor.getString(LABEL));
        field.setType(cursor.getString(TYPE));
        field.setDataElement(cursor.getString(DATA_ELEMENT));
        field.setCategoryOptionCombo(cursor.getString(CATEGORY_OPTION_COMBO));
        field.setValue(cursor.getString(VALUE));
        field.setOptionSet(cursor.getString(OPTION_SET));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(field);
        return row;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int groupIndex, List<Field> fields) {
        isNull(ops, "List<ContentProviderOperation> object must not be null");

        if (fields != null && fields.size() > 0) {
            for (Field field: fields) {
                ops.add(ContentProviderOperation
                        .newInsert(Fields.CONTENT_URI)
                        .withValueBackReference(Fields.GROUP_DB_ID, groupIndex)
                        .withValues(toContentValues(field))
                        .build());
            }
        }
    }
}
