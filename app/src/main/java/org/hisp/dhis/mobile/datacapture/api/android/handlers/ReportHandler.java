package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;

public final class ReportHandler {
    public static final String[] PROJECTION = new String[] {
            ReportColumns.TABLE_NAME + "." + ReportColumns.DB_ID,
            ReportColumns.TABLE_NAME + "." + ReportColumns.ORG_UNIT_ID,
            ReportColumns.TABLE_NAME + "." + ReportColumns.DATASET_ID,
            ReportColumns.TABLE_NAME + "." + ReportColumns.PERIOD,
            ReportColumns.TABLE_NAME + "." + ReportColumns.COMPLETE_DATE,
    };

    private static final int DB_ID = 0;
    private static final int ORG_UNIT_ID = 1;
    private static final int DATASET_ID = 2;
    private static final int PERIOD = 3;
    private static final int COMPLETE_DATE = 4;

    public static ContentValues toContentValues(Report report) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReportColumns.ORG_UNIT_ID, report.getOrgUnit());
        contentValues.put(ReportColumns.DATASET_ID, report.getDataSet());
        contentValues.put(ReportColumns.PERIOD, report.getPeriod());
        contentValues.put(ReportColumns.COMPLETE_DATE, report.getCompleteDate());
        return contentValues;
    }

    public static DBItemHolder<Report> fromCursor(Cursor cursor) {
        Report report = new Report();

        report.setOrgUnit(cursor.getString(ORG_UNIT_ID));
        report.setDataSet(cursor.getString(DATASET_ID));
        report.setPeriod(cursor.getString(PERIOD));
        report.setCompleteDate(cursor.getString(COMPLETE_DATE));

        DBItemHolder<Report> holder = new DBItemHolder<>();
        holder.setDataBaseId(cursor.getInt(DB_ID));
        holder.setItem(report);
        return holder;
    }
}
