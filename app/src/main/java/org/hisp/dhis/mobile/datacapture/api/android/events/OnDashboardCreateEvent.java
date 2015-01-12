package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;

public final class OnDashboardCreateEvent {
    private ResponseHolder<String> mResponseHolder;

    public ResponseHolder<String> getResponseHolder() {
        return mResponseHolder;
    }

    public void setResponseHolder(ResponseHolder<String> responseHolder) {
        mResponseHolder = responseHolder;
    }
}
