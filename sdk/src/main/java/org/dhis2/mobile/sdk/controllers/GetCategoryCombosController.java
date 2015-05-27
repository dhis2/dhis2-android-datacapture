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
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.tasks.GetCategoryCombosTask;
import org.dhis2.mobile.sdk.persistence.models.Session;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toMap;

public final class GetCategoryCombosController implements IController<List<CategoryCombo>> {
    private final DhisManager mDhisManager;
    private final Session mSession;
    private final List<String> mCategoryCombos;

    public GetCategoryCombosController(DhisManager dhisManager,
                                       Session session, List<String> categoryCombos) {
        mDhisManager = dhisManager;
        mSession = session;
        mCategoryCombos = categoryCombos;
    }

    @Override
    public List<CategoryCombo> run() throws APIException {
        Map<String, CategoryCombo> newBaseCategoryCombos = getNewBaseCategoryCombos();
        Map<String, CategoryCombo> oldCategoryCombos = getOldFullCategoryCombos();

        List<String> categoryCombosToDownload = new ArrayList<>();
        for (String newCategoryComboKey : newBaseCategoryCombos.keySet()) {
            CategoryCombo newCategoryCombo = newBaseCategoryCombos.get(newCategoryComboKey);
            CategoryCombo oldCategoryCombo = oldCategoryCombos.get(newCategoryComboKey);

            if (oldCategoryCombo == null) {
                categoryCombosToDownload.add(newCategoryComboKey);
                continue;
            }

            DateTime newLastUpdated = newCategoryCombo.getLastUpdated();
            DateTime oldLastUpdated = oldCategoryCombo.getLastUpdated();

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                // we need to update current version
                categoryCombosToDownload.add(newCategoryComboKey);
            }
        }

        Map<String, CategoryCombo> newCategoryCombos =
                getNewFullCategoryCombos(categoryCombosToDownload);
        List<CategoryCombo> combinedCategoryCombos = new ArrayList<>();
        for (String newCategoryComboKey : newBaseCategoryCombos.keySet()) {
            CategoryCombo newCategoryCombo = newCategoryCombos.get(newCategoryComboKey);
            CategoryCombo oldCategoryCombo = oldCategoryCombos.get(newCategoryComboKey);

            if (newCategoryCombo != null) {
                combinedCategoryCombos.add(newCategoryCombo);
                continue;
            }

            if (oldCategoryCombo != null) {
                combinedCategoryCombos.add(oldCategoryCombo);
            }
        }
        return combinedCategoryCombos;
    }

    private Map<String, CategoryCombo> getNewBaseCategoryCombos() throws APIException {
        return toMap(
                (new GetCategoryCombosTask(mDhisManager,
                        mSession.getServerUri(), mSession.getCredentials(),
                        mCategoryCombos, true)).run()
        );
    }

    private Map<String, CategoryCombo> getNewFullCategoryCombos(List<String> ids) throws APIException {
        return toMap(
                (new GetCategoryCombosTask(mDhisManager,
                        mSession.getServerUri(), mSession.getCredentials(),
                        ids, false)).run()
        );
    }

    private Map<String, CategoryCombo> getOldFullCategoryCombos() {
        return toMap(new Select().from(CategoryCombo.class).queryList());
    }
}
