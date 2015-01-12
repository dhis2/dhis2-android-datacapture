package org.hisp.dhis.mobile.datacapture.api.android.events;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;

public class DashboardUpdateEvent {
    private DBItemHolder<Dashboard> mDbItem;
    private Dashboard mDashboard;

    public DBItemHolder<Dashboard> getDbItem() {
        return mDbItem;
    }

    public void setDbItem(DBItemHolder<Dashboard> dbItem) {
        mDbItem = dbItem;
    }

    public Dashboard getDashboard() {
        return mDashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        mDashboard = dashboard;
    }
}
