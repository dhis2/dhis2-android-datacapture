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

import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSetCategoryCombos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.database.DbContract.DataSets.buildUriWithCategoryCombos;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class DataSetCategoryComboHandler {
    public static final String[] PROJECTION = {
            DataSetCategoryCombos.DATA_SET_ID,
            DataSetCategoryCombos.CATEGORY_COMBO_ID
    };
    private static final String TAG = DataSetCategoryComboHandler.class.getSimpleName();
    private static final int DATA_SET_ID = 0;
    private static final int CATEGORY_COMBO_ID = 1;

    private final Context mContext;
    private final ILogManager mLogManager;

    public DataSetCategoryComboHandler(Context context,
                                       ILogManager logManager) {
        mContext = isNull(context, "Context object must not be null");
        mLogManager = isNull(logManager, "ILogManager must not be null");
    }

    private static ContentValues toContentValues(String dataSetId, String categoryComboId) {
        isNull(dataSetId, "DataSet ID must not be null");
        isNull(categoryComboId, "CategoryCombo ID must not be null");
        ContentValues values = new ContentValues();
        values.put(DataSetCategoryCombos.DATA_SET_ID, dataSetId);
        values.put(DataSetCategoryCombos.CATEGORY_COMBO_ID, categoryComboId);
        return values;
    }

    private static Entry fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        Entry entry = new Entry();
        entry.setDataSetId(cursor.getString(DATA_SET_ID));
        entry.setCategoryComboId(cursor.getString(CATEGORY_COMBO_ID));
        return entry;
    }

    private static List<Entry> map(Cursor cursor, boolean close) {
        List<Entry> entries = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                entries.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }
        return entries;
    }

    private void insert(List<ContentProviderOperation> ops,
                        String dataSetId, String categoryComboId) {
        isNull(dataSetId, "DataSet ID must not be null");
        isNull(categoryComboId, "CategoryCombo ID must not be null");

        mLogManager.LOGD(TAG, "Inserting category combo " + "[" + categoryComboId + "]"
                + " for dataset " + "[" + dataSetId + "]");
        ops.add(ContentProviderOperation
                .newInsert(buildUriWithCategoryCombos(dataSetId))
                .withValues(toContentValues(dataSetId, categoryComboId))
                .build());
    }

    public List<CategoryCombo> queryCategoryCombos(String dataSetId) {
        isNull(dataSetId, "DataSet ID must not be null");

        Cursor cursor = mContext.getContentResolver().query(
                buildUriWithCategoryCombos(dataSetId), CategoryComboHandler.PROJECTION,
                null, null, null
        );
        return CategoryComboHandler.map(cursor, true);
    }

    public List<Entry> queryRelationShip() {
        Cursor cursor = mContext.getContentResolver().query(
                DataSetCategoryCombos.CONTENT_URI, PROJECTION, null, null, null
        );
        return map(cursor, true);
    }

    private Set<String> buildRelationShipSet(List<Entry> entries) {
        Set<String> set = new HashSet<>();
        for (Entry entry : entries) {
            set.add(entry.getDataSetId() + entry.getCategoryComboId());
        }
        return set;
    }

    public List<ContentProviderOperation> sync(List<DataSet> dataSets) {
        isNull(dataSets, "List<DataSet> object must not be null");

        Set<String> set = buildRelationShipSet(queryRelationShip());
        List<ContentProviderOperation> ops = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            CategoryCombo categoryCombo = dataSet.getCategoryCombo();
            if (categoryCombo != null && !set.contains(dataSet.getId() + categoryCombo.getId())) {
                insert(ops, dataSet.getId(), categoryCombo.getId());
            }
        }
        return ops;
    }

    private static class Entry {
        private String dataSetId;
        private String categoryComboId;

        public String getDataSetId() {
            return dataSetId;
        }

        public void setDataSetId(String dataSetId) {
            this.dataSetId = dataSetId;
        }

        public String getCategoryComboId() {
            return categoryComboId;
        }

        public void setCategoryComboId(String categoryComboId) {
            this.categoryComboId = categoryComboId;
        }
    }
}
