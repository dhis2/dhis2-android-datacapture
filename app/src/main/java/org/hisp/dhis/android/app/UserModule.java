/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.app;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.bindings.commons.ApiExceptionHandler;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultAppAccountManager;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultNotificationHandler;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultUserModule;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenterImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import javax.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

@Module
public class UserModule implements DefaultUserModule {

    public UserModule() {
        this(null);
    }

    public UserModule(String serverUrl) {
        if (!isEmpty(serverUrl)) {
            // it can throw exception in case if configuration has failed
            Configuration configuration = new Configuration(serverUrl);
            D2.configure(configuration).toBlocking().first();
        }
    }

    @Provides
    @Nullable
    @PerUser
    @Override
    public CurrentUserInteractor providesCurrentUserInteractor() {
        if (D2.isConfigured()) {
            return D2.me();
        }

        return null;
    }

    @Provides
    @Nullable
    @PerUser
    public UserOrganisationUnitInteractor providesUserOrganisationUnitInteractor() {
        if (D2.isConfigured()) {
            return D2.me().organisationUnits();
        }
        return null;
    }

    @Provides
    @PerUser
    public LauncherPresenter providesLauncherPresenter(
            @Nullable CurrentUserInteractor accountInteractor) {
        return new LauncherPresenterImpl(accountInteractor);
    }

    @Provides
    @PerUser
    public LoginPresenter providesLoginPresenter(
            @Nullable CurrentUserInteractor accountInteractor,
            ApiExceptionHandler apiExceptionHandler, Logger logger) {
        return new LoginPresenterImpl(accountInteractor, apiExceptionHandler, logger);
    }

    @Provides
    @PerUser
    @Override
    public ProfilePresenter providesProfilePresenter(
            CurrentUserInteractor userInteractor, SyncDateWrapper dateWrapper, DefaultAppAccountManager appAccountManager, DefaultNotificationHandler defaultNotificationHandler, Logger logger) {
        return new ProfilePresenterImpl(userInteractor, dateWrapper, appAccountManager, defaultNotificationHandler, logger);
    }

    @Override
    public SettingsPresenter providesSettingsPresenter(AppPreferences appPreferences,
                                                       DefaultAppAccountManager appAccountManager) {
        return new SettingsPresenterImpl(appPreferences, appAccountManager);
    }

    @Override
    public DefaultAppAccountManager providesAppAccountManager(Context context, AppPreferences appPreferences, CurrentUserInteractor currentUserInteractor, Logger logger) {
        return null;
    }

    @Override
    public DefaultNotificationHandler providesNotificationHandler(Context context) {
        return null;
    }

    @Provides
    @PerUser
    @Override
    public HomePresenter providesHomePresenter(
            CurrentUserInteractor currentUserInteractor, SyncDateWrapper syncDateWrapper, Logger logger) {
        return new HomePresenterImpl(currentUserInteractor, syncDateWrapper, logger);
    }

    @Provides
    @PerUser
    public SettingsPresenter provideSettingsPresenter(
            AppPreferences appPreferences, DefaultAppAccountManager appAccountManager) {
        return new SettingsPresenterImpl(appPreferences, appAccountManager);
    }
}
