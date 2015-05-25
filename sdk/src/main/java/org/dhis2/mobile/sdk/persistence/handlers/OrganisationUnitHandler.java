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
import android.util.Log;

import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.database.DbContract.OrganisationUnits;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

final class OrganisationUnitHandler implements IModelHandler<OrganisationUnit> {
    private static final String[] PROJECTION = new String[]{
            OrganisationUnits.ID,
            OrganisationUnits.CREATED,
            OrganisationUnits.LAST_UPDATED,
            OrganisationUnits.NAME,
            OrganisationUnits.DISPLAY_NAME,
            OrganisationUnits.LEVEL
    };
    private static final String TAG = OrganisationUnitHandler.class.getSimpleName();
    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int LEVEL = 5;

    private Context mContext;

    public OrganisationUnitHandler(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    private static ContentValues toContentValues(OrganisationUnit unit) {
        isNull(unit, "OrganizationUnit object must not be null");

        ContentValues values = new ContentValues();
        values.put(OrganisationUnits.ID, unit.getId());
        values.put(OrganisationUnits.CREATED, unit.getCreated());
        values.put(OrganisationUnits.LAST_UPDATED, unit.getLastUpdated());
        values.put(OrganisationUnits.NAME, unit.getName());
        values.put(OrganisationUnits.DISPLAY_NAME, unit.getDisplayName());
        values.put(OrganisationUnits.LEVEL, unit.getLevel());
        return values;
    }

    private static OrganisationUnit fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        OrganisationUnit unit = new OrganisationUnit();
        unit.setId(cursor.getString(ID));
        unit.setCreated(cursor.getString(CREATED));
        unit.setLastUpdated(cursor.getString(LAST_UPDATED));
        unit.setName(cursor.getString(NAME));
        unit.setDisplayName(cursor.getString(DISPLAY_NAME));
        unit.setLevel(cursor.getInt(LEVEL));
        return unit;
    }

    @Override
    public List<OrganisationUnit> map(Cursor cursor, boolean closeCursor) {
        List<OrganisationUnit> units = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    units.add(fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return units;
    }

    @Override
    public OrganisationUnit mapSingleItem(Cursor cursor, boolean closeCursor) {
        OrganisationUnit organisationUnit = null;
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                organisationUnit = fromCursor(cursor);
            }
        } finally {
            if (cursor != null && closeCursor) {
                cursor.close();
            }
        }
        return organisationUnit;
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public ContentProviderOperation insert(OrganisationUnit unit) {
        isNull(unit, "OrganizationUnit must not be null");

        Log.d(TAG, "Inserting " + unit.getName());
        return ContentProviderOperation
                .newInsert(OrganisationUnits.CONTENT_URI)
                .withValues(toContentValues(unit))
                .build();
    }

    @Override
    public ContentProviderOperation update(OrganisationUnit unit) {
        isNull(unit, "OrganizationUnit must not be null");

        Log.d(TAG, "Updating " + unit.getName());
        Uri uri = OrganisationUnits.CONTENT_URI.buildUpon()
                .appendPath(unit.getId()).build();
        return ContentProviderOperation
                .newUpdate(uri)
                .withValues(toContentValues(unit))
                .build();
    }

    @Override
    public ContentProviderOperation delete(OrganisationUnit unit) {
        isNull(unit, "OrganizationUnit must not be null");

        Log.d(TAG, "Deleting " + unit.getName());
        Uri uri = OrganisationUnits.CONTENT_URI.buildUpon()
                .appendPath(unit.getId()).build();
        return ContentProviderOperation
                .newDelete(uri)
                .build();
    }

    @Override
    public <T> List<T> queryRelatedModels(Class<T> clazz, Object id) {
        return null;
    }

    @Override
    public List<OrganisationUnit> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                OrganisationUnits.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );

        return map(cursor, true);
    }

    @Override
    public List<OrganisationUnit> query() {
        return query(null, null);
    }

    @Override
    public List<ContentProviderOperation> sync(List<OrganisationUnit> units) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, OrganisationUnit> newUnits = toMap(units);
        Map<String, OrganisationUnit> oldUnits = toMap(query());
        for (String oldOrgUnitKey : oldUnits.keySet()) {
            OrganisationUnit newUnit = newUnits.get(oldOrgUnitKey);
            OrganisationUnit oldUnit = oldUnits.get(oldOrgUnitKey);

            if (newUnit == null) {
                ops.add(delete(oldUnit));
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newUnit.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldUnit.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                ops.add(update(newUnit));
            }

            newUnits.remove(oldOrgUnitKey);
        }

        for (String newOrgUnitKey : newUnits.keySet()) {
            OrganisationUnit orgUnit = newUnits.get(newOrgUnitKey);
            ops.add(insert(orgUnit));
        }

        return ops;
    }
}
