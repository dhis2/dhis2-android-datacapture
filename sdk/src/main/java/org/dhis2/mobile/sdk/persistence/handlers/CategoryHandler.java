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

import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.Categories;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class CategoryHandler {
    public static final String[] PROJECTION = {
            Categories.ID,
            Categories.CREATED,
            Categories.LAST_UPDATED,
            Categories.NAME,
            Categories.DISPLAY_NAME,
            Categories.DATA_DIMENSION,
            Categories.DATA_DIMENSION_TYPE,
            Categories.DIMENSION
    };

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;
    private static final int DATA_DIMENSION = 5;
    private static final int DATA_DIMENSION_TYPE = 6;
    private static final int DIMENSION = 7;

    private static final String TAG = CategoryHandler.class.getSimpleName();

    private final Context mContext;
    private final ILogManager mLogManager;

    public CategoryHandler(Context context, ILogManager logManager) {
        mContext = isNull(context, "Context object must not be null");
        mLogManager = isNull(logManager, "ILogManager must not be null");
    }

    private static ContentValues toContentValues(Category category) {
        isNull(category, "Category object must not be null");
        ContentValues values = new ContentValues();
        values.put(Categories.ID, category.getId());
        values.put(Categories.CREATED, category.getCreated());
        values.put(Categories.LAST_UPDATED, category.getLastUpdated());
        values.put(Categories.NAME, category.getName());
        values.put(Categories.DISPLAY_NAME, category.getDisplayName());
        values.put(Categories.DATA_DIMENSION, category.getDataDimension());
        values.put(Categories.DATA_DIMENSION_TYPE, category.getDataDimensionType());
        values.put(Categories.DIMENSION, category.getDataDimension());
        return values;
    }

    private static Category fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        Category category = new Category();
        category.setId(cursor.getString(ID));
        category.setCreated(cursor.getString(CREATED));
        category.setLastUpdated(cursor.getString(LAST_UPDATED));
        category.setName(cursor.getString(NAME));
        category.setDisplayName(cursor.getString(DISPLAY_NAME));
        category.setDataDimension(cursor.getString(DATA_DIMENSION));
        category.setDataDimensionType(cursor.getString(DATA_DIMENSION_TYPE));
        category.setDimension(cursor.getString(DIMENSION));
        return category;
    }

    public static List<Category> map(Cursor cursor, boolean close) {
        List<Category> categories = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                categories.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }

        return categories;
    }

    private void insert(List<ContentProviderOperation> ops,
                        Category category) {
        isNull(category, "Category object must not be null");

        mLogManager.LOGD(TAG, "Inserting " + category.getName());
        ops.add(ContentProviderOperation
                .newInsert(Categories.CONTENT_URI)
                .withValues(toContentValues(category))
                .build());
    }

    private void update(List<ContentProviderOperation> ops,
                        Category category) {
        isNull(category, "Category object must not be null");

        mLogManager.LOGD(TAG, "Updating " + category.getName());
        ops.add(ContentProviderOperation
                .newUpdate(Categories.CONTENT_URI)
                .withValues(toContentValues(category))
                .build());
    }

    private void delete(List<ContentProviderOperation> ops,
                        Category category) {
        isNull(category, "Category object must not be null");

        mLogManager.LOGD(TAG, "Deleting " + category.getName());
        Uri uri = Categories.CONTENT_URI.buildUpon()
                .appendPath(category.getId()).build();
        ops.add(ContentProviderOperation
                .newDelete(uri)
                .build());
    }

    public List<Category> query() {
        return query(null, null);
    }

    public List<Category> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                Categories.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );
        return map(cursor, true);
    }

    public List<ContentProviderOperation> sync(List<Category> categories) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, Category> newCats = toMap(categories);
        Map<String, Category> oldCats = toMap(query());
        for (String oldCatKey : oldCats.keySet()) {
            Category newCat = newCats.get(oldCatKey);
            Category oldCat = oldCats.get(oldCatKey);

            if (newCat == null) {
                delete(ops, oldCat);
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newCat.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldCat.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                update(ops, newCat);
            }

            newCats.remove(oldCatKey);
        }

        for (String newCatKey : newCats.keySet()) {
            Category category = newCats.get(newCatKey);
            insert(ops, category);
        }

        return ops;
    }
}
