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

package org.dhis2.mobile.sdk.controllers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import org.dhis2.mobile.sdk.DhisManager;
import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.entities.CategoryOptionCombo;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.persistence.database.DbContract;
import org.dhis2.mobile.sdk.persistence.handlers.CategoryComboHandler;
import org.dhis2.mobile.sdk.persistence.handlers.CategoryHandler;
import org.dhis2.mobile.sdk.persistence.handlers.CategoryOptionHandler;
import org.dhis2.mobile.sdk.persistence.handlers.CategoryToOptionsHandler;
import org.dhis2.mobile.sdk.persistence.handlers.ComboCategoryHandler;
import org.dhis2.mobile.sdk.persistence.handlers.DataSetCategoryComboHandler;
import org.dhis2.mobile.sdk.persistence.handlers.DataSetHandler;
import org.dhis2.mobile.sdk.persistence.handlers.OrganisationUnitHandler;
import org.dhis2.mobile.sdk.persistence.handlers.SessionHandler;
import org.dhis2.mobile.sdk.persistence.handlers.UnitDataSetHandler;
import org.dhis2.mobile.sdk.persistence.models.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.dhis2.mobile.sdk.utils.DbUtils.toIds;

public final class MetaDataController implements IController<Object> {
    private final Context mContext;
    private final DhisManager mDhisManager;
    private final OrganisationUnitHandler mOrgUnitHandler;
    private final DataSetHandler mDataSetHandler;
    private final UnitDataSetHandler mUnitDataSetHandler;
    private final CategoryComboHandler mCategoryComboHandler;
    private final DataSetCategoryComboHandler mDataSetCatComboHandler;
    private final CategoryHandler mCategoryHandler;
    private final ComboCategoryHandler mComboCategoryHandler;
    private final CategoryOptionHandler mCatOptionHandler;
    private final CategoryToOptionsHandler mCategoryToOptionHandler;
    private final Session mSession;

    public MetaDataController(Context context,
                              DhisManager dhisManager,
                              OrganisationUnitHandler orgUnitHandler,
                              DataSetHandler dataSetHandler,
                              UnitDataSetHandler unitDataSetHandler,
                              CategoryComboHandler categoryComboHandler,
                              DataSetCategoryComboHandler dataSetCatComboHandler,
                              CategoryHandler categoryHandler,
                              ComboCategoryHandler comboCatHandler,
                              CategoryOptionHandler catOptionHandler,
                              CategoryToOptionsHandler categoryToOptionsHandler,
                              SessionHandler sessionHandler) {
        mContext = context;
        mDhisManager = dhisManager;
        mOrgUnitHandler = orgUnitHandler;
        mDataSetHandler = dataSetHandler;
        mUnitDataSetHandler = unitDataSetHandler;
        mCategoryComboHandler = categoryComboHandler;
        mDataSetCatComboHandler = dataSetCatComboHandler;
        mCategoryHandler = categoryHandler;
        mComboCategoryHandler = comboCatHandler;
        mCatOptionHandler = catOptionHandler;
        mCategoryToOptionHandler = categoryToOptionsHandler;
        mSession = sessionHandler.get();
    }

    @Override
    public Object run() throws APIException {
        // first we need to fetch all metadata from server
        List<OrganisationUnit> units = getOrganisationUnits();
        List<DataSet> dataSets = getDataSets(units);
        List<CategoryCombo> catCombos = getCategoryCombos(dataSets);
        List<Category> cats = getCats(catCombos);
        List<CategoryOption> catOptions = getCatOptions(
                new ArrayList<CategoryOptionCombo>(), cats);

        /*
        List<CategoryOptionCombo> catOptCombos = getCatOptCombos(catCombos);
        */

        Queue<ContentProviderOperation> ops = new LinkedList<>();
        ops.addAll(mOrgUnitHandler.sync(units));
        ops.addAll(mDataSetHandler.sync(dataSets));
        ops.addAll(mCategoryComboHandler.sync(catCombos));
        ops.addAll(mCategoryHandler.sync(cats));
        ops.addAll(mCatOptionHandler.sync(catOptions));

        // Handling relationships
        ops.addAll(mUnitDataSetHandler.sync(units));
        ops.addAll(mDataSetCatComboHandler.sync(dataSets));
        ops.addAll(mComboCategoryHandler.sync(catCombos));
        ops.addAll(mCategoryToOptionHandler.sync(cats));

        try {
            mContext.getContentResolver().applyBatch(
                    DbContract.AUTHORITY, new ArrayList<>(ops)
            );
        } catch (RemoteException e) {
            throw APIException.unexpectedError(null, e);
        } catch (OperationApplicationException e) {
            throw APIException.unexpectedError(null, e);
        }

        return new Object();
    }

    private List<OrganisationUnit> getOrganisationUnits() throws APIException {
        return (new GetOrganisationUnitsController(
                mDhisManager, mOrgUnitHandler, mUnitDataSetHandler, mSession
        )).run();
    }

    private List<DataSet> getDataSets(List<OrganisationUnit> units) throws APIException {
        /* extracting ids of assigned dataSets from units */
        Set<String> dataSetIds = new HashSet<>();
        if (units != null && units.size() > 0) {
            for (OrganisationUnit orgUnit : units) {
                dataSetIds.addAll(toIds(orgUnit.getDataSets()));
            }
        }
        return (new GetDataSetsController(
                mDhisManager, mDataSetHandler, mSession, new ArrayList<>(dataSetIds)
        )).run();
    }

    private List<CategoryCombo> getCategoryCombos(List<DataSet> dataSets) throws APIException {
        Set<String> categoryComboIds = new HashSet<>(toIds(dataSets));
        return (new GetCategoryCombosController(
                mDhisManager, mCategoryComboHandler,
                mSession, new ArrayList<>(categoryComboIds)
        )).run();
    }

    private List<Category> getCats(List<CategoryCombo> catCombos) throws APIException {
        Set<String> ids = new HashSet<>();
        if (catCombos != null && catCombos.size() > 0) {
            for (CategoryCombo catCombo : catCombos) {
                ids.addAll(toIds(catCombo.getCategories()));
            }
        }

        return (new GetCategoriesController(
                mDhisManager, null, mSession, new ArrayList<>(ids)
        )).run();
    }

    private List<CategoryOption> getCatOptions(List<CategoryOptionCombo> cocs,
                                               List<Category> cats) throws APIException {
        Set<String> catOptionIds = new HashSet<>();
        if (cocs != null && cocs.size() > 0) {
            for (CategoryOptionCombo coc : cocs) {
                catOptionIds.addAll(toIds(coc.getCategoryOptions()));
            }
        }

        if (cats != null && cats.size() > 0) {
            for (Category category : cats) {
                catOptionIds.addAll(toIds(category.getCategoryOptions()));
            }
        }
        return (new GetCategoryOptionsController(mDhisManager,
                mSession, new ArrayList<>(catOptionIds))).run();
    }

    /*
    private List<CategoryOptionCombo> getCatOptCombos(List<CategoryCombo> catCombos) throws APIException {
        Set<String> ids = new HashSet<>();
        if (catCombos != null && catCombos.size() > 0) {
            for (CategoryCombo catCombo : catCombos) {
                ids.addAll(toIds(catCombo.getCategoryOptionCombos()));
            }
        }

        return (new GetCategoryOptionCombosController(
                mDhisManager, null, mSession, new ArrayList<>(ids)
        )).run();
    }
    */
}
