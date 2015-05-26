/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.sdk.persistence.handlers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.entities.UnitToDataSetRelation;
import org.dhis2.mobile.sdk.network.managers.LogManager;
import org.dhis2.mobile.sdk.persistence.database.DbContract;

import java.util.ArrayList;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

/**
 * Created by araz on 29.04.2015.
 */
public final class DbManager {
    private static DbManager mManager;
    private final Context mContext;

    private OrganisationUnitHandler mUnitHandler;
    private DataSetHandler mDataSetHandler;
    private UnitDataSetRelationHandler mUnitDataSetHandler;

    private CategoryComboHandler mCategoryComboHandler;
    private CategoryHandler mCategoryHandler;
    private CategoryOptionHandler mCategoryOptionHandler;

    private DbManager(Context context) {
        mContext = isNull(context, "Context object must not be null");
    }

    public static void init(Context context) {
        if (mManager == null) {
            mManager = new DbManager(context);
        }
    }

    private static DbManager getInstance() {
        isNull(mManager, "You have to call init() " +
                "on DbManager before using it");
        return mManager;
    }

    private Context getContext() {
        return mContext;
    }

    // TODO get LogManager from calling code
    public static <T> IModelHandler<T> with(Class<T> clazz) {
        isNull(clazz, "Class object must not be null");

        if (clazz == OrganisationUnit.class) {
            if (getInstance().mUnitHandler == null) {
                getInstance().mUnitHandler = new OrganisationUnitHandler(getInstance().getContext());
            }
            return (IModelHandler<T>) getInstance().mUnitHandler;
        } else if (clazz == DataSet.class) {
            if (getInstance().mDataSetHandler == null) {
                getInstance().mDataSetHandler = new DataSetHandler(getInstance().getContext(), new LogManager());
            }
            return (IModelHandler<T>) getInstance().mDataSetHandler;
        } else if (clazz == UnitToDataSetRelation.class) {
            if (getInstance().mUnitDataSetHandler == null) {
                getInstance().mUnitDataSetHandler = new UnitDataSetRelationHandler(getInstance().getContext(), new LogManager());
            }
            return (IModelHandler<T>) getInstance().mUnitDataSetHandler;
        } else if (clazz == CategoryCombo.class) {
            if (getInstance().mCategoryComboHandler == null) {
                getInstance().mCategoryComboHandler = new CategoryComboHandler(getInstance().getContext(), new LogManager());
            }
            return (IModelHandler<T>) getInstance().mCategoryComboHandler;
        } else if (clazz == Category.class) {
            if (getInstance().mCategoryHandler == null) {
                getInstance().mCategoryHandler = new CategoryHandler(getInstance().getContext(), new LogManager());
            }
            return (IModelHandler<T>) getInstance().mCategoryHandler;
        } else if (clazz == CategoryOption.class) {
            if (getInstance().mCategoryOptionHandler == null) {
                getInstance().mCategoryOptionHandler = new CategoryOptionHandler(getInstance().getContext(), new LogManager());
            }
            return (IModelHandler<T>) getInstance().mCategoryOptionHandler;
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    public static <T> void notifyChange(Class<T> clazz) {
        isNull(clazz, "Class object must not be null");

        ContentResolver resolver = getInstance().getContext()
                .getContentResolver();
        if (clazz == OrganisationUnit.class) {
            resolver.notifyChange(DbContract.OrganisationUnits.CONTENT_URI, null);
        } else if (clazz == DataSet.class) {
            resolver.notifyChange(DbContract.DataSets.CONTENT_URI, null);
        } else if (clazz == UnitToDataSetRelation.class) {
            resolver.notifyChange(DbContract.UnitDataSets.CONTENT_URI, null);
        } else if (clazz == CategoryComboHandler.class) {
            resolver.notifyChange(DbContract.CategoryCombos.CONTENT_URI, null);
        } else if (clazz == CategoryHandler.class) {
            resolver.notifyChange(DbContract.Categories.CONTENT_URI, null);
        } else if (clazz == CategoryOption.class) {
            resolver.notifyChange(DbContract.CategoryOptions.CONTENT_URI, null);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    public static void applyBatch(ArrayList<ContentProviderOperation> ops) {
        try {
            getInstance().getContext().getContentResolver()
                    .applyBatch(DbContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        }
    }
}