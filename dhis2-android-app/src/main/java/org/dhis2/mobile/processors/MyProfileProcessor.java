/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.processors;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.dhis2.mobile.io.handlers.UserAccountHandler;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.ui.fragments.MyProfileFragment;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;

public class MyProfileProcessor {
    private MyProfileProcessor() { }

    // This method will be invoked from service
    // in order to post new profile info to server.
    public static void uploadProfileInfo(Context context, ArrayList<Field> fields) {
        PrefUtils.setResourceState(context,
                PrefUtils.Resources.PROFILE_DETAILS,
                PrefUtils.State.REFRESHING);

        String accountInfo = UserAccountHandler.fromFields(fields);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT, TextFileUtils.FileNames.ACCOUNT_INFO, accountInfo);

        String url = PrefUtils.getServerURL(context) + URLConstants.API_USER_ACCOUNT_URL;
        String creds = PrefUtils.getCredentials(context);
        Response response = HTTPClient.post(url, creds, accountInfo);

        if (HTTPClient.isError(response.getCode())) {
            PrefUtils.setAccountUpdateFlag(context, true);
        }

        PrefUtils.setResourceState(context,
                PrefUtils.Resources.PROFILE_DETAILS,
                PrefUtils.State.UP_TO_DATE);

        Intent intent = new Intent(MyProfileFragment.ON_UPLOAD_FINISHED_LISTENER_TAG);
        intent.putExtra(Response.CODE, response.getCode());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // updateProfileInfo() is invoked from service
    // in order to get latest profile info.
    public static void updateProfileInfo(Context context) {
        PrefUtils.setResourceState(context,
                PrefUtils.Resources.PROFILE_DETAILS,
                PrefUtils.State.REFRESHING);

        String url = PrefUtils.getServerURL(context) + URLConstants.API_USER_ACCOUNT_URL;
        String creds = PrefUtils.getCredentials(context);
        Response response = HTTPClient.get(url, creds);

        PrefUtils.State profileState;
        if (!HTTPClient.isError(response.getCode())) {
            profileState = PrefUtils.State.UP_TO_DATE;
            TextFileUtils.writeTextFile(context, TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ACCOUNT_INFO, response.getBody());
        } else {
            profileState = PrefUtils.State.ATTEMPT_TO_REFRESH_IS_MADE;
        }

        PrefUtils.setResourceState(context,
                PrefUtils.Resources.PROFILE_DETAILS,
                profileState);

        Intent intent = new Intent(MyProfileFragment.ON_UPDATE_FINISHED_LISTENER_TAG);
        intent.putExtra(Response.CODE, response.getCode());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
