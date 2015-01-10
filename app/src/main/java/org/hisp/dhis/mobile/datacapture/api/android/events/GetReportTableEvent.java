package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class GetReportTableEvent {
    private String mUrl;

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}
