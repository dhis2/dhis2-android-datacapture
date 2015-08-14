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

package org.hisp.dhis.android.datacapture.sdk.controllers;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.datacapture.sdk.DhisManager;
import org.hisp.dhis.android.datacapture.sdk.network.repository.DhisService;
import org.hisp.dhis.android.datacapture.sdk.network.repository.RepoManager;
import org.hisp.dhis.android.datacapture.sdk.persistence.DbHelper;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.Category;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryCombo;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryComboToCategoryRelation;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryOption;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryToCategoryOptionRelation;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataSet;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataSetToDataElementRelation;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DbOperation;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.UnitToDataSetRelation;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.datacapture.sdk.persistence.preferences.LastUpdatedPreferences;
import org.hisp.dhis.android.datacapture.sdk.utils.Joiner;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import retrofit.RetrofitError;

import static org.hisp.dhis.android.datacapture.sdk.utils.DbUtils.toListIds;
import static org.hisp.dhis.android.datacapture.sdk.utils.DbUtils.toSetIds;
import static org.hisp.dhis.android.datacapture.sdk.utils.NetworkUtils.unwrapResponse;

public final class MetaDataController implements IController<Object> {
    private final DhisService mService;
    private final LastUpdatedPreferences mLastUpdatedPreferences;

    public MetaDataController(LastUpdatedPreferences lastUpdatedPreferences) {
        mService = RepoManager.createService(DhisManager.getInstance().getServerUrl(),
                DhisManager.getInstance().getUserCredentials());
        mLastUpdatedPreferences = lastUpdatedPreferences;
    }

    @Override
    public Object run() throws RetrofitError {
        final DateTime oldLastUpdated = mLastUpdatedPreferences.getLastUpdated();
        final DateTime newLastUpdated = DateTime.now(DateTimeZone
                .forTimeZone(mLastUpdatedPreferences.getServerTimeZone()));
        final boolean isUpdating = oldLastUpdated != null;

        // fetching new data from distant instance.
        /*** START this is ok ***/
        List<OrganisationUnit> units = getOrganisationUnits();
        List<DataSet> dataSets = getDataSets(units);
        List<DataElement> dataElements = getDataElements(dataSets, newLastUpdated, isUpdating);

        /** building relationships among models */
        List<DataSetToDataElementRelation> dataSetToElementRelations
                = buildDataSetToDataElementRelations(dataSets);
        List<UnitToDataSetRelation> unitToDataSets
                = buildUnitDataSetRelations(units);
        /*** END this is ok ***/

        /* List<CategoryCombo> categoryCombos = getCategoryCombos(dataSets);
        List<Category> categories = getCategories(categoryCombos);
        List<CategoryComboToCategoryRelation> comboToCats
                = buildCatComboToCatRelations(categoryCombos);

        List<CategoryOption> categoryOptions = getCategoryOptions(categories);
        List<CategoryToCategoryOptionRelation> catToCatOptions
                = buildCatToCatOptionRelations(categories); */

        // creating db operations.
        Queue<DbOperation> ops = new LinkedList<>();
        /* ops.addAll(DbHelper.createOperations(new Select()
                .from(CategoryOption.class)
                .queryList(), categoryOptions));
        ops.addAll(DbHelper.createOperations(new Select()
                .from(Category.class)
                .queryList(), categories));
        ops.addAll(DbHelper.createOperations(new Select()
                .from(CategoryCombo.class)
                .queryList(), categoryCombos)); */

        /* ops.addAll(DbHelper.syncRelationModels(new Select()
                .from(CategoryToCategoryOptionRelation.class)
                .queryList(), catToCatOptions));
        ops.addAll(DbHelper.syncRelationModels(new Select()
                .from(CategoryComboToCategoryRelation.class)
                .queryList(), comboToCats)); */

        /*** START this is ok ***/
        /* ops.addAll(DbHelper.createOperations(new Select()
                .from(DataElement.class)
                .queryList(), dataElements)); */
        ops.addAll(DbHelper.save(dataElements));
        ops.addAll(DbHelper.createOperations(new Select()
                .from(DataSet.class)
                .queryList(), dataSets));
        ops.addAll(DbHelper.createOperations(new Select()
                .from(OrganisationUnit.class)
                .queryList(), units));

        ops.addAll(DbHelper.syncRelationModels(new Select()
                .from(DataSetToDataElementRelation.class)
                .queryList(), dataSetToElementRelations));
        ops.addAll(DbHelper.syncRelationModels(new Select()
                .from(UnitToDataSetRelation.class)
                .queryList(), unitToDataSets));
        /*** END this is ok ***/

        DbHelper.applyBatch(ops);
        mLastUpdatedPreferences
                .setLastUpdated(newLastUpdated);
        return new Object();
    }

    private List<OrganisationUnit> getOrganisationUnits() throws RetrofitError {
        return new AbsBaseIdentifiableController<OrganisationUnit>() {

            @Override List<OrganisationUnit> getNewBasicItems() {
                List<OrganisationUnit> assignedUnits = getAssignedUnits();
                List<OrganisationUnit> childUnits = getChildUnits(assignedUnits);
                assignedUnits.addAll(childUnits);
                return assignedUnits;
            }

            @Override List<OrganisationUnit> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName,level,dataSets[id]");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getOrganisationUnits(QUERY_MAP), "organisationUnits");
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

            private List<OrganisationUnit> getAssignedUnits() throws RetrofitError {
                final Map<String, String> QUERY_PARAMS = new HashMap<>();
                QUERY_PARAMS.put("fields", "organisationUnits[id,lastUpdated]");
                UserAccount userAccount = mService.getCurrentUserAccount(QUERY_PARAMS);
                return userAccount.getOrganisationUnits();
            }

            private List<OrganisationUnit> getChildUnits(List<OrganisationUnit> assignedUnits) {
                if (assignedUnits == null || assignedUnits.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_PARAMS = new HashMap<>();
                QUERY_PARAMS.put("fields", "id,lastUpdated");
                QUERY_PARAMS.put("filter", "parent.id:in:[" + Joiner.on(",").join(toListIds(assignedUnits)) + "]");
                return unwrapResponse(mService.getOrganisationUnits(QUERY_PARAMS), "organisationUnits");
            }
        }.run();
    }

    private List<DataSet> getDataSets(List<OrganisationUnit> units) {
        /* extracting ids of assigned dataSets from units */
        final Set<String> dataSetIds = new HashSet<>();
        if (units != null && units.size() > 0) {
            for (OrganisationUnit orgUnit : units) {
                dataSetIds.addAll(toListIds(orgUnit.getDataSets()));
            }
        }

        return new AbsBaseIdentifiableController<DataSet>() {

            @Override List<DataSet> getNewBasicItems() {
                if (dataSetIds.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,lastUpdated");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(dataSetIds) + "]");
                return unwrapResponse(mService.getDataSets(QUERY_MAP), "dataSets");
            }

            @Override List<DataSet> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName,expiryDays," +
                        "allowFuturePeriods,periodType,categoryCombo[id],dataElements[id]");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getDataSets(QUERY_MAP), "dataSets");
            }

            @Override List<DataSet> getItemsFromDb() {
                List<DataSet> dataSets = new Select()
                        .from(DataSet.class).queryList();
                for (DataSet dataSet : dataSets) {
                    dataSet.setDataElements(DataSet
                            .queryRelatedDataElementsFromDb(dataSet.getId()));
                }
                return dataSets;
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

    private List<DataElement> getDataElements(List<DataSet> dataSets, DateTime lastUpdated,
                                              boolean isUpdating) throws RetrofitError {
        /* final DateTime lastUpdated = mLastUpdatedPreferences
                .getLastUpdatedFor(ResourceType.DATA_ELEMENT); */
        final Set<String> dataSetIds = toSetIds(dataSets);

        if (dataSetIds == null || dataSetIds.isEmpty()) {
            return new ArrayList<>();
        }

        final Map<String, String> QUERY_MAP = new HashMap<>();
        /* defining fields which we need to download from server */
        QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName," +
                "type,numberType,textType,zeroIsSignificant,categoryCombo[id]");

        /* this filter is intended to give us only those data
         elements which are assigned to datasets */
        String filterValue = "dataSet.id:in:[" + Joiner.on(",").join(dataSetIds) + "]";
        /* if lastUpdated equals null, it means we have not
        downloaded any data elements from server yet. */
        if (isUpdating) {
            filterValue += "&filter=lastUpdated:gt:" + lastUpdated.toString();
        }

        QUERY_MAP.put("filter", filterValue);
        return unwrapResponse(mService.getDataElements(QUERY_MAP), "dataElements");
    }

    /*
    private List<DataElement> getDataElements(List<DataSet> dataSets) throws RetrofitError {
        final Set<String> dataElementIds = new HashSet<>();
        if (dataSets != null && dataSets.size() > 0) {
            for (DataSet dataSet : dataSets) {
                dataElementIds.addAll(toListIds(dataSet.getDataElements()));
            }
        }

        return new AbsBaseIdentifiableController<DataElement>() {

            @Override List<DataElement> getNewBasicItems() {
                if (dataElementIds.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,lastUpdated");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(dataElementIds) + "]");
                return unwrapResponse(mService.getDataElements(QUERY_MAP), "dataElements");
            }

            @Override List<DataElement> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName," +
                        "type,numberType,textType,zeroIsSignificant,categoryCombo[id]");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getDataElements(QUERY_MAP), "dataElements");
            }

            @Override List<DataElement> getItemsFromDb() {
                return new Select().from(DataElement.class).queryList();
            }
        }.run();
    }
    */

    private List<DataSetToDataElementRelation> buildDataSetToDataElementRelations(List<DataSet> dataSets) {
        List<DataSetToDataElementRelation> relations = new ArrayList<>();
        if (dataSets == null || dataSets.isEmpty()) {
            return relations;
        }

        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataElements() == null ||
                    dataSet.getDataElements().isEmpty()) {
                continue;
            }

            for (DataElement dataElement : dataSet.getDataElements()) {
                DataSetToDataElementRelation relation = new DataSetToDataElementRelation();
                relation.setDataSet(dataSet);
                relation.setDataElement(dataElement);
                relations.add(relation);
            }
        }
        return relations;
    }

    private List<CategoryCombo> getCategoryCombos(List<DataSet> dataSets) throws RetrofitError {
        final Set<String> categoryComboIds = new HashSet<>();
        if (dataSets != null && !dataSets.isEmpty()) {
            for (DataSet dataSet : dataSets) {
                categoryComboIds.add(dataSet.getCategoryCombo().getId());
            }
        }

        return new AbsBaseIdentifiableController<CategoryCombo>() {

            @Override List<CategoryCombo> getNewBasicItems() {
                if (categoryComboIds.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,lastUpdated");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(categoryComboIds) + "]");
                return unwrapResponse(mService.getCategoryCombos(QUERY_MAP), "categoryCombos");
            }

            @Override List<CategoryCombo> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName," +
                        "dimensionType,categories[id]");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getCategoryCombos(QUERY_MAP), "categoryCombos");
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

    private List<Category> getCategories(List<CategoryCombo> catCombos) throws RetrofitError {
        final Set<String> categoryIds = new HashSet<>();
        if (catCombos != null && catCombos.size() > 0) {
            for (CategoryCombo catCombo : catCombos) {
                categoryIds.addAll(toListIds(catCombo.getCategories()));
            }
        }

        return new AbsBaseIdentifiableController<Category>() {

            @Override List<Category> getNewBasicItems() {
                if (categoryIds.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,lastUpdated");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(categoryIds) + "]");
                return unwrapResponse(mService.getCategories(QUERY_MAP), "categories");
            }

            @Override List<Category> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName, " +
                        "dimension,dataDimension,dataDimensionType,categoryOptions[id]");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getCategories(QUERY_MAP), "categories");
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

    private List<CategoryOption> getCategoryOptions(List<Category> categories) throws RetrofitError {
        final Set<String> categoryOptions = new HashSet<>();
        if (categories != null && categories.size() > 0) {
            for (Category category : categories) {
                categoryOptions.addAll(toListIds(category.getCategoryOptions()));
            }
        }

        return new AbsBaseIdentifiableController<CategoryOption>() {

            @Override List<CategoryOption> getNewBasicItems() {
                if (categoryOptions.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,lastUpdated");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(categoryOptions) + "]");
                return unwrapResponse(mService.getCategoryOptions(QUERY_MAP), "categoryOptions");
            }

            @Override List<CategoryOption> getNewFullItems(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    return new ArrayList<>();
                }

                final Map<String, String> QUERY_MAP = new HashMap<>();
                QUERY_MAP.put("fields", "id,created,lastUpdated,name,displayName");
                QUERY_MAP.put("filter", "id:in:[" + Joiner.on(",").join(ids) + "]");
                return unwrapResponse(mService.getCategoryOptions(QUERY_MAP), "categoryOptions");
            }

            @Override List<CategoryOption> getItemsFromDb() {
                return new Select().from(CategoryOption.class).queryList();
            }
        }.run();
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
