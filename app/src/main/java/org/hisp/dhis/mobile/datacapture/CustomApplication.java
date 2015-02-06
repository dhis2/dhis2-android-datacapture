package org.hisp.dhis.mobile.datacapture;

import android.app.Application;

import org.hisp.dhis.mobile.datacapture.api.android.managers.Base64Manager;
import org.hisp.dhis.mobile.datacapture.api.android.managers.JsonManager;
import org.hisp.dhis.mobile.datacapture.api.android.managers.NetworkManager;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.ui.activities.LoginActivity;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.utils.PreferenceUtils;

public class CustomApplication extends Application {
    private DHISService mDHISService;

    @Override
    public void onCreate() {
        super.onCreate();

        DHISManager manager = DHISManager.getInstance();
        manager.setBase64Manager(new Base64Manager());
        manager.setJsonManager(new JsonManager());
        manager.setNetworkManager(new NetworkManager());

        String serverUrl = PreferenceUtils.get(this, LoginActivity.SERVER_URL);
        String credentials = PreferenceUtils.get(this, LoginActivity.USER_CREDENTIALS);

        manager.setServerUrl(serverUrl);
        manager.setCredentials(credentials);

        mDHISService = new DHISService(getBaseContext());
        BusProvider.getInstance().register(mDHISService);
    }
}
