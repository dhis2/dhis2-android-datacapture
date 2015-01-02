package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

public class DashboardFragment extends BaseFragment {
    public static final String TITLE = "dashboardTitle";
    private TextView mLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLabel = (TextView) view.findViewById(R.id.dashboard_title);
        if (getArguments() != null && getArguments().getString(TITLE) != null) {
            mLabel.setText(getArguments().getString(TITLE));
        }
    }
}
