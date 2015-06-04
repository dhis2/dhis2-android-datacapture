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
import org.dhis2.mobile.sdk.persistence.models.CategoryOptionCombo;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.models.Session;
import org.dhis2.mobile.sdk.network.tasks.NetworkManager;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toMap;

public final class GetCategoryOptionCombosController implements IController<List<CategoryOptionCombo>> {
    private final DhisManager mDhisManager;
    private final Session mSession;
    private final List<String> mCocIds;

    public GetCategoryOptionCombosController(DhisManager dhisManager,
                                             Session session, List<String> ids) {
        mDhisManager = dhisManager;
        mSession = session;
        mCocIds = ids;
    }

    @Override
    public List<CategoryOptionCombo> run() throws APIException {
        Map<String, CategoryOptionCombo> newBaseCocs = getNewBaseCocs();
        Map<String, CategoryOptionCombo> oldCocs = getOldFullCocs();

        List<String> cocsToDownload = new ArrayList<>();
        for (String newCocKey : newBaseCocs.keySet()) {
            CategoryOptionCombo newCoc = newBaseCocs.get(newCocKey);
            CategoryOptionCombo oldCoc = oldCocs.get(newCocKey);

            if (oldCoc == null) {
                cocsToDownload.add(newCocKey);
                continue;
            }

            DateTime newLastUpdated = newCoc.getLastUpdated();
            DateTime oldLastUpdated = oldCoc.getLastUpdated();

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                // we need to update current version
                cocsToDownload.add(newCocKey);
            }
        }

        Map<String, CategoryOptionCombo> newCocs = getNewFullCocs(cocsToDownload);
        List<CategoryOptionCombo> combinedCocs = new ArrayList<>();
        for (String newCocKey : newBaseCocs.keySet()) {
            CategoryOptionCombo newCoc = newCocs.get(newCocKey);
            CategoryOptionCombo oldCoc = oldCocs.get(newCocKey);

            if (newCoc != null) {
                combinedCocs.add(newCoc);
                continue;
            }

            if (oldCoc != null) {
                combinedCocs.add(oldCoc);
            }
        }
        return combinedCocs;
    }

    private Map<String, CategoryOptionCombo> getNewBaseCocs() throws APIException {
        return toMap(NetworkManager.getInstance()
                .getCategoryOptionCombosByIds(mCocIds, true));
    }

    private Map<String, CategoryOptionCombo> getNewFullCocs(List<String> ids) throws APIException {
        return toMap(NetworkManager.getInstance()
                .getCategoryOptionCombosByIds(ids, false));
    }

    private Map<String, CategoryOptionCombo> getOldFullCocs() {
        return toMap(new Select().from(CategoryOptionCombo.class).queryList());
    }
}
