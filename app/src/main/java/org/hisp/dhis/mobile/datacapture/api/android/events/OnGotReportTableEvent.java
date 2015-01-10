package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.ResponseHolder;

public final class OnGotReportTableEvent {
    private ResponseHolder<String> mHolder;

    public ResponseHolder<String> getHolder() {
        return mHolder;
    }

    public void setHolder(ResponseHolder<String> holder) {
        mHolder = holder;
    }
}
