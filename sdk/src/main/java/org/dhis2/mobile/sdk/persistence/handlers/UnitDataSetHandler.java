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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.UnitDataSetRelation;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.UnitDataSets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.persistence.database.DbContract.OrganisationUnits.buildUriWithDataSets;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

final class UnitDataSetHandler implements IModelHandler<UnitDataSetRelation> {
    private static final String TAG = UnitDataSetHandler.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
            UnitDataSets.ID,
            UnitDataSets.ORGANISATION_UNIT_ID,
            UnitDataSets.DATA_SET_ID
    };

    private static final int ID = 0;
    private static final int ORGANISATION_UNIT_ID = 1;
    private static final int DATA_SET_ID = 2;

    private final Context mContext;
    private final ILogManager mLogManager;

    public UnitDataSetHandler(Context context, ILogManager logManager) {
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

    private static UnitDataSetRelation fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        UnitDataSetRelation relation = new UnitDataSetRelation();
        relation.setId(cursor.getInt(ID));
        relation.setOrgUnitId(cursor.getString(ORGANISATION_UNIT_ID));
        relation.setDataSetId(cursor.getString(DATA_SET_ID));
        return relation;
    }

    @Override
    public List<UnitDataSetRelation> map(Cursor cursor, boolean close) {
        List<UnitDataSetRelation> relations = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    relations.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && close) {
                cursor.close();
            }
        }
        return relations;
    }

    @Override
    public UnitDataSetRelation mapSingleItem(Cursor cursor, boolean closeCursor) {
        throw new UnsupportedOperationException("Unsupported method");
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public ContentProviderOperation insert(UnitDataSetRelation relation) {
        isNull(relation, "UnitDataSetRelation object must not be null");

        mLogManager.LOGD(TAG, "Inserting dataset " + "[" + relation.getDataSetId() + "]"
                + " for organisation unit " + "[" + relation.getOrgUnitId() + "]");
        return ContentProviderOperation
                .newInsert(buildUriWithDataSets(relation.getOrgUnitId()))
                .withValues(toContentValues(relation.getOrgUnitId(), relation.getDataSetId()))
                .build();
    }

    @Override
    public ContentProviderOperation update(UnitDataSetRelation object) {
        throw new UnsupportedOperationException("Unsupported method");
    }

    @Override
    public ContentProviderOperation delete(UnitDataSetRelation relation) {
        isNull(relation, "UnitDataSetRelation object must not be null");

        mLogManager.LOGD(TAG, "Deleting dataset " + "[" + relation.getDataSetId() + "]"
                + " from organisation unit " + "[" + relation.getOrgUnitId() + "]");
        return ContentProviderOperation
                .newDelete(ContentUris.withAppendedId(UnitDataSets.CONTENT_URI, relation.getId()))
                .withValues(toContentValues(relation.getOrgUnitId(), relation.getDataSetId()))
                .build();
    }

    @Override
    public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        isNull(id, "Object ID must not be null");

        if (clazz == DataSet.class) {
            Cursor cursor = mContext.getContentResolver().query(
                    buildUriWithDataSets((String) id), DbManager.with(DataSet.class).getProjection(), null, null, null
            );

            return (List<T>) DbManager.with(DataSet.class).map(cursor, true);
        } else {
            throw new UnsupportedOperationException("Unsupported model class");
        }
    }

    @Override
    public List<UnitDataSetRelation> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                UnitDataSets.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );
        return map(cursor, true);
    }

    @Override
    public List<UnitDataSetRelation> query() {
        return query(null, null);
    }

    @Override
    public List<ContentProviderOperation> sync(List<UnitDataSetRelation> newRelationsList) {
        isNull(newRelationsList, "List<UnitDataSetRelation> object must not be null");

        List<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, UnitDataSetRelation> oldRelations = toMap(query());
        Map<String, UnitDataSetRelation> newRelations = toMap(newRelationsList);
        for (String oldRelationKey : oldRelations.keySet()) {
            UnitDataSetRelation oldRelation = oldRelations.get(oldRelationKey);
            UnitDataSetRelation newRelation = newRelations.get(oldRelationKey);

            if (newRelation == null) {
                ops.add(delete(oldRelation));
                continue;
            }

            newRelations.remove(oldRelationKey);
        }

        for (String newRelationKey : newRelations.keySet()) {
            ops.add(insert(newRelations.get(newRelationKey)));
        }

        return ops;
    }

    private static Map<String, UnitDataSetRelation> toMap(List<UnitDataSetRelation> relations) {
        Map<String, UnitDataSetRelation> relationMap = new HashMap<>();
        if (relations != null && !relations.isEmpty()) {
            for (UnitDataSetRelation relation : relations) {
                relationMap.put(relation.getOrgUnitId() + relation.getDataSetId(), relation);
            }
        }
        return relationMap;
    }
}

