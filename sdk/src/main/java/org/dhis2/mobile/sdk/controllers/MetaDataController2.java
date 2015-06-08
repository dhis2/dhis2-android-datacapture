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
import org.dhis2.mobile.sdk.persistence.preferences.LastUpdatedPreferences;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import retrofit.RetrofitError;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;
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

        Set<OrganisationUnit> units = updateOrganisationUnits(lastUpdated, isUpdating);
        Set<DataSet> dataSets = updateDataSets(lastUpdated, isUpdating);

        for (OrganisationUnit unit : units) {
            System.out.println("UNIT: " + unit.getId() + " " + unit.getDisplayName());
        }

        Stack<DbOperation> dbOperations = new Stack<>();
        dbOperations.addAll(DbHelper.createOperations(new Select()
                .from(OrganisationUnit.class).queryList(), units));
        dbOperations.addAll(DbHelper.createOperations(new Select()
                .from(DataSet.class).queryList(), dataSets));

        DbHelper.applyBatch(dbOperations);

        mLastUpdatedPreferences.setLastUpdated(lastUpdated);
        return new Object();
    }

    private Set<OrganisationUnit> updateOrganisationUnits(DateTime lastUpdated, boolean isUpdating) throws RetrofitError {
        final Set<OrganisationUnit> organisationUnits = new HashSet<>();
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName,level,dataSets[id]");

        if (!isUpdating) {
            organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits"));
            return organisationUnits;
        }

        QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());

        organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_BASIC), "organisationUnits"));
        organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits"));

        return organisationUnits;
    }

    private List<DbOperation> updateOrganisationUnits2(DateTime lastUpdated, boolean isUpdating) throws RetrofitError {
        // final Set<OrganisationUnit> organisationUnits = new HashSet<>();
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName,level,dataSets[id]");

        if (!isUpdating) {
            List<OrganisationUnit> units = unwrapResponse(mService
                    .getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits");
            return DbHelper.save(units);
            // organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits"));
            // return organisationUnits;
        }

        QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());

        // organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_BASIC), "organisationUnits"));
        // organisationUnits.addAll(unwrapResponse(mService.getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits"));
        // return organisationUnits;

        List<OrganisationUnit> basicUnits = unwrapResponse(mService
                .getOrganisationUnits(QUERY_MAP_BASIC), "organisationUnits");
        List<OrganisationUnit> newFullUnits = unwrapResponse(mService
                .getOrganisationUnits(QUERY_MAP_FULL), "organisationUnits");

        Map<String, OrganisationUnit> newOrgUnits = toMap(newFullUnits);
        Map<String, OrganisationUnit> dbOrgUnits = toMap(new Select()
                .from(OrganisationUnit.class).queryList());
        Map<String, OrganisationUnit> currentOrgUnits = new HashMap<>();

        for (OrganisationUnit orgUnit : basicUnits) {
            String orgUnitKey = orgUnit.getId();
            OrganisationUnit dbOrgUnit = dbOrgUnits.get(orgUnitKey);
            OrganisationUnit newOrgUnit = newOrgUnits.get(orgUnitKey);

            if (newOrgUnit != null) {
                currentOrgUnits.put(orgUnitKey, newOrgUnit);
                continue;
            }

            if (dbOrgUnit != null) {
                currentOrgUnits.put(orgUnitKey, dbOrgUnit);
            }
        }

        return null;
    }

    private List<OrganisationUnit> updateOrganisationUnits3(final DateTime lastUpdated, final boolean isUpdating) {
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
                return new Select().from(OrganisationUnit.class).queryList();
            }
        }.run();
    }

    private Set<DataSet> updateDataSets(DateTime lastUpdated, boolean isUpdating) throws RetrofitError {
        final Set<DataSet> dataSets = new HashSet<>();
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName,expiryDays," +
                "allowFuturePeriods,periodType,categoryCombo[id],dataElements[id]");

        if (!isUpdating) {
            dataSets.addAll(unwrapResponse(mService.getDataSets(QUERY_MAP_FULL), "dataSets"));
            return dataSets;
        }

        QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());

        dataSets.addAll(unwrapResponse(mService.getDataSets(QUERY_MAP_BASIC), "dataSets"));
        dataSets.addAll(unwrapResponse(mService.getDataSets(QUERY_MAP_FULL), "dataSets"));

        return dataSets;
    }
}
