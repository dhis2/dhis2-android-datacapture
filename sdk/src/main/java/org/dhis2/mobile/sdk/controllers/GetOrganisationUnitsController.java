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

import org.dhis2.mobile.sdk.DhisManager;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.tasks.GetAssignedOrganisationUnitsTask;
import org.dhis2.mobile.sdk.network.tasks.GetOrganisationUnitsTask;
import org.dhis2.mobile.sdk.persistence.handlers.DbManager;
import org.dhis2.mobile.sdk.persistence.models.Session;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toIds;
import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;

public final class GetOrganisationUnitsController implements IController<List<OrganisationUnit>> {
    private final DhisManager mDhisManager;
    private final Session mSession;

    public GetOrganisationUnitsController(DhisManager dhisManager, Session session) {
        mDhisManager = dhisManager;
        mSession = session;
    }

    @Override
    public List<OrganisationUnit> run() throws APIException {
        Map<String, OrganisationUnit> newShortUnits = getNewBaseOrganisationUnits();
        Map<String, OrganisationUnit> oldUnits = getOldFullOrganisationUnits();

        List<String> unitsToDownload = new ArrayList<>();
        for (String newOrgUnitKey : newShortUnits.keySet()) {
            OrganisationUnit newOrgUnit = newShortUnits.get(newOrgUnitKey);
            OrganisationUnit oldOrgUnit = oldUnits.get(newOrgUnitKey);

            // it means we have to fetch full
            // version of new OrganisationUnit
            if (oldOrgUnit == null) {
                unitsToDownload.add(newOrgUnitKey);
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newOrgUnit.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldOrgUnit.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                // we need to update current version
                unitsToDownload.add(newOrgUnitKey);
            }
        }

        Map<String, OrganisationUnit> newUnits = getNewFullOrganisationUnits(unitsToDownload);
        List<OrganisationUnit> combinedUnits = new ArrayList<>();
        for (String newOrgUnitKey : newShortUnits.keySet()) {
            OrganisationUnit newUnit = newUnits.get(newOrgUnitKey);
            OrganisationUnit oldUnit = oldUnits.get(newOrgUnitKey);

            if (newUnit != null) {
                combinedUnits.add(newUnit);
                continue;
            }

            if (oldUnit != null) {
                combinedUnits.add(oldUnit);
            }
        }

        return combinedUnits;
    }

    private Map<String, OrganisationUnit> getNewBaseOrganisationUnits() throws APIException {
        List<OrganisationUnit> units = (new GetAssignedOrganisationUnitsTask(
                mDhisManager, mSession.getServerUri(), mSession.getCredentials()
        )).run();

        List<OrganisationUnit> childUnits = (new GetOrganisationUnitsTask(
                mDhisManager, mSession.getServerUri(), mSession.getCredentials(),
                toIds(units), null, true
        )).run();

        units.addAll(childUnits);
        return toMap(units);
    }

    private Map<String, OrganisationUnit> getNewFullOrganisationUnits(List<String> unitsToDownload) throws APIException {
        Map<String, OrganisationUnit> map = new HashMap<>();
        if (unitsToDownload.size() > 0) {
            map = toMap(
                    (new GetOrganisationUnitsTask(
                            mDhisManager, mSession.getServerUri(), mSession.getCredentials(),
                            null, unitsToDownload, false
                    )).run()
            );
        }
        return map;
    }

    private Map<String, OrganisationUnit> getOldFullOrganisationUnits() {
        Map<String, OrganisationUnit> map = new HashMap<>();
        List<OrganisationUnit> orgUnits = DbManager.with(OrganisationUnit.class).query();
        for (OrganisationUnit orgUnit : orgUnits) {
            /* List<DataSet> dataSets = mUnitDataSetHandler
                    .queryDataSets(orgUnit.getId()); */
            List<DataSet> dataSets = DbManager.with(OrganisationUnit.class)
                    .queryRelatedModels(DataSet.class, orgUnit.getId());
            orgUnit.setDataSets(dataSets);
            map.put(orgUnit.getId(), orgUnit);
        }
        return map;
    }
}
