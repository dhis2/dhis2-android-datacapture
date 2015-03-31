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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DbContract {
    public static final String AUTHORITY = "org.dhis2.mobile.sdk.persistence.database.DbContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static interface OrganisationUnitColumns {
        public static final String TABLE_NAME = "organizationUnitsTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String LEVEL = "level";
    }

    public static interface DataSetColumns {
        public static final String TABLE_NAME = "dataSetsTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String VERSION = "version";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String ALLOW_FUTURE_PERIODS = "allowFuturePeriods";
        public static final String PERIOD_TYPE = "periodType";
        // public static final String CATEGORY_COMBO = "categoryCombo";
    }

    public static interface UnitDataSetsColumns {
        public static final String TABLE_NAME = "unitDataSetsTable";
        public static final String ID = BaseColumns._ID;
        public static final String ORGANISATION_UNIT_ID = "organisationUnitId";
        public static final String DATA_SET_ID = "dataSetId";
    }

    public static interface CategoryComboColumns {
        public static final String TABLE_NAME = "categoryCombosTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String DIMENSION_TYPE = "dimensionType";
        public static final String SKIP_TOTAL = "skipTotal";
    }

    public static interface DataSetCategoryComboColumns {
        public static final String TABLE_NAME = "dataSetCategoryComboTable";
        public static final String ID = BaseColumns._ID;
        public static final String DATA_SET_ID = "dataSetId";
        public static final String CATEGORY_COMBO_ID = "categoryComboId";
    }

    public static interface CategoryColumns {
        public static final String TABLE_NAME = "categoriesTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String DATA_DIMENSION = "dataDimension";
        public static final String DATA_DIMENSION_TYPE = "dimensionType";
        public static final String DIMENSION = "dimension";
    }

    public static interface ComboCategoriesColumns {
        public static final String TABLE_NAME = "";
        public static final String ID = BaseColumns._ID;
        public static final String CATEGORY_COMBO_ID = "categoryComboId";
        public static final String CATEGORY_ID = "categoryId";
    }

    public static interface CategoryOptionColumns {
        public static final String TABLE_NAME = "categoryOptionsTable";
        public static final String ID = "id";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
    }

    public static interface CategoryToOptionsColumns {
        public static final String TABLE_NAME = "";
        public static final String ID = BaseColumns._ID;
        public static final String CATEGORY_ID = "categoryId";
        public static final String CATEGORY_OPTION_ID = "categoryOptionId";
    }

    public static class OrganisationUnits implements OrganisationUnitColumns {
        public static final String PATH = TABLE_NAME;
        public static final String ORGANISATION_UNITS = PATH;
        public static final String ORGANISATION_UNIT_ID = PATH + "/*/";
        public static final String ORGANISATION_UNIT_ID_DATASETS = ORGANISATION_UNIT_ID
                + DataSets.TABLE_NAME;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.OrganisationUnit";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.OrganisationUnit";
        private static final int ORGANIZATION_UNIT_ID_POSITION = 1;

        public static Uri buildUriWithDataSets(String orgUnitId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(orgUnitId)
                    .appendPath(DataSets.TABLE_NAME)
                    .build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(ORGANIZATION_UNIT_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class DataSets implements DataSetColumns {
        public static final String PATH = TABLE_NAME;
        public static final String DATASETS = PATH;
        public static final String DATASET_ID = PATH + "/*/";
        public static final String DATASET_ID_CATEGORY_COMBO = DATASET_ID +
                CategoryCombos.TABLE_NAME;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.DataSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.DataSet";
        private static final int DATA_SET_ID_POSITION = 1;

        public static Uri buildUriWithCategoryCombos(String dataSetId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(dataSetId)
                    .appendPath(CategoryCombos.TABLE_NAME)
                    .build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(DATA_SET_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class UnitDataSets implements UnitDataSetsColumns {
        public static final String PATH = TABLE_NAME;
        public static final String UNIT_DATA_SETS = PATH;
        public static final String UNIT_DATA_SETS_ID = PATH + "/#/";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.UnitDataSet";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.UnitDataSet";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class CategoryCombos implements CategoryComboColumns {
        public static final String PATH = TABLE_NAME;
        public static final String CATEGORY_COMBOS = PATH;
        public static final String CATEGORY_COMBO_ID = CATEGORY_COMBOS + "/*/";
        public static final String CATEGORY_COMBO_ID_CATEGORIES = CATEGORY_COMBO_ID +
                Categories.TABLE_NAME;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.CategoryCombo";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.CategoryCombo";
        private static final int URI_ID_POSITION = 1;

        public static Uri buildUriWithCategories(String comboId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(comboId)
                    .appendPath(Categories.TABLE_NAME)
                    .build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(URI_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class DataSetCategoryCombos implements DataSetCategoryComboColumns {
        public static final String PATH = TABLE_NAME;
        public static final String DATA_SET_CATEGORY_COMBOS = PATH;
        public static final String DATA_SET_CATEGORY_COMBO_ID = DATA_SET_CATEGORY_COMBOS + "/#/";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.DataSetCategoryCombo";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.DataSetCategoryCombo";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class Categories implements CategoryColumns {
        public static final String PATH = TABLE_NAME;
        public static final String CATEGORIES = PATH;
        public static final String CATEGORY_ID = CATEGORIES + "/*/";
        public static final String CATEGORY_ID_OPTIONS = CATEGORY_ID + CategoryOptions.TABLE_NAME;


        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.Category";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.Category";
        private static final int URI_ID_POSITION = 1;

        public static Uri buildUriWithOptions(String categoryId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(categoryId)
                    .appendPath(CategoryOptions.TABLE_NAME)
                    .build();
        }

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(URI_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class ComboCategories implements ComboCategoriesColumns {
        public static final String PATH = TABLE_NAME;
        public static final String COMBO_CATEGORIES = PATH;
        public static final String COMBO_CATEGORY_ID = COMBO_CATEGORIES + "/#/";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.ComboCategories";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.ComboCategories";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class CategoryOptions implements CategoryOptionColumns {
        public static final String PATH = TABLE_NAME;
        public static final String CATEGORY_OPTIONS = PATH;
        public static final String CATEGORY_OPTION_ID = CATEGORY_OPTIONS + "/*/";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.CategoryOption";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.CategoryOption";
        private static final int URI_ID_POSITION = 1;

        public static String getId(Uri uri) {
            return uri.getPathSegments().get(URI_ID_POSITION);
        }

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }

    public static class CategoryToOptions implements CategoryToOptionsColumns {
        public static final String PATH = TABLE_NAME;
        public static final String CATEGORY_TO_OPTIONS = PATH;
        public static final String CATEGORY_TO_OPTION_ID = CATEGORY_TO_OPTIONS + "/#/";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.dhis2.mobile.CategoryToOptions";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.dhis2.mobile.CategoryToOptions";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
    }
}
