package org.hisp.dhis.mobile.datacapture.io.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.android.models.ReportState;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.Reports;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.mobile.datacapture.api.utils.Preconditions.isNull;

public final class ReportHandler {
    public static final String[] PROJECTION = new String[]{
            Reports.TABLE_NAME + "." + Reports.DB_ID,
            Reports.TABLE_NAME + "." + Reports.ORG_UNIT_ID,
            Reports.TABLE_NAME + "." + Reports.ORG_UNIT_LABEL,
            Reports.TABLE_NAME + "." + Reports.DATASET_ID,
            Reports.TABLE_NAME + "." + Reports.DATASET_LABEL,
            Reports.TABLE_NAME + "." + Reports.PERIOD,
            Reports.TABLE_NAME + "." + Reports.PERIOD_LABEL,
            Reports.TABLE_NAME + "." + Reports.COMPLETE_DATE,
    };

    public static final String REPORT_ID_SELECTION =
            Reports.ORG_UNIT_ID + " = " + " ? " + " AND " +
                    Reports.DATASET_ID + " = " + " ? " + " AND " +
                    Reports.PERIOD + " = " + " ? ";

    public static final String REPORT_STATE_SELECTION =
            Reports.STATE + " = " + " ? ";

    private static final int DB_ID = 0;
    private static final int ORG_UNIT_ID = 1;
    private static final int ORG_UNIT_LABEL = 2;
    private static final int DATASET_ID = 3;
    private static final int DATASET_LABEL = 4;
    private static final int PERIOD = 5;
    private static final int PERIOD_LABEL = 6;
    private static final int COMPLETE_DATE = 7;

    private Context mContext;

    public ReportHandler(Context context) {
        mContext = isNull(context, "Context must not be null");
    }

    private static ContentValues toContentValues(Report report) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Reports.ORG_UNIT_ID, report.getOrgUnit());
        contentValues.put(Reports.ORG_UNIT_LABEL, report.getOrgUnitLabel());
        contentValues.put(Reports.DATASET_ID, report.getDataSet());
        contentValues.put(Reports.DATASET_LABEL, report.getDataSetLabel());
        contentValues.put(Reports.PERIOD, report.getPeriod());
        contentValues.put(Reports.PERIOD_LABEL, report.getPeriodLabel());
        contentValues.put(Reports.COMPLETE_DATE, report.getCompleteDate());
        return contentValues;
    }

    private static DbRow<Report> fromCursor(Cursor cursor) {
        Report report = new Report();

        report.setOrgUnit(cursor.getString(ORG_UNIT_ID));
        report.setOrgUnitLabel(cursor.getString(ORG_UNIT_LABEL));
        report.setDataSet(cursor.getString(DATASET_ID));
        report.setDataSetLabel(cursor.getString(DATASET_LABEL));
        report.setPeriod(cursor.getString(PERIOD));
        report.setPeriodLabel(cursor.getString(PERIOD_LABEL));
        report.setCompleteDate(cursor.getString(COMPLETE_DATE));

        DbRow<Report> holder = new DbRow<>();
        holder.setId(cursor.getInt(DB_ID));
        holder.setItem(report);
        return holder;
    }

    public DbRow<Report> query(String orgUnitId, String dataSetId, String period) {
        String[] selectionArgs = new String[]{orgUnitId, dataSetId, period};
        Cursor cursor = mContext.getContentResolver().query(
                Reports.CONTENT_URI, PROJECTION, REPORT_ID_SELECTION, selectionArgs, null
        );
        return mapSingleItem(cursor, true);
    }

    public List<DbRow<Report>> query(ReportState state) {
        String[] selectionArgs = new String[] { state.toString() };
        Cursor cursor = mContext.getContentResolver().query(
                Reports.CONTENT_URI, PROJECTION, REPORT_STATE_SELECTION, selectionArgs, null
        );
        return map(cursor, true);
    }

    public List<DbRow<Report>> query() {
        Cursor cursor = mContext.getContentResolver().query(
                Reports.CONTENT_URI, PROJECTION, null, null, null
        );

        return map(cursor, true);
    }

    public static List<DbRow<Report>> map(Cursor cursor, boolean closeCursor) {
        List<DbRow<Report>> reports = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                reports.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (closeCursor) {
                cursor.close();
            }
        }

        return reports;
    }

    public DbRow<Report> mapSingleItem(Cursor cursor, boolean closeCursor) {
        DbRow<Report> row = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            row = fromCursor(cursor);

            if (closeCursor) {
                cursor.close();
            }
        }
        return row;
    }

    public void insert(Report report, DataSet dataSet) {
        isNull(report, "Report object must not be null");
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        insert(ops, report, dataSet);

        try {
            mContext.getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static void insert(List<ContentProviderOperation> ops,
                               Report report, DataSet dataSet) {
        isNull(ops, "List<ContentProviderOperation> must not be null");
        isNull(report, "Report must not be null");
        isNull(dataSet, "DataSet must not be null");

        ops.add(ContentProviderOperation
                .newInsert(Reports.CONTENT_URI)
                .withValues(toContentValues(report))
                .withValue(Reports.STATE, ReportState.PENDING.toString())
                .build());

        int index = ops.size() - 1;
        ReportGroupHandler.insertWithReference(ops, index,
                dataSet.getGroups());
    }
}
