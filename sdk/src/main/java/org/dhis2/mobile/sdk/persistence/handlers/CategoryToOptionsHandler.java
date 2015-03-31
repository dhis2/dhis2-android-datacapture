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

import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryToOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.database.DbContract.Categories.buildUriWithOptions;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class CategoryToOptionsHandler {
    private static final String TAG = CategoryToOptionsHandler.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
            CategoryToOptions.CATEGORY_ID,
            CategoryToOptions.CATEGORY_OPTION_ID
    };

    private static final int CATEGORY_ID = 0;
    private static final int CATEGORY_OPTION_ID = 1;

    private final Context mContext;
    private final ILogManager mLogManager;

    public CategoryToOptionsHandler(Context context,
                                    ILogManager logManager) {
        mContext = context;
        mLogManager = logManager;
    }

    private static ContentValues toContentValues(String categoryId,
                                                 String categoryOptionId) {
        isNull(categoryId, "Category ID object must not be null");
        isNull(categoryOptionId, "CategoryOption ID object must not be null");

        ContentValues values = new ContentValues();
        values.put(CategoryToOptions.CATEGORY_ID, categoryId);
        values.put(CategoryToOptions.CATEGORY_OPTION_ID, categoryOptionId);
        return values;
    }

    private static Entry fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        Entry entry = new Entry();
        entry.setCategoryId(cursor.getString(CATEGORY_ID));
        entry.setCategoryOptionId(cursor.getString(CATEGORY_OPTION_ID));
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
                        String categoryId, String categoryOptionId) {
        isNull(categoryId, "Category ID must not be null");
        isNull(categoryOptionId, "CategoryOption ID must not be null");

        mLogManager.LOGD(TAG, "Inserting category option" + "[" + categoryOptionId + "]"
                + " for category " + "[" + categoryId + "]");
        ops.add(ContentProviderOperation
                .newInsert(buildUriWithOptions(categoryId))
                .withValues(toContentValues(categoryId, categoryOptionId))
                .build());
    }

    public List<CategoryOption> queryCategoryOptions(String category) {
        isNull(category, "Category ID must not be null");

        Cursor cursor = mContext.getContentResolver().query(
                buildUriWithOptions(category), CategoryOptionHandler.PROJECTION, null, null, null
        );
        return CategoryOptionHandler.map(cursor, true);
    }

    public List<Entry> queryRelationShip() {
        Cursor cursor = mContext.getContentResolver().query(
                CategoryToOptions.CONTENT_URI, PROJECTION, null, null, null
        );
        return map(cursor, true);
    }

    private Set<String> buildRelationShipSet(List<Entry> entries) {
        Set<String> set = new HashSet<>();
        for (Entry entry : entries) {
            set.add(entry.getCategoryId() + entry.getCategoryOptionId());
        }
        return set;
    }

    public List<ContentProviderOperation> sync(List<Category> categories) {
        isNull(categories, "List<Category> object must not be null");

        Set<String> set = buildRelationShipSet(queryRelationShip());
        List<ContentProviderOperation> ops = new ArrayList<>();
        for (Category category : categories) {
            for (CategoryOption option : category.getCategoryOptions()) {
                if (!set.contains(category.getId() + option.getId())) {
                    insert(ops, category.getId(), option.getId());
                }
            }
        }
        return ops;
    }

    private static class Entry {
        private String categoryId;
        private String categoryOptionId;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryOptionId() {
            return categoryOptionId;
        }

        public void setCategoryOptionId(String categoryOptionId) {
            this.categoryOptionId = categoryOptionId;
        }

    }
}

