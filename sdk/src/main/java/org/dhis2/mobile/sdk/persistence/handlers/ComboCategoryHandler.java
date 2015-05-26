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
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.ComboCategories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryCombos.buildUriWithCategories;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class ComboCategoryHandler {
    private static final String TAG = ComboCategoryHandler.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
            ComboCategories.CATEGORY_COMBO_ID,
            ComboCategories.CATEGORY_ID
    };

    private static final int CATEGORY_COMBO_ID = 0;
    private static final int CATEGORY_ID = 1;

    private final Context mContext;
    private final ILogManager mLogManager;

    public ComboCategoryHandler(Context context,
                                ILogManager logManager) {
        mContext = context;
        mLogManager = logManager;
    }

    private static ContentValues toContentValues(String categoryComboId,
                                                 String categoryId) {
        isNull(categoryComboId, "CategoryCombo ID object must not be null");
        isNull(categoryId, "Category ID object must not be null");

        ContentValues values = new ContentValues();
        values.put(ComboCategories.CATEGORY_COMBO_ID, categoryComboId);
        values.put(ComboCategories.CATEGORY_ID, categoryId);
        return values;
    }

    private static Entry fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        Entry entry = new Entry();
        entry.setCategoryComboId(cursor.getString(CATEGORY_COMBO_ID));
        entry.setCategoryId(cursor.getString(CATEGORY_ID));
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
                        String categoryComboId, String categoryId) {
        isNull(categoryComboId, "CategoryCombo ID must not be null");
        isNull(categoryId, "Category ID must not be null");

        mLogManager.LOGD(TAG, "Inserting category " + "[" + categoryId + "]"
                + " for category combo " + "[" + categoryComboId + "]");
        ops.add(ContentProviderOperation
                .newInsert(buildUriWithCategories(categoryComboId))
                .withValues(toContentValues(categoryComboId, categoryId))
                .build());
    }

    public List<Category> queryCategories(String categoryCombo) {
        isNull(categoryCombo, "CategoryCombo ID must not be null");

        Cursor cursor = mContext.getContentResolver().query(
                buildUriWithCategories(categoryCombo), CategoryHandler.PROJECTION, null, null, null
        );
        return DbManager.with(Category.class).map(cursor, true);
    }

    public List<Entry> queryRelationShip() {
        Cursor cursor = mContext.getContentResolver().query(
                ComboCategories.CONTENT_URI, PROJECTION, null, null, null
        );
        return map(cursor, true);
    }

    private Set<String> buildRelationShipSet(List<Entry> entries) {
        Set<String> set = new HashSet<>();
        for (Entry entry : entries) {
            set.add(entry.getCategoryComboId() + entry.getCategoryId());
        }
        return set;
    }

    public List<ContentProviderOperation> sync(List<CategoryCombo> combos) {
        isNull(combos, "List<CategoryCombo> object must not be null");

        Set<String> set = buildRelationShipSet(queryRelationShip());
        List<ContentProviderOperation> ops = new ArrayList<>();
        for (CategoryCombo combo : combos) {
            for (Category category : combo.getCategories()) {
                if (!set.contains(combo.getId() + category.getId())) {
                    insert(ops, combo.getId(), category.getId());
                }
            }
        }
        return ops;
    }

    private static class Entry {
        private String categoryComboId;
        private String categoryId;

        public String getCategoryComboId() {
            return categoryComboId;
        }

        public void setCategoryComboId(String categoryComboId) {
            this.categoryComboId = categoryComboId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

    }
}

