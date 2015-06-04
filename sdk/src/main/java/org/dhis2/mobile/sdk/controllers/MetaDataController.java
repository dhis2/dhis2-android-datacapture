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

import org.dhis2.mobile.sdk.persistence.models.Category;
import org.dhis2.mobile.sdk.persistence.models.CategoryCombo;
import org.dhis2.mobile.sdk.persistence.models.CategoryComboToCategoryRelation;
import org.dhis2.mobile.sdk.persistence.models.CategoryOption;
import org.dhis2.mobile.sdk.persistence.models.CategoryToCategoryOptionRelation;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.models.UnitToDataSetRelation;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.tasks.NetworkManager;
import org.dhis2.mobile.sdk.persistence.DbHelper;
import org.dhis2.mobile.sdk.persistence.models.DbOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toIds;

public final class MetaDataController implements IController<Object> {
    private final NetworkManager mNetworkManager;

    public MetaDataController() {
        mNetworkManager = NetworkManager.getInstance();
    }

    @Override
    public Object run() throws APIException {
        // fetching new data from distant instance.
        List<OrganisationUnit> units = getOrganisationUnits();
        List<DataSet> dataSets = getDataSets(units);
        List<UnitToDataSetRelation> unitToDataSets = buildUnitDataSetRelations(units);

        List<CategoryCombo> categoryCombos = getCategoryCombos(dataSets);
        List<Category> categories = getCategories(categoryCombos);
        List<CategoryComboToCategoryRelation> comboToCats = buildCatComboToCatRelations(categoryCombos);

        List<CategoryOption> categoryOptions = getCategoryOptions(categories);
        List<CategoryToCategoryOptionRelation> catToCatOptions = buildCatToCatOptionRelations(categories);

        // creating db operations.
        Queue<DbOperation> ops = new LinkedList<>();
        ops.addAll(DbHelper.createOperations(new Select().from(CategoryOption.class).queryList(), categoryOptions));
        ops.addAll(DbHelper.createOperations(new Select().from(Category.class).queryList(), categories));
        ops.addAll(DbHelper.createOperations(new Select().from(CategoryCombo.class).queryList(), categoryCombos));

        ops.addAll(DbHelper.createOperations(new Select().from(OrganisationUnit.class).queryList(), units));
        ops.addAll(DbHelper.createOperations(new Select().from(DataSet.class).queryList(), dataSets));

        ops.addAll(DbHelper.syncRelationModels(new Select().from(CategoryToCategoryOptionRelation.class).queryList(), catToCatOptions));
        ops.addAll(DbHelper.syncRelationModels(new Select().from(CategoryComboToCategoryRelation.class).queryList(), comboToCats));
        ops.addAll(DbHelper.syncRelationModels(new Select().from(UnitToDataSetRelation.class).queryList(), unitToDataSets));

        DbHelper.applyBatch(ops);
        return new Object();
    }

    private List<OrganisationUnit> getOrganisationUnits() throws APIException {
        return new AbsBaseIdentifiableController<OrganisationUnit>() {

            @Override List<OrganisationUnit> getNewBasicItems() {
                List<OrganisationUnit> parentUnits = mNetworkManager
                        .getAssignedOrganisationUnits();
                List<OrganisationUnit> childUnits = mNetworkManager
                        .getChildOrganisationUnits(toIds(parentUnits), true);
                parentUnits.addAll(childUnits);
                return parentUnits;
            }

            @Override List<OrganisationUnit> getNewFullItems(List<String> ids) {
                return mNetworkManager.getOrganisationUnitsByIds(ids, false);
            }

            @Override List<OrganisationUnit> getItemsFromDb() {
                // read all organisation units from database
                List<OrganisationUnit> orgUnits = new Select()
                        .from(OrganisationUnit.class)
                        .queryList();
                for (OrganisationUnit orgUnit : orgUnits) {
                    // read relationships of unit with datasets
                    orgUnit.setDataSets(OrganisationUnit
                            .queryRelatedDataSetsFromDb(orgUnit.getId()));
                }
                return orgUnits;
            }
        }.run();
    }

    private List<DataSet> getDataSets(List<OrganisationUnit> units) {
        /* extracting ids of assigned dataSets from units */
        final Set<String> dataSetIds = new HashSet<>();
        if (units != null && units.size() > 0) {
            for (OrganisationUnit orgUnit : units) {
                dataSetIds.addAll(toIds(orgUnit.getDataSets()));
            }
        }

        return new AbsBaseIdentifiableController<DataSet>() {

            @Override List<DataSet> getNewBasicItems() {
                return mNetworkManager.getDataSetsByIds(new ArrayList<>(dataSetIds), true);
            }

            @Override List<DataSet> getNewFullItems(List<String> ids) {
                return mNetworkManager.getDataSetsByIds(ids, false);
            }

            @Override List<DataSet> getItemsFromDb() {
                return new Select().from(DataSet.class).queryList();
            }
        }.run();
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
        final Set<String> categoryComboIds = new HashSet<>();
        if (dataSets != null && !dataSets.isEmpty()) {
            for (DataSet dataSet : dataSets) {
                categoryComboIds.add(dataSet.getCategoryCombo().getId());
            }
        }

        return new AbsBaseIdentifiableController<CategoryCombo>() {

            @Override List<CategoryCombo> getNewBasicItems() {
                return mNetworkManager.getCategoryCombosByIds(
                        new ArrayList<>(categoryComboIds), true);
            }

            @Override List<CategoryCombo> getNewFullItems(List<String> ids) {
                return mNetworkManager.getCategoryCombosByIds(ids, false);
            }

            @Override List<CategoryCombo> getItemsFromDb() {
                List<CategoryCombo> categoryCombos = new Select()
                        .from(CategoryCombo.class).queryList();
                for (CategoryCombo categoryCombo : categoryCombos) {
                    categoryCombo.setCategories(CategoryCombo
                            .getRelatedCategoriesFromDb(categoryCombo.getId()));
                }
                return categoryCombos;
            }
        }.run();
    }

    private List<Category> getCategories(List<CategoryCombo> catCombos) throws APIException {
        final Set<String> ids = new HashSet<>();
        if (catCombos != null && catCombos.size() > 0) {
            for (CategoryCombo catCombo : catCombos) {
                ids.addAll(toIds(catCombo.getCategories()));
            }
        }

        return new AbsBaseIdentifiableController<Category>() {

            @Override List<Category> getNewBasicItems() {
                return mNetworkManager.getCategoriesByIds(new ArrayList<>(ids), true);
            }

            @Override List<Category> getNewFullItems(List<String> ids) {
                return mNetworkManager.getCategoriesByIds(ids, false);
            }

            @Override List<Category> getItemsFromDb() {
                List<Category> categories = new Select().from(Category.class).queryList();
                for (Category category : categories) {
                    category.setCategoryOptions(Category
                            .getRelatedOptions(category.getId()));
                }
                return categories;
            }
        }.run();
    }

    private List<CategoryComboToCategoryRelation> buildCatComboToCatRelations(List<CategoryCombo> combos) {
        List<CategoryComboToCategoryRelation> relations = new ArrayList<>();
        if (combos == null || combos.isEmpty()) {
            return relations;
        }

        for (CategoryCombo combo : combos) {
            if (combo.getCategories() == null || combo.getCategories().isEmpty()) {
                continue;
            }

            for (Category category : combo.getCategories()) {
                CategoryComboToCategoryRelation relation
                        = new CategoryComboToCategoryRelation();
                relation.setCategoryCombo(combo);
                relation.setCategory(category);
                relations.add(relation);
            }
        }
        return relations;
    }

    private List<CategoryOption> getCategoryOptions(List<Category> categories) throws APIException {
        final Set<String> ids = new HashSet<>();
        if (categories != null && categories.size() > 0) {
            for (Category category : categories) {
                ids.addAll(toIds(category.getCategoryOptions()));
            }
        }
        return mNetworkManager.getCategoryOptions(new ArrayList<>(ids));
    }

    private List<CategoryToCategoryOptionRelation> buildCatToCatOptionRelations(List<Category> categories) {
        List<CategoryToCategoryOptionRelation> relations = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            return relations;
        }

        for (Category category : categories) {
            if (category.getCategoryOptions() == null || category.getCategoryOptions().isEmpty()) {
                continue;
            }

            for (CategoryOption option : category.getCategoryOptions()) {
                CategoryToCategoryOptionRelation relation
                        = new CategoryToCategoryOptionRelation();
                relation.setCategory(category);
                relation.setCategoryOption(option);
                relations.add(relation);
            }
        }
        return relations;
    }
}
