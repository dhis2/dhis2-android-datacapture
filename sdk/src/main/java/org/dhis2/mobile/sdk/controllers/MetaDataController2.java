package org.dhis2.mobile.sdk.controllers;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.dhis2.mobile.sdk.DhisManager;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.repository.DhisService;
import org.dhis2.mobile.sdk.network.repository.RepoManager;
import org.dhis2.mobile.sdk.persistence.DbHelper;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.DbOperation;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.models.UnitToDataSetRelation;
import org.dhis2.mobile.sdk.persistence.preferences.LastUpdatedPreferences;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.RetrofitError;

import static org.dhis2.mobile.sdk.utils.NetworkUtils.unwrapResponse;

public final class MetaDataController2 implements IController<Object> {
    private final LastUpdatedPreferences mLastUpdatedPreferences;
    private final DhisService mService;

    public MetaDataController2(LastUpdatedPreferences preferences) {
        mLastUpdatedPreferences = preferences;
        mService = RepoManager.createService(DhisManager.getInstance().getServerUrl(),
                DhisManager.getInstance().getUserCredentials());
    }

    @Override public Object run() throws APIException {
        DateTime lastUpdated = DateTime.now(DateTimeZone
                .forTimeZone(mLastUpdatedPreferences.getServerTimeZone()));
        /* if lastUpdated equals null, it means we have not
        downloaded any metadata from server yet. */
        boolean isUpdating = mLastUpdatedPreferences.getLastUpdated() != null;

        List<OrganisationUnit> units = updateOrganisationUnits(lastUpdated, isUpdating);
        List<DataSet> dataSets = updateDataSets(lastUpdated, isUpdating);
        List<UnitToDataSetRelation> unitToDataSetRelations = buildUnitDataSetRelations(units);

        for (OrganisationUnit unit : units) {
            System.out.println("UNIT: " + unit.getId() + " " + unit.getDisplayName());
        }

        Queue<DbOperation> dbOperations = new LinkedList<>();
        dbOperations.addAll(DbHelper.createOperations(new Select()
                .from(DataSet.class).queryList(), dataSets));
        dbOperations.addAll(DbHelper.createOperations(new Select()
                .from(OrganisationUnit.class).queryList(), units));
        dbOperations.addAll(DbHelper.syncRelationModels(new Select()
                .from(UnitToDataSetRelation.class).queryList(), unitToDataSetRelations));

        DbHelper.applyBatch(dbOperations);

        mLastUpdatedPreferences.setLastUpdated(lastUpdated);
        return new Object();
    }

    private List<OrganisationUnit> updateOrganisationUnits(final DateTime lastUpdated, final boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName,level,dataSets[id]");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return new AbsBaseController<OrganisationUnit>() {

            @Override public List<OrganisationUnit> getExistingItems() {
                return unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_BASIC),
                        "organisationUnits");
            }

            @Override public List<OrganisationUnit> getUpdatedItems() {
                return unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_FULL),
                        "organisationUnits");
            }

            @Override public List<OrganisationUnit> getPersistedItems() {
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

    private List<DataSet> updateDataSets(final DateTime lastUpdated, final boolean isUpdating) throws RetrofitError {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName,expiryDays," +
                "allowFuturePeriods,periodType,categoryCombo[id],dataElements[id]");

        if (isUpdating) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return new AbsBaseController<DataSet>() {

            @Override public List<DataSet> getExistingItems() {
                return unwrapResponse(mService.getDataSets(QUERY_MAP_BASIC), "dataSets");
            }

            @Override public List<DataSet> getUpdatedItems() {
                return unwrapResponse(mService.getDataSets(QUERY_MAP_FULL), "dataSets");
            }

            @Override public List<DataSet> getPersistedItems() {
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
}
