package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;

public class DashboardFragment extends BaseFragment {
    private static final String DASHBOARD_ID = "";

    public static DashboardFragment newInstance(long dashboardId) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();

        args.putLong(DashboardColumns.DB_ID, dashboardId);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
