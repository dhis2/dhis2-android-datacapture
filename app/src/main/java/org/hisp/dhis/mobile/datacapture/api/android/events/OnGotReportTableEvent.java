package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.APIException;

public final class OnGotReportTableEvent {
    private String mReportTable;
    private APIException mApiException;

    public void setReportTable(String reportTable) {
        mReportTable = reportTable;
    }

    public void setApiException(APIException exception) {
        mApiException = exception;
    }

    public String getReportTable() {
        return mReportTable;
    }

    public APIException getApiException() {
        return mApiException;
    }
}
