package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;

public final class DashboardDeleteEvent {
    private DBItemHolder<Dashboard> mDashboard;

    public DBItemHolder<Dashboard> getDashboard() {
        return mDashboard;
    }

    public void setDashboard(DBItemHolder<Dashboard> dashboard) {
        mDashboard = dashboard;
    }
}
