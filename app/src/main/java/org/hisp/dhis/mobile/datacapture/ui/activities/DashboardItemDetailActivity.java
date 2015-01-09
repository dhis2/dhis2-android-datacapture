package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItemElement;
import org.hisp.dhis.mobile.datacapture.api.models.User;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ImageViewFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.ListViewFragment;
import org.hisp.dhis.mobile.datacapture.ui.fragments.WebViewFragment;

import java.util.List;

public class DashboardItemDetailActivity extends ActionBarActivity {

    public static Intent prepareIntent(Context context, DashboardItem dashboardItem) {
        // TODO Create intent, put dashboard item into extras of intent.
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_item_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDashboardItem(DashboardItem dashboardItem) {
        Fragment fragment = null;
        Bundle params = new Bundle();
        String serverUrl = DHISManager.getInstance().getServerUrl();
        if (DashboardItem.TYPE_MAP.equals(dashboardItem.getType()) && dashboardItem.getMap() != null) {
            setTitle(dashboardItem.getMap().getName());
            String request = serverUrl + "/api/maps/" + dashboardItem.getMap().getId() + "/data.png";
            params.putString(ImageViewFragment.IMAGE_URL_EXTRA, request);
            fragment = new ImageViewFragment();
            // mCanShareInterpretation = true;
        } else if (DashboardItem.TYPE_CHART.equals(dashboardItem.getType()) && dashboardItem.getChart() != null) {
            setTitle(dashboardItem.getChart().getName());
            String request = serverUrl + "/api/charts/" + dashboardItem.getChart().getId() + "/data.png";
            params.putString(ImageViewFragment.IMAGE_URL_EXTRA, request);
            fragment = new ImageViewFragment();
            // mCanShareInterpretation = true;
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(dashboardItem.getType()) && dashboardItem.getEventChart() != null) {
            setTitle(dashboardItem.getEventChart().getName());
            String request = serverUrl + "/api/eventCharts/" + dashboardItem.getEventChart().getId() + "/data.png";
            params.putString(ImageViewFragment.IMAGE_URL_EXTRA, request);
            fragment = new ImageViewFragment();
            // mCanShareInterpretation = false;
        } else if (DashboardItem.TYPE_REPORT_TABLE.equals(dashboardItem.getType()) && dashboardItem.getReportTable() != null) {
            setTitle(dashboardItem.getReportTable().getName());
            String request = serverUrl + "/api/reportTables/" + dashboardItem.getReportTable().getId() + "/data.html";
            params.putString(WebViewFragment.WEB_URL_EXTRA, request);
            fragment = new WebViewFragment();
            // mCanShareInterpretation = true;
        } else if (DashboardItem.TYPE_USERS.equals(dashboardItem.getType()) && dashboardItem.getUsers() != null) {
            setTitle(getString(R.string.users));
            List<User> users = dashboardItem.getUsers();
            String[] userNames = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                userNames[i] = users.get(i).getName();
            }
            params.putStringArray(ListViewFragment.STRING_ARRAY_EXTRA, userNames);
            fragment = new ListViewFragment();
            // mCanShareInterpretation = false;
        } else if (DashboardItem.TYPE_RESOURCES.equals(dashboardItem.getType()) && dashboardItem.getResources() != null) {
            setTitle(getString(R.string.resources));
            List<DashboardItemElement> resources = dashboardItem.getResources();
            String[] resourcesLabels = new String[resources.size()];
            for (int i = 0; i < resources.size(); i++) {
                resourcesLabels[i] = resources.get(i).getName();
            }
            params.putStringArray(ListViewFragment.STRING_ARRAY_EXTRA, resourcesLabels);
            fragment = new ListViewFragment();
            // mCanShareInterpretation = false;
        } else if (DashboardItem.TYPE_REPORTS.equals(dashboardItem.getType()) && dashboardItem.getReports() != null) {
            setTitle(getString(R.string.reports));
            List<DashboardItemElement> reports = dashboardItem.getReports();
            String[] reportLabels = new String[reports.size()];
            for (int i = 0; i < reports.size(); i++) {
                reportLabels[i] = reports.get(i).getName();
            }
            params.putStringArray(ListViewFragment.STRING_ARRAY_EXTRA, reportLabels);
            fragment = new ListViewFragment();
            // mCanShareInterpretation = false;
        }

        if (fragment != null) {
            fragment.setArguments(params);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dashboard_item_detail_frame, fragment).commitAllowingStateLoss();
        }
    }
}
