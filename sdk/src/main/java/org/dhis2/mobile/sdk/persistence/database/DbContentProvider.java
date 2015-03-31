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

package org.dhis2.mobile.sdk.persistence.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.dhis2.mobile.sdk.persistence.database.DbContract.Categories;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryCombos;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryOptions;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryToOptions;
import org.dhis2.mobile.sdk.persistence.database.DbContract.ComboCategories;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSetCategoryCombos;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSets;
import org.dhis2.mobile.sdk.persistence.database.DbContract.OrganisationUnits;
import org.dhis2.mobile.sdk.persistence.database.DbContract.UnitDataSets;

import java.util.ArrayList;

import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;

public final class DbContentProvider extends ContentProvider {
    private static final int ORGANISATION_UNITS = 100;
    private static final int ORGANISATION_UNIT_ID = 101;
    private static final int ORGANISATION_UNIT_ID_DATASETS = 102;

    private static final int DATA_SETS = 200;
    private static final int DATA_SET_ID = 201;
    private static final int DATA_SET_ID_CATEGORY_COMBOS = 202;

    private static final int UNIT_DATA_SETS = 300;
    private static final int UNIT_DATA_SETS_ID = 301;

    private static final int CATEGORY_COMBOS = 400;
    private static final int CATEGORY_COMBO_ID = 401;
    private static final int CATEGORY_COMBO_ID_CATEGORIES = 402;

    private static final int DATA_SET_CATEGORY_COMBOS = 700;
    private static final int DATA_SET_CATEGORY_COMBOS_ID = 701;

    private static final int CATEGORIES = 500;
    private static final int CATEGORY_ID = 501;
    private static final int CATEGORY_ID_OPTIONS = 502;

    private static final int COMBO_CATEGORIES = 800;
    private static final int COMBO_CATEGORIES_ID = 801;

    private static final int CATEGORY_OPTIONS = 600;
    private static final int CATEGORY_OPTION_ID = 601;

    private static final int CATEGORY_TO_OPTIONS = 900;
    private static final int CATEGORY_TO_OPTIONS_ID = 901;


    private static final UriMatcher URI_MATCHER = buildMatcher();
    private DbHelper mDbHelper;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(DbContract.AUTHORITY,
                OrganisationUnits.ORGANISATION_UNITS, ORGANISATION_UNITS);
        matcher.addURI(DbContract.AUTHORITY,
                OrganisationUnits.ORGANISATION_UNIT_ID, ORGANISATION_UNIT_ID);
        matcher.addURI(DbContract.AUTHORITY,
                OrganisationUnits.ORGANISATION_UNIT_ID_DATASETS, ORGANISATION_UNIT_ID_DATASETS);

        matcher.addURI(DbContract.AUTHORITY,
                DataSets.DATASETS, DATA_SETS);
        matcher.addURI(DbContract.AUTHORITY,
                DataSets.DATASET_ID, DATA_SET_ID);
        matcher.addURI(DbContract.AUTHORITY,
                DataSets.DATASET_ID_CATEGORY_COMBO, DATA_SET_ID_CATEGORY_COMBOS);

        matcher.addURI(DbContract.AUTHORITY,
                UnitDataSets.UNIT_DATA_SETS, UNIT_DATA_SETS);
        matcher.addURI(DbContract.AUTHORITY,
                UnitDataSets.UNIT_DATA_SETS_ID, UNIT_DATA_SETS_ID);

        matcher.addURI(DbContract.AUTHORITY,
                CategoryCombos.CATEGORY_COMBOS, CATEGORY_COMBOS);
        matcher.addURI(DbContract.AUTHORITY,
                CategoryCombos.CATEGORY_COMBO_ID, CATEGORY_COMBO_ID);
        matcher.addURI(DbContract.AUTHORITY,
                CategoryCombos.CATEGORY_COMBO_ID_CATEGORIES, CATEGORY_COMBO_ID_CATEGORIES);

        matcher.addURI(DbContract.AUTHORITY,
                DataSetCategoryCombos.DATA_SET_CATEGORY_COMBOS, DATA_SET_CATEGORY_COMBOS);
        matcher.addURI(DbContract.AUTHORITY,
                DataSetCategoryCombos.DATA_SET_CATEGORY_COMBO_ID, DATA_SET_CATEGORY_COMBOS_ID);

        matcher.addURI(DbContract.AUTHORITY,
                Categories.CATEGORIES, CATEGORIES);
        matcher.addURI(DbContract.AUTHORITY,
                Categories.CATEGORY_ID, CATEGORY_ID);
        matcher.addURI(DbContract.AUTHORITY,
                Categories.CATEGORY_ID_OPTIONS, CATEGORY_ID_OPTIONS);

        matcher.addURI(DbContract.AUTHORITY,
                ComboCategories.COMBO_CATEGORIES, COMBO_CATEGORIES);
        matcher.addURI(DbContract.AUTHORITY,
                ComboCategories.COMBO_CATEGORY_ID, COMBO_CATEGORIES_ID);

        matcher.addURI(DbContract.AUTHORITY,
                CategoryOptions.CATEGORY_OPTIONS, CATEGORY_OPTIONS);
        matcher.addURI(DbContract.AUTHORITY,
                CategoryOptions.CATEGORY_OPTION_ID, CATEGORY_OPTION_ID);

        matcher.addURI(DbContract.AUTHORITY,
                CategoryToOptions.CATEGORY_TO_OPTIONS, CATEGORY_TO_OPTIONS);
        matcher.addURI(DbContract.AUTHORITY,
                CategoryToOptions.CATEGORY_TO_OPTION_ID, CATEGORY_TO_OPTIONS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ORGANISATION_UNITS:
                return DbContract.OrganisationUnits.CONTENT_TYPE;
            case ORGANISATION_UNIT_ID:
                return OrganisationUnits.CONTENT_ITEM_TYPE;
            case ORGANISATION_UNIT_ID_DATASETS:
                return DataSets.CONTENT_TYPE;
            case DATA_SETS:
                return DataSets.CONTENT_TYPE;
            case DATA_SET_ID:
                return DataSets.CONTENT_ITEM_TYPE;
            case DATA_SET_ID_CATEGORY_COMBOS:
                return CategoryCombos.CONTENT_TYPE;
            case UNIT_DATA_SETS:
                return UnitDataSets.CONTENT_TYPE;
            case UNIT_DATA_SETS_ID:
                return UnitDataSets.CONTENT_ITEM_TYPE;
            case CATEGORY_COMBOS:
                return CategoryCombos.CONTENT_TYPE;
            case CATEGORY_COMBO_ID:
                return CategoryCombos.CONTENT_ITEM_TYPE;
            case CATEGORY_COMBO_ID_CATEGORIES:
                return Categories.CONTENT_TYPE;
            case DATA_SET_CATEGORY_COMBOS:
                return DataSetCategoryCombos.CONTENT_TYPE;
            case DATA_SET_CATEGORY_COMBOS_ID:
                return DataSetCategoryCombos.CONTENT_ITEM_TYPE;
            case CATEGORIES:
                return Categories.CONTENT_TYPE;
            case CATEGORY_ID:
                return Categories.CONTENT_ITEM_TYPE;
            case CATEGORY_ID_OPTIONS:
                return CategoryOptions.CONTENT_TYPE;
            case COMBO_CATEGORIES:
                return ComboCategories.CONTENT_TYPE;
            case COMBO_CATEGORIES_ID:
                return ComboCategories.CONTENT_ITEM_TYPE;
            case CATEGORY_OPTIONS:
                return CategoryOptions.CONTENT_TYPE;
            case CATEGORY_OPTION_ID:
                return CategoryOptions.CONTENT_ITEM_TYPE;
            case CATEGORY_TO_OPTIONS:
                return CategoryToOptions.CONTENT_TYPE;
            case CATEGORY_TO_OPTIONS_ID:
                return CategoryToOptions.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("No corresponding Uri type was found");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case ORGANISATION_UNITS: {
                return query(uri, OrganisationUnits.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case ORGANISATION_UNIT_ID: {
                String id = OrganisationUnits.getId(uri);
                return queryId(uri, OrganisationUnits.TABLE_NAME,
                        OrganisationUnits.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case ORGANISATION_UNIT_ID_DATASETS: {
                String id = OrganisationUnits.getId(uri);
                return queryId(uri, DbSchema.UNIT_JOIN_DATA_SET_TABLE,
                        UnitDataSets.TABLE_NAME + "." + UnitDataSets.ORGANISATION_UNIT_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case DATA_SETS: {
                return query(uri, DataSets.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DATA_SET_ID: {
                String id = DataSets.getId(uri);
                return queryId(uri, DataSets.TABLE_NAME,
                        DataSets.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case DATA_SET_ID_CATEGORY_COMBOS: {
                String id = DataSets.getId(uri);
                return queryId(uri, DbSchema.DATA_SET_JOIN_CATEGORY_COMBO_TABLE,
                        DataSetCategoryCombos.TABLE_NAME + "." + DataSetCategoryCombos.DATA_SET_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case UNIT_DATA_SETS: {
                return query(uri, UnitDataSets.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case UNIT_DATA_SETS_ID: {
                String id = ContentUris.parseId(uri) + "";
                return queryId(uri, UnitDataSets.TABLE_NAME,
                        UnitDataSets.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORY_COMBOS: {
                return query(uri, CategoryCombos.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case CATEGORY_COMBO_ID: {
                String id = CategoryCombos.getId(uri);
                return queryId(uri, CategoryCombos.TABLE_NAME,
                        CategoryCombos.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORY_COMBO_ID_CATEGORIES: {
                String id = Categories.getId(uri);
                return queryId(uri, DbSchema.COMBO_JOIN_CATEGORY_TABLE,
                        ComboCategories.TABLE_NAME + "." + ComboCategories.CATEGORY_COMBO_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case DATA_SET_CATEGORY_COMBOS: {
                return query(uri, DataSetCategoryCombos.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case DATA_SET_CATEGORY_COMBOS_ID: {
                String id = ContentUris.parseId(uri) + "";
                return queryId(uri, DataSetCategoryCombos.TABLE_NAME,
                        DataSetCategoryCombos.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORIES: {
                return query(uri, Categories.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case CATEGORY_ID: {
                String id = Categories.getId(uri);
                return queryId(uri, Categories.TABLE_NAME,
                        Categories.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORY_ID_OPTIONS: {
                String id = Categories.getId(uri);
                return queryId(uri, DbSchema.CATEGORIES_JOIN_OPTIONS_TABLE,
                        CategoryToOptions.TABLE_NAME + "." + CategoryToOptions.CATEGORY_ID,
                        projection, selection, selectionArgs, sortOrder, id);
            }
            case COMBO_CATEGORIES: {
                return query(uri, ComboCategories.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case COMBO_CATEGORIES_ID: {
                String id = ContentUris.parseId(uri) + "";
                return queryId(uri, ComboCategories.TABLE_NAME,
                        ComboCategories.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORY_OPTIONS: {
                return query(uri, CategoryOptions.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case CATEGORY_OPTION_ID: {
                String id = CategoryOptions.getId(uri);
                return queryId(uri, CategoryOptions.TABLE_NAME,
                        CategoryOptions.ID, projection, selection, selectionArgs, sortOrder, id);
            }
            case CATEGORY_TO_OPTIONS: {
                return query(uri, CategoryToOptions.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
            }
            case CATEGORY_TO_OPTIONS_ID: {
                String id = ContentUris.parseId(uri) + "";
                return queryId(uri, CategoryToOptions.TABLE_NAME,
                        CategoryToOptions.ID, projection, selection, selectionArgs, sortOrder, id);
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case ORGANISATION_UNITS: {
                return insert(OrganisationUnits.TABLE_NAME, values, uri);
            }
            case DATA_SETS: {
                return insert(DataSets.TABLE_NAME, values, uri);
            }
            case ORGANISATION_UNIT_ID_DATASETS: {
                return insert(UnitDataSets.TABLE_NAME, values, uri);
            }
            case CATEGORY_COMBOS: {
                return insert(CategoryCombos.TABLE_NAME, values, uri);
            }
            case DATA_SET_CATEGORY_COMBOS: {
                return insert(DataSetCategoryCombos.TABLE_NAME, values, uri);
            }
            case CATEGORIES: {
                return insert(Categories.TABLE_NAME, values, uri);
            }
            case CATEGORY_OPTIONS: {
                return insert(CategoryOptions.TABLE_NAME, values, uri);
            }
            case COMBO_CATEGORIES: {
                return insert(ComboCategories.TABLE_NAME, values, uri);
            }
            case CATEGORY_TO_OPTIONS: {
                return insert(CategoryToOptions.TABLE_NAME, values, uri);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case ORGANISATION_UNITS: {
                return delete(OrganisationUnits.TABLE_NAME,
                        selection, selectionArgs);
            }
            case ORGANISATION_UNIT_ID: {
                String id = OrganisationUnits.getId(uri);
                return deleteId(OrganisationUnits.TABLE_NAME,
                        OrganisationUnits.ID, selection, selectionArgs, id);
            }
            case DATA_SETS: {
                return delete(DataSets.TABLE_NAME,
                        selection, selectionArgs);
            }
            case DATA_SET_ID: {
                String id = DataSets.getId(uri);
                return deleteId(DataSets.TABLE_NAME,
                        DataSets.ID, selection, selectionArgs, id);
            }
            case CATEGORY_COMBOS: {
                return delete(CategoryCombos.TABLE_NAME,
                        selection, selectionArgs);
            }
            case CATEGORY_COMBO_ID: {
                String id = CategoryCombos.getId(uri);
                return deleteId(CategoryCombos.TABLE_NAME,
                        CategoryCombos.ID, selection, selectionArgs, id);
            }
            case CATEGORIES: {
                return delete(Categories.TABLE_NAME,
                        selection, selectionArgs);
            }
            case CATEGORY_ID: {
                String id = Categories.getId(uri);
                return deleteId(Categories.TABLE_NAME,
                        Categories.ID, selection, selectionArgs, id);
            }
            case CATEGORY_OPTIONS: {
                return delete(CategoryOptions.TABLE_NAME,
                        selection, selectionArgs);
            }
            case CATEGORY_OPTION_ID: {
                String id = CategoryOptions.getId(uri);
                return deleteId(CategoryOptions.TABLE_NAME,
                        CategoryOptions.ID, selection, selectionArgs, id);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case ORGANISATION_UNITS: {
                return update(OrganisationUnits.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case ORGANISATION_UNIT_ID: {
                String id = OrganisationUnits.getId(uri);
                return updateId(OrganisationUnits.TABLE_NAME,
                        OrganisationUnits.ID, selection, selectionArgs, id, values);
            }
            case DATA_SETS: {
                return update(DataSets.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case DATA_SET_ID: {
                String id = DataSets.getId(uri);
                return updateId(DataSets.TABLE_NAME,
                        DataSets.ID, selection, selectionArgs, id, values);
            }
            case CATEGORY_COMBOS: {
                return update(CategoryCombos.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case CATEGORY_COMBO_ID: {
                String id = CategoryCombos.getId(uri);
                return updateId(CategoryCombos.TABLE_NAME,
                        CategoryCombos.ID, selection, selectionArgs, id, values);
            }
            case CATEGORIES: {
                return update(Categories.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case CATEGORY_ID: {
                String id = Categories.getId(uri);
                return updateId(Categories.TABLE_NAME,
                        Categories.ID, selection, selectionArgs, id, values);
            }
            case CATEGORY_OPTIONS: {
                return update(CategoryOptions.TABLE_NAME,
                        selection, selectionArgs, values);
            }
            case CATEGORY_OPTION_ID: {
                String id = CategoryOptions.getId(uri);
                return updateId(CategoryOptions.TABLE_NAME,
                        CategoryOptions.ID, selection, selectionArgs, id, values);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private Cursor query(Uri uri, String tableName, String[] projection,
                         String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Uri insert(String tableName, ContentValues values, Uri uri) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insertOrThrow(tableName, null, values);
        return withAppendedId(uri, id);
    }

    private int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.delete(tableName, selection, selectionArgs);
    }

    private int update(String tableName, String selection,
                       String[] selectionArgs, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(tableName, values, selection, selectionArgs);
    }

    private Cursor queryId(Uri uri, String tableName, String colId, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(tableName);
        qBuilder.appendWhere(colId + " = " + "'" + id + "'");

        Cursor cursor = qBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private int deleteId(String tableName, String colId,
                         String selection, String[] selectionArgs, String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = colId + " = " + "'" + id + "'";
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.delete(tableName, where, selectionArgs);
    }

    private int updateId(String tableName, String colId,
                         String selection, String[] selectionArgs,
                         String id, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = colId + " = " + "'" + id + "'";
        if (!isEmpty(selection)) {
            where += " AND " + selection;
        }

        return db.update(tableName, values, where, selectionArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}