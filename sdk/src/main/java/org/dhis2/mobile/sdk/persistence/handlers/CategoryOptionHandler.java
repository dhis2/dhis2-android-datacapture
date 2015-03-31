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

import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.network.managers.ILogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryOptions;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class CategoryOptionHandler {
    public static final String[] PROJECTION = {
            CategoryOptions.TABLE_NAME + "." + CategoryOptions.ID,
            CategoryOptions.TABLE_NAME + "." + CategoryOptions.CREATED,
            CategoryOptions.TABLE_NAME + "." + CategoryOptions.LAST_UPDATED,
            CategoryOptions.TABLE_NAME + "." + CategoryOptions.NAME,
            CategoryOptions.TABLE_NAME + "." + CategoryOptions.DISPLAY_NAME
    };

    private static final int ID = 0;
    private static final int CREATED = 1;
    private static final int LAST_UPDATED = 2;
    private static final int NAME = 3;
    private static final int DISPLAY_NAME = 4;

    private static final String TAG = CategoryOptionHandler.class.getSimpleName();
    private final Context mContext;
    private final ILogManager mLogManager;

    public CategoryOptionHandler(Context context,
                                 ILogManager logManager) {
        mContext = isNull(context, "Context object must not be null");
        mLogManager = isNull(logManager, "ILogManager object must not be null");
    }

    private static ContentValues toContentValues(CategoryOption categoryOption) {
        isNull(categoryOption, "CategoryOption object must not be null");
        ContentValues values = new ContentValues();
        values.put(CategoryOptions.ID, categoryOption.getId());
        values.put(CategoryOptions.CREATED, categoryOption.getCreated());
        values.put(CategoryOptions.LAST_UPDATED, categoryOption.getLastUpdated());
        values.put(CategoryOptions.NAME, categoryOption.getName());
        values.put(CategoryOptions.DISPLAY_NAME, categoryOption.getDisplayName());
        return values;
    }

    private static CategoryOption fromCursor(Cursor cursor) {
        isNull(cursor, "Cursor object must not be null");
        CategoryOption categoryOption = new CategoryOption();
        categoryOption.setId(cursor.getString(ID));
        categoryOption.setCreated(cursor.getString(CREATED));
        categoryOption.setLastUpdated(cursor.getString(LAST_UPDATED));
        categoryOption.setName(cursor.getString(NAME));
        categoryOption.setDisplayName(cursor.getString(DISPLAY_NAME));
        return categoryOption;
    }

    public static List<CategoryOption> map(Cursor cursor, boolean close) {
        List<CategoryOption> options = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                options.add(fromCursor(cursor));
            } while (cursor.moveToNext());

            if (close) {
                cursor.close();
            }
        }
        return options;
    }

    private void insert(List<ContentProviderOperation> ops,
                        CategoryOption catOption) {
        isNull(catOption, "CategoryOption object must not be null");

        mLogManager.LOGD(TAG, "Inserting " + catOption.getName());
        ops.add(ContentProviderOperation
                .newInsert(CategoryOptions.CONTENT_URI)
                .withValues(toContentValues(catOption))
                .build());
    }

    private void update(List<ContentProviderOperation> ops,
                        CategoryOption catOption) {
        isNull(catOption, "CategoryOption object must not be null");

        mLogManager.LOGD(TAG, "Updating " + catOption.getName());
        ops.add(ContentProviderOperation
                .newUpdate(CategoryOptions.CONTENT_URI)
                .withValues(toContentValues(catOption))
                .build());
    }

    private void delete(List<ContentProviderOperation> ops,
                        CategoryOption catOption) {
        isNull(catOption, "CategoryOption object must not be null");

        mLogManager.LOGD(TAG, "Deleting " + catOption.getName());
        Uri uri = CategoryOptions.CONTENT_URI.buildUpon()
                .appendPath(catOption.getId()).build();
        ops.add(ContentProviderOperation
                .newDelete(uri)
                .build());
    }

    public List<CategoryOption> query() {
        return query(null, null);
    }

    public List<CategoryOption> query(String selection, String[] selectionArgs) {
        Cursor cursor = mContext.getContentResolver().query(
                CategoryOptions.CONTENT_URI, PROJECTION, selection, selectionArgs, null
        );

        return map(cursor, true);
    }


    public List<ContentProviderOperation> sync(List<CategoryOption> categories) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        Map<String, CategoryOption> newCatOptions = toMap(categories);
        Map<String, CategoryOption> oldCatOptions = toMap(query());
        for (String oldCatOptionKey : oldCatOptions.keySet()) {
            CategoryOption newCatOption = newCatOptions.get(oldCatOptionKey);
            CategoryOption oldCatOption = oldCatOptions.get(oldCatOptionKey);

            if (newCatOption == null) {
                delete(ops, oldCatOption);
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newCatOption.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldCatOption.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                update(ops, newCatOption);
            }

            newCatOptions.remove(oldCatOptionKey);
        }

        for (String newCatOptionKey : newCatOptions.keySet()) {
            CategoryOption catOption = newCatOptions.get(newCatOptionKey);
            insert(ops, catOption);
        }

        return ops;
    }
}
