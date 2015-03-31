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
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.tasks.GetDataSetsTask;
import org.dhis2.mobile.sdk.persistence.handlers.DataSetHandler;
import org.dhis2.mobile.sdk.persistence.models.Session;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.DbUtils.toMap;

public final class GetDataSetsController implements IController<List<DataSet>> {
    private final DhisManager mDhisManager;
    private final DataSetHandler mDataSetHandler;
    private final Session mSession;
    private final List<String> mDataSetIds;

    public GetDataSetsController(DhisManager dhisManager,
                                 DataSetHandler dataSetHandler,
                                 Session session,
                                 List<String> dataSetIds) {
        mDhisManager = dhisManager;
        mDataSetHandler = dataSetHandler;
        mSession = session;
        mDataSetIds = dataSetIds;
    }

    @Override
    public List<DataSet> run() throws APIException {
        Map<String, DataSet> newBaseDataSets = getNewBaseDataSets();
        Map<String, DataSet> oldDataSets = getOldFullDataSets();

        List<String> dataSetsToDownload = new ArrayList<>();
        for (String newDataSetKey : newBaseDataSets.keySet()) {
            DataSet newDataSet = newBaseDataSets.get(newDataSetKey);
            DataSet oldDataSet = oldDataSets.get(newDataSetKey);

            if (oldDataSet == null) {
                dataSetsToDownload.add(newDataSetKey);
                continue;
            }

            DateTime newLastUpdated = DateTime.parse(newDataSet.getLastUpdated());
            DateTime oldLastUpdated = DateTime.parse(oldDataSet.getLastUpdated());

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                // we need to update current version
                dataSetsToDownload.add(newDataSetKey);
            }
        }

        Map<String, DataSet> newDataSets = getNewFullDataSets(dataSetsToDownload);
        List<DataSet> combinedDataSets = new ArrayList<>();
        for (String newDataSetKey : newBaseDataSets.keySet()) {
            DataSet newDataSet = newDataSets.get(newDataSetKey);
            DataSet oldDataSet = oldDataSets.get(newDataSetKey);

            if (newDataSet != null) {
                combinedDataSets.add(newDataSet);
                continue;
            }

            if (oldDataSet != null) {
                combinedDataSets.add(oldDataSet);
            }
        }
        return combinedDataSets;
    }

    private Map<String, DataSet> getNewFullDataSets(List<String> ids) throws APIException {
        Map<String, DataSet> map = new HashMap<>();
        if (ids.size() > 0) {
            map = toMap((new GetDataSetsTask(
                    mDhisManager, mSession.getServerUri(),
                    mSession.getCredentials(), ids, false
            )).run());
        }
        return map;
    }

    private Map<String, DataSet> getNewBaseDataSets() throws APIException {
        return toMap(
                (new GetDataSetsTask(
                        mDhisManager, mSession.getServerUri(),
                        mSession.getCredentials(), mDataSetIds, true)).run()
        );
    }

    private Map<String, DataSet> getOldFullDataSets() {
        return toMap(mDataSetHandler.query());
    }
}
