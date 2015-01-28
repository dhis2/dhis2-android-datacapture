package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;

public final class ReportGroupHandler {
    public static final String[] PROJECTION = new String[] {
            ReportGroupColumns.TABLE_NAME + "." + ReportGroupColumns.DB_ID,
            ReportGroupColumns.TABLE_NAME + "." + ReportGroupColumns.LABEL,
            ReportGroupColumns.TABLE_NAME + "." + ReportGroupColumns.DATA_ELEMENT_COUNT,
    };

    private static final int DB_ID = 0;
    private static final int LABEL = 1;
    private static final int DATA_ELEMENT_COUNT = 2;

    public static ContentValues toContentValues(Group group) {
        ContentValues values = new ContentValues();
        values.put(ReportGroupColumns.LABEL, group.getLabel());
        values.put(ReportGroupColumns.DATA_ELEMENT_COUNT, group.getDataElementCount());
        return values;
    }

    public static DBItemHolder<Group> fromCursor(Cursor cursor) {
        Group group = new Group();
        group.setLabel(cursor.getString(LABEL));
        group.setDataElementCount(cursor.getInt(DATA_ELEMENT_COUNT));

        DBItemHolder<Group> holder = new DBItemHolder<>();
        holder.setDataBaseId(cursor.getInt(DB_ID));
        holder.setItem(group);
        return holder;
    }
}
