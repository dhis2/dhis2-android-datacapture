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

import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.mobile.sdk.DhisManager;
import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.entities.CategoryOptionCombo;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.entities.UnitToDataSetRelation;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.persistence.DbHelper;
import org.dhis2.mobile.sdk.persistence.models.DbOperation;
import org.dhis2.mobile.sdk.persistence.models.Session;
import org.dhis2.mobile.sdk.persistence.preferences.SessionHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toIds;

public final class MetaDataController implements IController<Object> {
    private final DhisManager mDhisManager;
    private final Session mSession;

    public MetaDataController(DhisManager dhisManager,
                              SessionHandler sessionHandler) {
        mDhisManager = dhisManager;
        mSession = sessionHandler.get();
    }

    @Override
    public Object run() throws APIException {
        // first we need to fetch all metadata from server
        List<OrganisationUnit> newUnits = getOrganisationUnits();
        List<DataSet> newDataSets = getDataSets(newUnits);
        //List<CategoryCombo> catCombos = getCategoryCombos(dataSets);
        //List<Category> cats = getCats(catCombos);
        //List<CategoryOption> catOptions = getCatOptions(
        //        new ArrayList<CategoryOptionCombo>(), cats);

        /*
        List<CategoryOptionCombo> catOptCombos = getCatOptCombos(catCombos);
        */

        Queue<DbOperation> ops = new LinkedList<>();
        ops.addAll(DbHelper.syncBaseIdentifiableModels(
                new Select().from(OrganisationUnit.class).queryList(), newUnits
        ));
        ops.addAll(DbHelper.syncBaseIdentifiableModels(
                new Select().from(DataSet.class).queryList(), newDataSets
        ));
        //ops.addAll(mCategoryComboHandler.sync(catCombos));
        //ops.addAll(mCategoryHandler.sync(cats));
        //ops.addAll(mCatOptionHandler.sync(catOptions));

        // Handling relationships
        ops.addAll(DbHelper.syncRelationModels(
                new Select().from(UnitToDataSetRelation.class).queryList(),
                buildUnitDataSetRelations(newUnits)
        ));

        //ops.addAll(mDataSetCatComboHandler.sync(dataSets));
        //ops.addAll(mComboCategoryHandler.sync(catCombos));
        //ops.addAll(mCategoryToOptionHandler.sync(cats));

        // DbManager.applyBatch(new ArrayList<>(ops));
        // DbManager.notifyChange(OrganisationUnit.class);
        // DbManager.notifyChange(DataSet.class);

        DbHelper.applyBatch(ops);
        return new Object();
    }


    private List<OrganisationUnit> getOrganisationUnits() throws APIException {
        return (new GetOrganisationUnitsController(
                mDhisManager, mSession
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
                mDhisManager, mSession, new ArrayList<>(dataSetIds)
        )).run();
    }

    private List<UnitToDataSetRelation> buildUnitDataSetRelations(List<OrganisationUnit> units) {
        List<UnitToDataSetRelation> relations = new ArrayList<>();
        if (units == null || units.isEmpty()) {
            return relations;
        }

        for (OrganisationUnit orgUnit : units) {
            if (orgUnit.getDataSets() == null || orgUnit.getDataSets().isEmpty()) {
                continue;
            }

            for (DataSet dataSet : orgUnit.getDataSets()) {
                UnitToDataSetRelation relation = new UnitToDataSetRelation();
                relation.setOrganisationUnit(orgUnit);
                relation.setDataSet(dataSet);
                relations.add(relation);
            }
        }
        return relations;
    }

    private List<CategoryCombo> getCategoryCombos(List<DataSet> dataSets) throws APIException {
        Set<String> categoryComboIds = new HashSet<>(toIds(dataSets));
        return (new GetCategoryCombosController(
                mDhisManager, mSession, new ArrayList<>(categoryComboIds)
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
                mDhisManager, mSession, new ArrayList<>(ids)
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
