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

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.datacapture.sdk.network.APIException;
import org.hisp.dhis.android.datacapture.sdk.network.models.Credentials;
import org.hisp.dhis.android.datacapture.sdk.network.models.Session;
import org.hisp.dhis.android.datacapture.sdk.network.repository.DhisService;
import org.hisp.dhis.android.datacapture.sdk.network.repository.RepoManager;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.datacapture.sdk.persistence.preferences.LastUpdatedPreferences;
import org.hisp.dhis.android.datacapture.sdk.persistence.preferences.SessionManager;
import org.hisp.dhis.android.datacapture.sdk.persistence.preferences.UserAccountHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.hisp.dhis.android.datacapture.sdk.utils.Preconditions.isNull;

public final class LogInUserController implements IController<UserAccount> {
    private final HttpUrl mServerUrl;
    private final Credentials mCredentials;
    private final UserAccountHandler mUserAccountHandler;
    private final LastUpdatedPreferences mLastUpdatedPreferences;
    private final DhisService mService;

    public LogInUserController(UserAccountHandler userAccountHandler,
                               LastUpdatedPreferences lastUpdatedPreferences,
                               HttpUrl serverUrl, Credentials credentials) {
        mUserAccountHandler = isNull(userAccountHandler, "UserAccountHandler must not be null");
        mLastUpdatedPreferences = isNull(lastUpdatedPreferences, "LastUpdatedPreferences must not be null");
        mServerUrl = isNull(serverUrl, "Server URI must not be null");
        mCredentials = isNull(credentials, "User credentials must not be null");
        mService = RepoManager.createService(mServerUrl, mCredentials);
    }

    @Override
    public UserAccount run() throws APIException {
        // First, we need to get UserAccount
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", UserAccount.ALL_USER_ACCOUNT_FIELDS);
        UserAccount userAccount = mService.getCurrentUserAccount(QUERY_PARAMS);

        // Second, we need to get SystemInfo about server.
        SystemInfo systemInfo = mService.getSystemInfo();

        // if we got here, it means http
        // request was executed successfully

        /* save user credentials */
        Session session = new Session(mServerUrl, mCredentials);
        SessionManager.getInstance().put(session);

        /* save user account details */
        mUserAccountHandler.put(userAccount);

        /* get server time zone and save it */
        TimeZone serverTimeZone = systemInfo.getServerDate()
                .getZone().toTimeZone();
        mLastUpdatedPreferences.setServerTimeZone(serverTimeZone);
        return userAccount;
    }
}
