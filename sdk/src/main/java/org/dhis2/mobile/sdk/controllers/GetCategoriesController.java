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
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.tasks.GetCategoriesTask;
import org.dhis2.mobile.sdk.persistence.models.Session;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dhis2.mobile.sdk.persistence.DbUtils.toMap;

public class GetCategoriesController implements IController<List<Category>> {
    private final DhisManager mDhisManager;
    private final Session mSession;
    private final List<String> mCategoryIds;

    public GetCategoriesController(DhisManager dhisManager,
                                   Session session, List<String> ids) {
        mDhisManager = dhisManager;
        mSession = session;
        mCategoryIds = ids;
    }

    @Override
    public List<Category> run() throws APIException {
        Map<String, Category> newBaseCategories = getNewBaseCategories();
        Map<String, Category> oldCategories = getOldFullCategories();

        List<String> catsToDownload = new ArrayList<>();
        for (String newCatKey : newBaseCategories.keySet()) {
            Category newCat = newBaseCategories.get(newCatKey);
            Category oldCat = oldCategories.get(newCatKey);

            if (oldCat == null) {
                catsToDownload.add(newCatKey);
                continue;
            }

            DateTime newLastUpdated = newCat.getLastUpdated();
            DateTime oldLastUpdated = oldCat.getLastUpdated();

            if (newLastUpdated.isAfter(oldLastUpdated)) {
                // we need to update current version
                catsToDownload.add(newCatKey);
            }
        }

        Map<String, Category> newCats = getNewFullCategories(catsToDownload);
        List<Category> combinedCats = new ArrayList<>();
        for (String newCatKey : newBaseCategories.keySet()) {
            Category newCat = newCats.get(newCatKey);
            Category oldCat = oldCategories.get(newCatKey);

            if (newCat != null) {
                combinedCats.add(newCat);
                continue;
            }

            if (oldCat != null) {
                combinedCats.add(oldCat);
            }
        }
        return combinedCats;
    }

    private Map<String, Category> getNewBaseCategories() throws APIException {
        return toMap(
                (new GetCategoriesTask(mDhisManager,
                        mSession.getServerUri(), mSession.getCredentials(),
                        mCategoryIds, true)).run()
        );
    }

    private Map<String, Category> getNewFullCategories(List<String> ids) throws APIException {
        return toMap(
                (new GetCategoriesTask(mDhisManager,
                        mSession.getServerUri(), mSession.getCredentials(),
                        ids, false)).run()
        );
    }

    private Map<String, Category> getOldFullCategories() {
        return toMap(new Select().from(Category.class).queryList());
    }
}
