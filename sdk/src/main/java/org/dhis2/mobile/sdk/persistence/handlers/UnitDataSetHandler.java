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

import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.UnitDataSets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.database.DbContract.OrganisationUnits.buildUriWithDataSets;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class UnitDataSetHandler {
    private static final String TAG = UnitDataSetHandler.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
            UnitDataSets.ORGANISATION_UNIT_ID,
            UnitDataSets.DATA_SET_ID
    };

    private static final int ORGANISATION_UNIT_ID = 0;
    private static final int DATA_SET_ID = 1;

    private final Context mContext;
    private final ILogManager mLogManager;

    public UnitDataSetHandler(Context context,
                              ILogManager logManager) {
        mContext = context;
        mLogManager = logManager;
    }

    private static ContentValues toContentValues(String orgUnitId, String dataSetId) {
        isNull(orgUnitId, "OrganisationUnit ID object must not be null");
        isNull(orgUnitId, "DataSet ID object must not be null");

        ContentValues values = new ContentValues();
        values.put(UnitDataSets.ORGANISATION_UNIT_ID, orgUnitId);
        values.put(UnitDataSets.DATA_SET_ID, dataSetId);
        return values;
    }

    private static Entry fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        Entry entry = new Entry();
        entry.setOrgUnit(cursor.getString(ORGANISATION_UNIT_ID));
        entry.setDataSet(cursor.getString(DATA_SET_ID));
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
                        String orgUnitId, String dataSetId) {
        isNull(orgUnitId, "OrganisationUnit ID must not be null");
        isNull(dataSetId, "DataSet ID must not be null");

        mLogManager.LOGD(TAG, "Inserting dataset " + "[" + dataSetId + "]"
                + " for organisation unit " + "[" + orgUnitId + "]");
        ops.add(ContentProviderOperation
                .newInsert(buildUriWithDataSets(orgUnitId))
                .withValues(toContentValues(orgUnitId, dataSetId))
                .build());
    }

    public List<DataSet> queryDataSets(String orgUnitId) {
        isNull(orgUnitId, "OrganisationUnit ID must not be null");

        Cursor cursor = mContext.getContentResolver().query(
                buildUriWithDataSets(orgUnitId), DataSetHandler.PROJECTION, null, null, null
        );
        return DataSetHandler.map(cursor, true);
    }

    public List<Entry> queryRelationShip() {
        Cursor cursor = mContext.getContentResolver().query(
                UnitDataSets.CONTENT_URI, PROJECTION, null, null, null
        );
        return map(cursor, true);
    }

    private Set<String> buildRelationShipSet(List<Entry> entries) {
        Set<String> set = new HashSet<>();
        for (Entry entry : entries) {
            set.add(entry.getOrgUnit() + entry.getDataSet());
        }
        return set;
    }

    public List<ContentProviderOperation> sync(List<OrganisationUnit> units) {
        isNull(units, "List<OrganisationUnit> object must not be null");

        Set<String> set = buildRelationShipSet(queryRelationShip());
        List<ContentProviderOperation> ops = new ArrayList<>();
        for (OrganisationUnit unit : units) {
            for (DataSet dataSet : unit.getDataSets()) {
                if (!set.contains(unit.getId() + dataSet.getId())) {
                    insert(ops, unit.getId(), dataSet.getId());
                }
            }
        }
        return ops;
    }

    private static class Entry {
        private String orgUnit;
        private String dataSet;

        public String getOrgUnit() {
            return orgUnit;
        }

        public void setOrgUnit(String orgUnit) {
            this.orgUnit = orgUnit;
        }

        public String getDataSet() {
            return dataSet;
        }

        public void setDataSet(String dataSet) {
            this.dataSet = dataSet;
        }

    }
}

