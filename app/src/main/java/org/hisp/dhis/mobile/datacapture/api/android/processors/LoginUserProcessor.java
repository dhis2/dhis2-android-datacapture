package org.hisp.dhis.mobile.datacapture.api.android.processors;

import android.content.Context;
import android.os.AsyncTask;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.android.events.LoginUserEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnUserLoginEvent;
import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;
import org.hisp.dhis.mobile.datacapture.api.network.ApiRequestCallback;
import org.hisp.dhis.mobile.datacapture.api.network.Response;
import org.hisp.dhis.mobile.datacapture.ui.activities.LoginActivity;
import org.hisp.dhis.mobile.datacapture.utils.PreferenceUtils;

public class LoginUserProcessor extends AsyncTask<Void, Void, OnUserLoginEvent> {
    private Context mContext;
    private LoginUserEvent mEvent;

    public LoginUserProcessor(Context context, LoginUserEvent event) {
        mContext = context;
        mEvent = event;
    }

    @Override
    protected OnUserLoginEvent doInBackground(Void... params) {
        final ResponseHolder<UserAccount> holder = new ResponseHolder<>();
        final OnUserLoginEvent event = new OnUserLoginEvent();

        DHISManager manager = DHISManager.getInstance();
        manager.setServerUrl(mEvent.getServerUrl());

        final String credentials = DHISManager.getInstance()
                .getBase64Manager().toBase64(mEvent.getUsername(), mEvent.getPassword());
        manager.login(new ApiRequestCallback<UserAccount>() {
            @Override
            public void onSuccess(Response response, UserAccount userAccount) {
                holder.setItem(userAccount);
                PreferenceUtils.put(mContext,
                        LoginActivity.SERVER_URL, mEvent.getServerUrl());
                PreferenceUtils.put(mContext,
                        LoginActivity.USER_CREDENTIALS, credentials);
            }

            @Override
            public void onFailure(APIException e) {
                holder.setException(e);
            }
        }, mEvent.getUsername(), mEvent.getPassword());

        event.setResponseHolder(holder);
        return event;
    }

    @Override
    protected void onPostExecute(OnUserLoginEvent event) {
        BusProvider.getInstance().post(event);
    }
}
