/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.sdk.persistence.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSets;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

class DataSetHandler implements IModelHandler<DataSet> {
    private static final String[] PROJECTION = new String[]{
            DataSets.TABLE_NAME + "." + DataSets.ID,
            DataSets.TABLE_NAME + "." + DataSets.CREATED,
            DataSets.TABLE_NAME + "." + DataSets.LAST_UPDATED,
            DataSets.TABLE_NAME + "." + DataSets.NAME,
            DataSets.TABLE_NAME + "." + DataSets.DISPLAY_NAME,
            DataSets.TABLE_NAME + "." + DataSets.VERSION,
            DataSets.TABLE_NAME + "." + DataSets.EXPIRY_DAYS,
            DataSets.TABLE_NAME + "." + DataSets.ALLOW_FUTURE_PERIODS,
            DataSets.TABLE_NAME + "." + DataSets.PERIOD_TYPE,
    };
    private static final String TAG = DataSetHandler.class.getSimpleName();

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int VERSION = 5;
    private static final int EXPIRY_DAYS = 6;
    private static final int ALLOW_FUTURE_PERIODS = 7;
    private static final int PERIOD_TYPE = 8;

    private Context mContext;
    private ILogManager mLogManager;

    public DataSetHandler(Context context, ILogManager logManager) {
        mContext = isNull(context, "Context object must not be null");
        mLogManager = isNull(logManager, "LogManager object must not be null");
    }

    private static ContentValues toContentValues(DataSet dataSet) {
        isNull(dataSet, "DataSet object must not be null");
        ContentValues values = new ContentValues();
        values.put(DataSets.ID, dataSet.getId());
        values.put(DataSets.CREATED, dataSet.getCreated());
        values.put(DataSets.LAST_UPDATED, dataSet.getLastUpdated());
        values.put(DataSets.NAME, dataSet.getName());
        values.put(DataSets.DISPLAY_NAME, dataSet.getDisplayName());
        values.put(DataSets.VERSION, dataSet.getVersion());
        values.put(DataSets.EXPIRY_DAYS, dataSet.getExpiryDays());
        values.put(DataSets.ALLOW_FUTURE_PERIODS, dataSet.isAllowFuturePeriods() ? 1 : 0);
        values.put(DataSets.PERIOD_TYPE, dataSet.getPeriodType());
        return values;
    }

    private static DataSet fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        DataSet dataSet = new DataSet();
        dataSet.setId(cursor.getString(ID));
        dataSet.setCreated(cursor.getString(CREATED));
        dataSet.setLastUpdated(cursor.getString(LAST_UPDATED));
        dataSet.setName(cursor.getString(NAME));
        dataSet.setDisplayName(cursor.getString(DISPLAY_NAME));
        dataSet.setVersion(cursor.getInt(VERSION));
        dataSet.setExpiryDays(cursor.getInt(EXPIRY_DAYS));
        dataSet.setAllowFuturePeriods(cursor.getInt(ALLOW_FUTURE_PERIODS) == 1);
        dataSet.setPeriodType(cursor.getString(PERIOD_TYPE));
        return dataSet;
    }

    @Override
    public List<DataSet> map(Cursor cursor, boolean close) {
        List<DataSet> dataSets = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                dataSets.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }
        return dataSets;
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public ContentProviderOperation insert(DataSet dataSet) {
        isNull(dataSet, "DataSet object must not be null");

        mLogManager.LOGD(TAG, "Inserting " + dataSet.getName());
        return ContentProviderOperation
                .newInsert(DataSets.CONTENT_URI)
                .withValues(toContentValues(dataSet))
                .build();
    }

    @Override
    public ContentProviderOperation update(DataSet dataSet) {
        isNull(dataSet, "DataSet object must not be null");

        mLogManager.LOGD(TAG, "Updating " + dataSet.getName());
        return ContentProviderOperation
                .newUpdate(DataSets.CONTENT_URI)
                .withValues(toContentValues(dataSet))
                .build();
    }

    @Override
    public ContentProviderOperation delete(DataSet dataSet) {
        isNull(dataSet, "DataSet object must not be null");

        mLogManager.LOGD(TAG, "Deleting " + dataSet.getName());
        Uri uri = DataSets.CONTENT_URI.buildUpon()
                .appendPath(dataSet.getId()).build();
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override
    public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        return null;
    }

    @Override
    public DataSet mapSingleItem(Cursor cursor, boolean close) {
        DataSet dataSet = null;
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                dataSet = fromCursor(cursor);
            }
        } finally {
            if (cursor != null && close) {
                cursor.close();
            }
        }

        return dataSet;
    }

    @Override
    public List<DataSet> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                DataSets.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );

        return map(cursor, true);
    }

    @Override
    public List<DataSet> query() {
        return query(null, null);
    }

    @Override
    public List<ContentProviderOperation> sync(List<DataSet> dataSets) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, DataSet> newDataSets = toMap(dataSets);
        Map<String, DataSet> oldDataSets = toMap(query());
        for (String oldDataSetKey : oldDataSets.keySet()) {
            DataSet newDataSet = newDataSets.get(oldDataSetKey);
            DataSet oldDataSet = oldDataSets.get(oldDataSetKey);

            if (newDataSet == null) {
                ops.add(delete(oldDataSet));
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newDataSet.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldDataSet.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                ops.add(update(newDataSet));
            }

            newDataSets.remove(oldDataSetKey);
        }

        for (String newDataSetKey : newDataSets.keySet()) {
            DataSet dataSet = newDataSets.get(newDataSetKey);
            ops.add(insert(dataSet));
        }

        return ops;
    }
}
