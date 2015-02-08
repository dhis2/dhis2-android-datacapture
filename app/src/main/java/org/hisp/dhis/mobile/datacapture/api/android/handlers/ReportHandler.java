package org.hisp.dhis.mobile.datacapture.api.android.handlers;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Reports;

public final class ReportHandler {
    public static final String[] PROJECTION = new String[] {
            Reports.TABLE_NAME + "." + Reports.DB_ID,
            Reports.TABLE_NAME + "." + Reports.ORG_UNIT_ID,
            Reports.TABLE_NAME + "." + Reports.DATASET_ID,
            Reports.TABLE_NAME + "." + Reports.PERIOD,
            Reports.TABLE_NAME + "." + Reports.COMPLETE_DATE,
    };

    private static final int DB_ID = 0;
    private static final int ORG_UNIT_ID = 1;
    private static final int DATASET_ID = 2;
    private static final int PERIOD = 3;
    private static final int COMPLETE_DATE = 4;

    public static ContentValues toContentValues(Report report) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Reports.ORG_UNIT_ID, report.getOrgUnit());
        contentValues.put(Reports.DATASET_ID, report.getDataSet());
        contentValues.put(Reports.PERIOD, report.getPeriod());
        contentValues.put(Reports.COMPLETE_DATE, report.getCompleteDate());
        return contentValues;
    }

    public static DbRow<Report> fromCursor(Cursor cursor) {
        Report report = new Report();

        report.setOrgUnit(cursor.getString(ORG_UNIT_ID));
        report.setDataSet(cursor.getString(DATASET_ID));
        report.setPeriod(cursor.getString(PERIOD));
        report.setCompleteDate(cursor.getString(COMPLETE_DATE));

        DbRow<Report> holder = new DbRow<>();
        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(report);
        return holder;
    }
}
