package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.APIException;

public class OnDashboardsSyncedEvent {
    private APIException mException;

    public APIException getException() {
        return mException;
    }

    public void setException(APIException exception) {
        this.mException = exception;
    }
}
