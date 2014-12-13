package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;

public class AggregateReportFragment extends AbsRefreshableScrollViewFragment {

    @Override
    public void onStart() {
        super.onStart();
        setTitleToActionBar(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        setTitleToActionBar(false);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aggregate_report_layout, container, false);
    }

    @Override
    public View onCreateMessageView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_message, container, false);
    }

    @Override
    public void onRefresh() {
        showMessageView(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
            }
        }, 3000);
    }

    private void setTitleToActionBar(boolean flag) {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (flag) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.aggregate_report);
        } else {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(null);
        }
    }
}
