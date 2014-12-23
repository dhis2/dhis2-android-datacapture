package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.APIException;
import org.hisp.dhis.mobile.datacapture.api.models.UserAccount;

public class OnUserLoginEvent {
    private UserAccount mUserAccount;
    private APIException mApiException;

    public UserAccount getUserAccount() {
        return mUserAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.mUserAccount = userAccount;
    }

    public APIException getApiException() {
        return mApiException;
    }

    public void setApiException(APIException apiException) {
        this.mApiException = apiException;
    }
}