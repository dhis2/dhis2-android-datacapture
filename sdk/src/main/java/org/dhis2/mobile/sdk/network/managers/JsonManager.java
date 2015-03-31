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

package org.dhis2.mobile.sdk.network.managers;

import com.google.gson.Gson;

import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.sdk.entities.CategoryCombo;
import org.dhis2.mobile.sdk.entities.CategoryOption;
import org.dhis2.mobile.sdk.entities.CategoryOptionCombo;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.entities.UserAccount;
import org.dhis2.mobile.sdk.network.converters.CategoryComboConverter;
import org.dhis2.mobile.sdk.network.converters.CategoryConverter;
import org.dhis2.mobile.sdk.network.converters.CategoryOptionComboConverter;
import org.dhis2.mobile.sdk.network.converters.CategoryOptionConverter;
import org.dhis2.mobile.sdk.network.converters.DataSetsConverter;
import org.dhis2.mobile.sdk.network.converters.IJsonConverter;
import org.dhis2.mobile.sdk.network.converters.OrgUnitsConverter;
import org.dhis2.mobile.sdk.network.converters.UserAccountConverter;

import java.util.List;

public class JsonManager implements IJsonManager {
    private Gson mGson;

    public JsonManager() {
        mGson = new Gson();
    }

    @Override
    public IJsonConverter<UserAccount, UserAccount> getUserAccountConverter() {
        return new UserAccountConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<OrganisationUnit>> getOrgUnitsConverter() {
        return new OrgUnitsConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<DataSet>> getDataSetsConverter() {
        return new DataSetsConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<CategoryCombo>> getCategoryCombosConverter() {
        return new CategoryComboConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<CategoryOptionCombo>> getCategoryOptionComboConverter() {
        return new CategoryOptionComboConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<Category>> getCategoryConverter() {
        return new CategoryConverter(mGson);
    }

    @Override
    public IJsonConverter<String, List<CategoryOption>> getCategoryOptionConverter() {
        return new CategoryOptionConverter(mGson);
    }
}
