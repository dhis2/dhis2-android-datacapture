package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Groups;

import java.util.List;

import static org.hisp.dhis.mobile.datacapture.utils.Utils.isNull;

public final class GroupHandler {
    public static final String[] PROJECTION = new String[] {
            Groups.DB_ID,
            Groups.LABEL,
            Groups.DATA_ELEMENT_COUNT
    };

    private static final int DB_ID = 0;
    private static final int LABEL = 2;
    private static final int DATA_ELEMENT_COUNT = 3;

    public static ContentValues toContentValues(Group group) {
        isNull(group, "Group must not be null");

        ContentValues values = new ContentValues();
        values.put(Groups.LABEL, group.getLabel());
        values.put(Groups.DATA_ELEMENT_COUNT, group.getDataElementCount());

        return values;
    }

    public static DbRow<Group> fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");

        DbRow<Group> row = new DbRow<>();
        Group group = new Group();
        group.setLabel(cursor.getString(LABEL));
        group.setDataElementCount(cursor.getInt(DATA_ELEMENT_COUNT));
        row.setId(cursor.getInt(DB_ID));
        row.setItem(group);
        return row;
    }

    public static void insertWithReference(List<ContentProviderOperation> ops,
                                           int dataSetIndex, List<Group> groups) {
        isNull(ops, "List<ContentProviderOperation> must not be null");

        if (groups != null && groups.size() > 0) {
            for (Group group: groups) {
                ops.add(ContentProviderOperation
                        .newInsert(Groups.CONTENT_URI)
                        .withValueBackReference(Groups.DATA_SET_DB_ID, dataSetIndex)
                        .withValues(toContentValues(group))
                        .build());
                int index = ops.size() - 1;
                FieldHandler.insertWithReference(ops, index, group.getFields());
            }
        }
    }
}
