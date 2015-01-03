package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;

import java.util.List;

public class DashboardViewPagerAdapter extends FragmentPagerAdapter {
    private static final String EMPTY_TITLE = "";
    private List<DBItemHolder<Dashboard>> mDashboards;

    public DashboardViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return new DashboardFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mDashboards != null) {
            return mDashboards.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mDashboards != null && mDashboards.size() > 0) {
            return mDashboards.get(position).getItem().getName();
        } else {
            return EMPTY_TITLE;
        }
    }

    public void setData(List<DBItemHolder<Dashboard>> dashboards) {
        boolean hasToNotifyAdapter = mDashboards != dashboards;
        mDashboards = dashboards;

        if (hasToNotifyAdapter) {
            notifyDataSetChanged();
        }
    }
}
