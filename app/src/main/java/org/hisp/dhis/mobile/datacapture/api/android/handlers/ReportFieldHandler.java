package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;

public final class ReportFieldHandler {
    public static final String[] PROJECTION = new String[]{
            ReportFields.DB_ID,
            ReportFields.LABEL,
            ReportFields.TYPE,
            ReportFields.DATA_ELEMENT,
            ReportFields.CATEGORY_OPTION_COMBO,
            ReportFields.OPTION_SET,
            ReportFields.VALUE
    };

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int TYPE = 2;
    private static final int DATA_ELEMENT = 3;
    private static final int CATEGORY_OPTION_COMBO = 4;
    private static final int OPTION_SET = 5;
    private static final int VALUE = 6;

    public static ContentValues toContentValues(Field field) {
        ContentValues values = new ContentValues();
        values.put(ReportFields.LABEL, field.getLabel());
        values.put(ReportFields.TYPE, field.getType());
        values.put(ReportFields.DATA_ELEMENT, field.getDataElement());
        values.put(ReportFields.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
        values.put(ReportFields.OPTION_SET, field.getOptionSet());
        values.put(ReportFields.VALUE, field.getValue());
        return values;
    }

    public static DBItemHolder<Field> fromCursor(Cursor cursor) {
        Field field = new Field();
        field.setLabel(cursor.getString(LABEL));
        field.setType(cursor.getString(TYPE));
        field.setDataElement(cursor.getString(DATA_ELEMENT));
        field.setCategoryOptionCombo(cursor.getString(CATEGORY_OPTION_COMBO));
        field.setOptionSet(cursor.getString(OPTION_SET));
        field.setValue(cursor.getString(VALUE));

        DBItemHolder<Field> holder = new DBItemHolder<>();
        holder.setDataBaseId(cursor.getInt(DB_ID));
        holder.setItem(field);
        return holder;
    }
}
