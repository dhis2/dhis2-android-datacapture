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

import org.dhis2.mobile.sdk.persistence.database.DbContract.Categories;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryCombos;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryOptions;
import org.dhis2.mobile.sdk.persistence.database.DbContract.CategoryToOptions;
import org.dhis2.mobile.sdk.persistence.database.DbContract.ComboCategories;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSetCategoryCombos;
import org.dhis2.mobile.sdk.persistence.database.DbContract.DataSets;
import org.dhis2.mobile.sdk.persistence.database.DbContract.OrganisationUnits;
import org.dhis2.mobile.sdk.persistence.database.DbContract.UnitDataSets;

public final class DbSchema {

    public static final String CREATE_ORGANIZATION_UNIT_TABLE = "CREATE TABLE " + OrganisationUnits.TABLE_NAME + "(" +
            OrganisationUnits.ID + " TEXT PRIMARY KEY," +
            OrganisationUnits.CREATED + " TEXT NOT NULL," +
            OrganisationUnits.LAST_UPDATED + " TEXT NOT NULL," +
            OrganisationUnits.NAME + " TEXT," +
            OrganisationUnits.DISPLAY_NAME + " TEXT," +
            OrganisationUnits.LEVEL + " INTEGER" + ")";

    public static final String DROP_ORGANIZATION_UNIT_TABLE = "DROP TABLE IF EXISTS " + OrganisationUnits.TABLE_NAME;

    public static final String CREATE_DATA_SET_TABLE = "CREATE TABLE " + DataSets.TABLE_NAME + "(" +
            DataSets.ID + " TEXT PRIMARY KEY," +
            DataSets.CREATED + " TEXT NOT NULL," +
            DataSets.LAST_UPDATED + " TEXT NOT NULL," +
            DataSets.NAME + " TEXT," +
            DataSets.DISPLAY_NAME + " TEXT," +
            DataSets.VERSION + " INTEGER," +
            DataSets.EXPIRY_DAYS + " INTEGER," +
            DataSets.ALLOW_FUTURE_PERIODS + " TEXT NOT NULL," +
            DataSets.PERIOD_TYPE + " TEXT NOT NULL" + ")";
            /* DataSets.CATEGORY_COMBO + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + DataSets.CATEGORY_COMBO + ")" +
            " REFERENCES " + CategoryCombos.TABLE_NAME + "(" + CategoryCombos.ID + ")" +
            " ON DELETE CASCADE " + ")"; */

    public static final String DROP_DATA_SET_TABLE = "DROP TABLE IF EXISTS " + DataSets.TABLE_NAME;

    public static final String CREATE_UNIT_DATA_SETS_TABLE = "CREATE TABLE " + UnitDataSets.TABLE_NAME + "(" +
            UnitDataSets.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UnitDataSets.ORGANISATION_UNIT_ID + " TEXT NOT NULL," +
            UnitDataSets.DATA_SET_ID + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + UnitDataSets.ORGANISATION_UNIT_ID + ")" +
            " REFERENCES " + OrganisationUnits.TABLE_NAME + "(" + OrganisationUnits.ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + UnitDataSets.DATA_SET_ID + ")" +
            " REFERENCES " + DataSets.TABLE_NAME + "(" + DataSets.ID + ")" +
            " ON DELETE CASCADE " +
            " UNIQUE " + "(" + UnitDataSets.ORGANISATION_UNIT_ID + "," + UnitDataSets.DATA_SET_ID + ")" +
            " ON CONFLICT REPLACE" + ")";

    public static final String DROP_UNIT_DATA_SETS_TABLE = "DROP TABLE IF EXISTS " + UnitDataSets.TABLE_NAME;

    public static final String UNIT_JOIN_DATA_SET_TABLE = UnitDataSets.TABLE_NAME +
            " LEFT OUTER JOIN " + DataSets.TABLE_NAME + " ON " +
            UnitDataSets.TABLE_NAME + "." + UnitDataSets.DATA_SET_ID +
            " = " + DataSets.TABLE_NAME + "." + DataSets.ID;

    public static final String CREATE_CATEGORY_COMBOS_TABLE = "CREATE TABLE " + CategoryCombos.TABLE_NAME + "(" +
            CategoryCombos.ID + " TEXT PRIMARY KEY," +
            CategoryCombos.CREATED + " TEXT NOT NULL," +
            CategoryCombos.LAST_UPDATED + " TEXT NOT NULL," +
            CategoryCombos.NAME + " TEXT," +
            CategoryCombos.DISPLAY_NAME + " TEXT," +
            CategoryCombos.DIMENSION_TYPE + " TEXT," +
            CategoryCombos.SKIP_TOTAL + " INTEGER" + ")";

    public static final String DROP_CATEGORY_COMBOS_TABLE = "DROP TABLE IF EXISTS " + CategoryCombos.TABLE_NAME;

    public static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + Categories.TABLE_NAME + "(" +
            Categories.ID + " TEXT PRIMARY KEY," +
            Categories.CREATED + " TEXT NOT NULL," +
            Categories.LAST_UPDATED + " TEXT NOT NULL," +
            Categories.NAME + " TEXT," +
            Categories.DISPLAY_NAME + " TEXT," +
            Categories.DATA_DIMENSION + " TEXT," +
            Categories.DATA_DIMENSION_TYPE + " TEXT," +
            Categories.DIMENSION + " TEXT" + ")";

    public static final String DROP_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + Categories.TABLE_NAME;

    public static final String CREATE_CATEGORY_OPTIONS_TABLE = "CREATE TABLE " + CategoryOptions.TABLE_NAME + "(" +
            CategoryOptions.ID + " TEXT PRIMARY KEY," +
            CategoryOptions.CREATED + " TEXT NOT NULL," +
            CategoryOptions.LAST_UPDATED + " TEXT NOT NULL," +
            CategoryOptions.NAME + " TEXT," +
            CategoryOptions.DISPLAY_NAME + " TEXT" + ")";

    public static final String DROP_CATEGORY_OPTIONS_TABLE = "DROP TABLE IF EXISTS " + CategoryOptions.TABLE_NAME;

    public static final String CREATE_DATA_SET_CATEGORY_COMBO_TABLE = "CREATE TABLE " + DataSetCategoryCombos.TABLE_NAME + "(" +
            DataSetCategoryCombos.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataSetCategoryCombos.DATA_SET_ID + " TEXT NOT NULL," +
            DataSetCategoryCombos.CATEGORY_COMBO_ID + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + DataSetCategoryCombos.DATA_SET_ID + ")" +
            " REFERENCES " + DataSets.TABLE_NAME + "(" + DataSets.ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + DataSetCategoryCombos.CATEGORY_COMBO_ID + ")" +
            " REFERENCES " + CategoryCombos.TABLE_NAME + "(" + CategoryCombos.ID + ")" +
            " ON DELETE CASCADE " +
            " UNIQUE " + "(" + DataSetCategoryCombos.DATA_SET_ID + "," + DataSetCategoryCombos.CATEGORY_COMBO_ID + ")" +
            " ON CONFLICT REPLACE" + ")";

    public static final String DROP_DATA_SET_CATEGORY_COMBO_TABLE = "DROP TABLE IF EXISTS " + DataSetCategoryCombos.TABLE_NAME;

    public static final String DATA_SET_JOIN_CATEGORY_COMBO_TABLE = DataSetCategoryCombos.TABLE_NAME +
            " LEFT OUTER JOIN " + CategoryCombos.TABLE_NAME + " ON " +
            DataSetCategoryCombos.TABLE_NAME + "." + DataSetCategoryCombos.CATEGORY_COMBO_ID +
            " = " + CategoryCombos.TABLE_NAME + "." + CategoryCombos.ID;

    public static final String CREATE_COMBO_CATEGORIES_TABLE = "CREATE TABLE " + ComboCategories.TABLE_NAME + "(" +
            ComboCategories.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ComboCategories.CATEGORY_COMBO_ID + " TEXT NOT NULL," +
            ComboCategories.CATEGORY_ID + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + ComboCategories.CATEGORY_COMBO_ID + ")" +
            " REFERENCES " + CategoryCombos.TABLE_NAME + "(" + CategoryCombos.ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + ComboCategories.CATEGORY_ID + ")" +
            " REFERENCES " + Categories.TABLE_NAME + "(" + Categories.ID + ")" +
            " ON DELETE CASCADE " +
            " UNIQUE " + "(" + ComboCategories.CATEGORY_COMBO_ID + "," + ComboCategories.CATEGORY_ID + ")" +
            " ON CONFLICT REPLACE" + ")";

    public static final String DROP_COMBO_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + ComboCategories.TABLE_NAME;

    public static final String COMBO_JOIN_CATEGORY_TABLE = ComboCategories.TABLE_NAME +
            " LEFT OUTER JOIN " + Categories.TABLE_NAME + " ON " +
            ComboCategories.TABLE_NAME + "." + ComboCategories.CATEGORY_ID +
            " = " + Categories.TABLE_NAME + "." + Categories.ID;

    public static final String CREATE_CATEGORIES_TO_OPTIONS_TABLE = "CREATE TABLE " + CategoryToOptions.TABLE_NAME + "(" +
            CategoryToOptions.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CategoryToOptions.CATEGORY_ID + " TEXT NOT NULL," +
            CategoryToOptions.CATEGORY_OPTION_ID + " TEXT NOT NULL," +
            " FOREIGN KEY " + "(" + CategoryToOptions.CATEGORY_ID + ")" +
            " REFERENCES " + Categories.TABLE_NAME + "(" + Categories.ID + ")" +
            " ON DELETE CASCADE " +
            " FOREIGN KEY " + "(" + CategoryToOptions.CATEGORY_OPTION_ID + ")" +
            " REFERENCES " + CategoryOptions.TABLE_NAME + "(" + CategoryOptions.ID + ")" +
            " ON DELETE CASCADE " +
            " UNIQUE " + "(" + CategoryToOptions.CATEGORY_ID + "," + CategoryToOptions.CATEGORY_OPTION_ID + ")" +
            " ON CONFLICT REPLACE" + ")";

    public static final String DROP_CATEGORIES_TO_OPTIONS_TABLE = "DROP TABLE IF EXISTS " + CategoryToOptions.TABLE_NAME;

    public static final String CATEGORIES_JOIN_OPTIONS_TABLE = CategoryToOptions.TABLE_NAME +
            " LEFT OUTER JOIN " + CategoryOptions.TABLE_NAME + " ON " +
            CategoryToOptions.TABLE_NAME + "." + CategoryToOptions.CATEGORY_OPTION_ID +
            " = " + CategoryOptions.TABLE_NAME + "." + CategoryOptions.ID;
}