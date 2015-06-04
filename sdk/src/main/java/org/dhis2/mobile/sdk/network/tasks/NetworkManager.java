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

package org.dhis2.mobile.sdk.network.tasks;

import android.net.Uri;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.OkHttpClient;

import org.dhis2.mobile.sdk.persistence.models.Category;
import org.dhis2.mobile.sdk.persistence.models.CategoryCombo;
import org.dhis2.mobile.sdk.persistence.models.CategoryOption;
import org.dhis2.mobile.sdk.persistence.models.CategoryOptionCombo;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.converters.IJsonManager;
import org.dhis2.mobile.sdk.network.converters.JsonManager;
import org.dhis2.mobile.sdk.network.http.Base64Manager;
import org.dhis2.mobile.sdk.network.http.HttpManager;
import org.dhis2.mobile.sdk.network.http.IBase64Manager;
import org.dhis2.mobile.sdk.network.http.IHttpManager;
import org.dhis2.mobile.sdk.network.models.Credentials;
import org.dhis2.mobile.sdk.utils.ILogManager;
import org.dhis2.mobile.sdk.utils.LogManager;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager implements INetworkManager {
    private static NetworkManager mNetworkManager;

    private final IHttpManager mHttpManager;
    private final IJsonManager mJsonManager;
    private final IBase64Manager mBase64Manager;
    private final ILogManager mLogManager;

    private Uri mServerUri;
    private Credentials mCredentials;

    private NetworkManager() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        mHttpManager = new HttpManager(new OkHttpClient());
        mJsonManager = new JsonManager(mapper);
        mBase64Manager = new Base64Manager();
        mLogManager = new LogManager();
    }

    public static NetworkManager getInstance() {
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager();
        }

        return mNetworkManager;
    }

    @Override
    public IBase64Manager getBase64Manager() {
        return mBase64Manager;
    }

    @Override
    public IHttpManager getHttpManager() {
        return mHttpManager;
    }

    @Override
    public IJsonManager getJsonManager() {
        return mJsonManager;
    }

    @Override
    public ILogManager getLogManager() {
        return mLogManager;
    }

    @Override
    public Uri getServerUri() {
        return mServerUri;
    }

    @Override
    public void setServerUri(Uri uri) {
        mServerUri = uri;
    }

    @Override
    public Credentials getCredentials() {
        return mCredentials;
    }

    @Override
    public void setCredentials(Credentials credentials) {
        mCredentials = credentials;
    }

    public UserAccount loginUser(Uri serverUri, Credentials credentials) throws APIException {
        return new LoginUserTask(this, serverUri, credentials).run();
    }

    public List<OrganisationUnit> getAssignedOrganisationUnits() throws APIException {
        return new GetAssignedOrganisationUnitsTask(this).run();
    }

    public List<OrganisationUnit> getChildOrganisationUnits(List<String> parentIds, boolean onlyBasicFields) throws APIException {
        if (parentIds == null || parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new GetOrganisationUnitsTask(this, parentIds, null, onlyBasicFields).run();
    }

    public List<OrganisationUnit> getOrganisationUnitsByIds(List<String> ids, boolean onlyBasicFields) throws APIException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return new GetOrganisationUnitsTask(this, null, ids, onlyBasicFields).run();
    }

    public List<DataSet> getDataSetsByIds(List<String> ids, boolean onlyBasicValues) throws APIException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return new GetDataSetsTask(this, ids, onlyBasicValues).run();
    }

    public List<CategoryCombo> getCategoryCombosByIds(List<String> ids, boolean onlyBasicFields) throws APIException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return new GetCategoryCombosTask(this, ids, onlyBasicFields).run();
    }

    public List<Category> getCategoriesByIds(List<String> ids, boolean onlyBasicFields) throws APIException {
        return new GetCategoriesTask(this, ids, onlyBasicFields).run();
    }

    public List<CategoryOption> getCategoryOptions(List<String> ids) throws APIException {
        return new GetCategoryOptionsTask(this, ids).run();
    }

    public List<CategoryOptionCombo> getCategoryOptionCombosByIds(List<String> ids, boolean onlyBasicFields) throws APIException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return new GetCategoryOptionComboTask(this, ids, onlyBasicFields).run();
    }
}
