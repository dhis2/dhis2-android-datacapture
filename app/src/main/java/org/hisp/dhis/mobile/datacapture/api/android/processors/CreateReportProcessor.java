package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.api.android.events.CreateReportEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnCreateReportEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.KeyValueHandler;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportFieldHandler;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportGroupHandler;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.DBContract;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;

import java.util.ArrayList;

public class CreateReportProcessor extends AbsProcessor<CreateReportEvent, OnCreateReportEvent> {

    public CreateReportProcessor(Context context, CreateReportEvent event) {
        super(context, event);
    }

    @Override
    public OnCreateReportEvent process() {
        OnCreateReportEvent event = new OnCreateReportEvent();
        DBItemHolder<Report> report = readReport();

        if (report != null) {
            return event;
        }

        DataSet dataSet = readDataSet();
        ArrayList<ContentProviderOperation> ops = insertReport(dataSet);
        try {
            getContext().getContentResolver().applyBatch(DBContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        return event;
    }

    private DataSet readDataSet() {
        final String KEY = KeyValueColumns.KEY + " = " + "'" + getEvent().getReport().getDataSet() + "'";
        final String TYPE = KeyValueColumns.TYPE + " = " + "'" + KeyValue.Type.DATASET.toString() + "'";
        final String SELECTION = KEY + " AND " + TYPE;

        Cursor cursor = getContext().getContentResolver().query(KeyValueColumns.CONTENT_URI,
                KeyValueHandler.PROJECTION, SELECTION, null, null);
        DataSet dataSet = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            DBItemHolder<KeyValue> keyValue = KeyValueHandler.fromCursor(cursor);
            cursor.close();

            if (keyValue != null && keyValue.getItem() != null) {
                String jDataSet = keyValue.getItem().getValue();
                Gson gson = new Gson();
                dataSet = gson.fromJson(jDataSet, DataSet.class);
            }
        }
        return dataSet;
    }

    private ArrayList<ContentProviderOperation> insertReport(DataSet dataSet) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ContentValues reportValues = ReportHandler.toContentValues(getEvent().getReport());
        ops.add(ContentProviderOperation.newInsert(ReportColumns.CONTENT_URI)
                .withValues(reportValues)
                .withValue(ReportColumns.STATE, State.OFFLINE.toString())
                .build());

        int reportIndex = ops.size() - 1;
        for (Group group : dataSet.getGroups()) {
            ContentValues groupValues = ReportGroupHandler.toContentValues(group);
            ops.add(ContentProviderOperation.newInsert(ReportGroupColumns.CONTENT_URI)
                    .withValues(groupValues)
                    .withValueBackReference(ReportGroupColumns.REPORT_DB_ID, reportIndex)
                    .build());

            int groupIndex = ops.size() - 1;
            for (Field field : group.getFields()) {
                ContentValues fieldValues = ReportFieldHandler.toContentValues(field);
                ops.add(ContentProviderOperation.newInsert(ReportFieldColumns.CONTENT_URI)
                        .withValues(fieldValues)
                        .withValueBackReference(ReportFieldColumns.GROUP_DB_ID, groupIndex)
                        .build());
            }
        }

        return ops;
    }

    private DBItemHolder<Report> readReport() {
        final String ORG_UNIT = ReportColumns.ORG_UNIT_ID + " = " + "'" + getEvent().getReport().getOrgUnit() + "'";
        final String DATASET = ReportColumns.DATASET_ID + " = " + "'" + getEvent().getReport().getDataSet() + "'";
        final String PERIOD = ReportColumns.PERIOD + " = " + "'" + getEvent().getReport().getPeriod() + "'";
        final String SELECTION = ORG_UNIT + " AND " + DATASET + " AND " + PERIOD;

        Cursor cursor = getContext().getContentResolver().query(ReportColumns.CONTENT_URI,
                ReportHandler.PROJECTION, SELECTION, null, null);

        DBItemHolder<Report> dbItem = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            dbItem = ReportHandler.fromCursor(cursor);
            cursor.close();
        }
        return dbItem;
    }
}
