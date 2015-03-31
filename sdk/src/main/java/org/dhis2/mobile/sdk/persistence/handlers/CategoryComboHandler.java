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

import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryCombos;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class CategoryComboHandler {
    public static final String[] PROJECTION = {
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.ID,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.CREATED,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.LAST_UPDATED,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.NAME,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.DISPLAY_NAME,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.DIMENSION_TYPE,
            CategoryCombos.TABLE_NAME + "." + CategoryCombos.SKIP_TOTAL
    };

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int DIMENSION_TYPE = 5;
    private static final int SKIP_TOTAL = 6;

    private static final String TAG = CategoryComboHandler.class.getSimpleName();

    private final Context mContext;
    private final ILogManager mLogManager;

    public CategoryComboHandler(Context context,
                                ILogManager logManager) {
        mContext = isNull(context, "Context object must not be null");
        mLogManager = isNull(logManager, "ILogManager object must not be null");
    }

    private static ContentValues toContentValues(CategoryCombo categoryCombo) {
        ContentValues values = new ContentValues();
        values.put(CategoryCombos.ID, categoryCombo.getId());
        values.put(CategoryCombos.CREATED, categoryCombo.getCreated());
        values.put(CategoryCombos.LAST_UPDATED, categoryCombo.getLastUpdated());
        values.put(CategoryCombos.NAME, categoryCombo.getName());
        values.put(CategoryCombos.DISPLAY_NAME, categoryCombo.getDisplayName());
        values.put(CategoryCombos.DIMENSION_TYPE, categoryCombo.getDimensionType());
        values.put(CategoryCombos.SKIP_TOTAL, categoryCombo.isSkipTotal() ? 1 : 0);
        return values;
    }

    private static CategoryCombo fromCursor(Cursor cursor) {
        CategoryCombo categoryCombo = new CategoryCombo();
        categoryCombo.setId(cursor.getString(ID));
        categoryCombo.setCreated(cursor.getString(CREATED));
        categoryCombo.setLastUpdated(cursor.getString(LAST_UPDATED));
        categoryCombo.setName(cursor.getString(NAME));
        categoryCombo.setDisplayName(cursor.getString(DISPLAY_NAME));
        categoryCombo.setDimensionType(cursor.getString(DIMENSION_TYPE));
        categoryCombo.setSkipTotal(cursor.getInt(SKIP_TOTAL) == 1);
        return categoryCombo;
    }

    public static List<CategoryCombo> map(Cursor cursor, boolean close) {
        List<CategoryCombo> categoryCombos = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                categoryCombos.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }
        return categoryCombos;
    }

    private void insert(List<ContentProviderOperation> ops,
                        CategoryCombo categoryCombo) {
        isNull(categoryCombo, "CategoryCombo object must not be null");

        mLogManager.LOGD(TAG, "Inserting " + categoryCombo.getName());
        ops.add(ContentProviderOperation
                .newInsert(CategoryCombos.CONTENT_URI)
                .withValues(toContentValues(categoryCombo))
                .build());
    }

    private void update(List<ContentProviderOperation> ops,
                        CategoryCombo categoryCombo) {
        isNull(categoryCombo, "CategoryCombo object must not be null");

        mLogManager.LOGD(TAG, "Updating " + categoryCombo.getName());
        ops.add(ContentProviderOperation
                .newUpdate(CategoryCombos.CONTENT_URI)
                .withValues(toContentValues(categoryCombo))
                .build());
    }

    private void delete(List<ContentProviderOperation> ops,
                        CategoryCombo categoryCombo) {
        isNull(categoryCombo, "CategoryCombo object must not be null");

        mLogManager.LOGD(TAG, "Deleting " + categoryCombo.getName());
        Uri uri = CategoryCombos.CONTENT_URI.buildUpon()
                .appendPath(categoryCombo.getId()).build();
        ops.add(ContentProviderOperation
                .newDelete(uri)
                .build());
    }

    public List<CategoryCombo> query() {
        return query(null, null);
    }

    public List<CategoryCombo> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                CategoryCombos.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );
        return map(cursor, true);
    }

    public List<ContentProviderOperation> sync(List<CategoryCombo> categoryCombos) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, CategoryCombo> newCatCombos = toMap(categoryCombos);
        Map<String, CategoryCombo> oldCatCombos = toMap(query());
        for (String oldCatComboKey : oldCatCombos.keySet()) {
            CategoryCombo newCatCombo = newCatCombos.get(oldCatComboKey);
            CategoryCombo oldCatCombo = oldCatCombos.get(oldCatComboKey);

            if (newCatCombo == null) {
                delete(ops, oldCatCombo);
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newCatCombo.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldCatCombo.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                update(ops, newCatCombo);
            }

            newCatCombos.remove(oldCatComboKey);
        }

        for (String newCatComboKey : newCatCombos.keySet()) {
            CategoryCombo categoryCombo = newCatCombos.get(newCatComboKey);
            insert(ops, categoryCombo);
        }

        return ops;
    }
}
