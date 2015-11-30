/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.FormOptions;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.ui.activities.AggregateReportDataEntryActivity;
import org.dhis2.mobile.ui.pickers.BaseItemPicker;
import org.dhis2.mobile.ui.pickers.ItemPicker;
import org.dhis2.mobile.ui.pickers.PeriodPicker;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.PrefUtils.Resources;
import org.dhis2.mobile.utils.PrefUtils.State;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.TextFileUtils.Directory;
import org.dhis2.mobile.utils.TextFileUtils.FileNames;
import org.dhis2.mobile.utils.ToastManager;
import org.dhis2.mobile.utils.date.DateHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.dhis2.mobile.utils.ViewUtils.enableViews;
import static org.dhis2.mobile.utils.ViewUtils.hideAndDisableViews;
import static org.dhis2.mobile.utils.ViewUtils.perfomInAnimation;
import static org.dhis2.mobile.utils.ViewUtils.perfomOutAnimation;

public class AggregateReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<OrganizationUnit>> {
    public static final String TAG = "org.dhis2.mobile.ui.fragments.aggregateReportFragment.aggregateReportFragment";
    private static final int AGGREGATE_REPORT_LOADER_ID = TAG.length();
    private static final String IS_REFRESHING = "isRefreshing";

    private View pickersContainer;
    private View contentView;
    private View stubFragmentLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing;

    private View userEntryContainer;
    private TextView choosenDataset;
    private TextView choosenUnit;
    private TextView choosenPeriod;

    private PeriodPicker periodPicker;
    private ItemPicker orgUnitPicker;
    private ItemPicker datasetPicker;

    private DatasetInfoHolder info;

    private OrganizationUnit savedUnit;
    private Form savedForm;
    private DateHolder savedPeriod;

    /* This class is responsible for async. data loading from storage */
    private static class DataLoader extends AsyncTaskLoader<ArrayList<OrganizationUnit>> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public ArrayList<OrganizationUnit> loadInBackground() {
            String jSourceUnits;
            if (TextFileUtils.doesFileExist(getContext(),
                    Directory.ROOT, FileNames.ORG_UNITS_WITH_DATASETS)) {
                jSourceUnits = TextFileUtils.readTextFile(getContext(),
                        Directory.ROOT, FileNames.ORG_UNITS_WITH_DATASETS);
            } else {
                return null;
            }

            if (jSourceUnits == null) {
                return null;
            }

            ArrayList<OrganizationUnit> units = null;
            try {
                JsonArray jUnits = JsonHandler.buildJsonArray(jSourceUnits);
                Type type = new TypeToken<ArrayList<OrganizationUnit>>() {
                }.getType();
                units = JsonHandler.fromJson(jUnits, type);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            return units;
        }
    }

    /* this BroadcastReceiver waits for response with updates from service */
    private BroadcastReceiver onFormsUpdateListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cxt, Intent intent) {
            hideProgressInActionBar();

            int networkStatusCode = intent.getExtras().getInt(Response.CODE);
            int parsingStatusCode = intent.getExtras().getInt(JsonHandler.PARSING_STATUS_CODE);

            Context context = getActivity();
            if (HTTPClient.isError(networkStatusCode)) {
                String message = HTTPClient.getErrorMessage(context, networkStatusCode);
                ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            }

            if (parsingStatusCode != JsonHandler.PARSING_OK_CODE) {
                String message = getString(R.string.bad_response);
                ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            }
            loadData();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.aggregate_report_fragment_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

        pickersContainer = root.findViewById(R.id.pickers_container);
        contentView = root.findViewById(R.id.content);
        stubFragmentLayout = root.findViewById(R.id.pull_to_refresh_stub_screen);
        userEntryContainer = root.findViewById(R.id.user_data_entry);

        // TextViews which will show user choice in userEntryContainer
        choosenDataset = (TextView) userEntryContainer.findViewById(R.id.choosen_form);
        choosenUnit = (TextView) userEntryContainer.findViewById(R.id.choosen_unit);
        choosenPeriod = (TextView) userEntryContainer.findViewById(R.id.form_description);

        //ViewUtils.hideAndDisableViews(pickersContainer, stubFragmentLayout);
        //ViewUtils.hideAndDisableViews(contentView);
        hideAndDisableViews(contentView);

        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    startUpdate();
                }
            }
        };

        int blue = R.color.actionbar_blue;
        int grey = R.color.light_grey;

        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setColorScheme(blue, grey, blue, grey);

        orgUnitPicker = new ItemPicker(getActivity(), root, R.id.org_unit_dialog_invoker,
                R.string.organization_unit, R.string.choose_unit);
        datasetPicker = new ItemPicker(getActivity(), root, R.id.data_set_dialog_invoker,
                R.string.dataset, R.string.choose_data_set);
        periodPicker = new PeriodPicker(getActivity(), root);

        userEntryContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                startDataentryActivity();
            }
        });

        // Creating holder
        info = new DatasetInfoHolder();

        // restore values from bundle
        if (savedInstanceState != null) {
            Bundle valuesBundle = savedInstanceState.getBundle(TAG);
            if (valuesBundle != null) {
                savedUnit = valuesBundle.getParcelable(OrganizationUnit.TAG);
                savedForm = valuesBundle.getParcelable(Form.TAG);
                savedPeriod = valuesBundle.getParcelable(DateHolder.TAG);
                isRefreshing = valuesBundle.getBoolean(IS_REFRESHING, false);
            }
        }

        State datasetState = PrefUtils.getResourceState(getActivity(), Resources.DATASETS);
        if (!isRefreshing) {
            isRefreshing = datasetState == State.REFRESHING;
        }

        if (!isRefreshing) {
            boolean needsUpdate = datasetState == State.OUT_OF_DATE;
            boolean isConnectionAvailable = NetworkUtils.checkConnection(getActivity());

            if (needsUpdate && isConnectionAvailable) {
                startUpdate();
            } else {
                loadData();
            }
        } else {
            showProgressInActionBar();
        }

        return root;
    }

    @Override
    public void onPause() {
        dismissAllPickers(orgUnitPicker, datasetPicker, periodPicker);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onFormsUpdateListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(onFormsUpdateListener, new IntentFilter(TAG));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle savedState = new Bundle();
        savedState.putParcelable(OrganizationUnit.TAG, orgUnitPicker.getSavedSelection());
        savedState.putParcelable(Form.TAG, datasetPicker.getSavedSelection());
        savedState.putParcelable(DateHolder.TAG, periodPicker.getSavedSelection());
        savedState.putBoolean(IS_REFRESHING, isRefreshing);
        outState.putBundle(TAG, savedState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<ArrayList<OrganizationUnit>> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<OrganizationUnit>> loader, ArrayList<OrganizationUnit> units) {
        onLoadFinished(units);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<OrganizationUnit>> loader) {
    }

    protected void restorePreviousState() {
        if (savedUnit != null) {
            onUnitSelected(savedUnit);

            if (savedForm != null) {
                onDatasetSelected(savedForm);

                if (savedPeriod != null) {
                    onDateSelected(savedPeriod);
                }
            }
        }
    }

    private void loadData() {
        showProgressBar();
        setEnabledSwipeRefreshLayout(false);
        getLoaderManager().restartLoader(AGGREGATE_REPORT_LOADER_ID, null, this).forceLoad();
    }

    private void onLoadFinished(ArrayList<OrganizationUnit> units) {
        hideProgressBar();
        setEnabledSwipeRefreshLayout(true);

        // if there is no any forms available, show stub screen
        if (units == null) {
            enableViews(stubFragmentLayout);
            hideAndDisableViews(pickersContainer);
            String message = getString(R.string.no_forms_found);
            ToastManager.makeToast(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            perfomInAnimation(getActivity(), R.anim.fade_in, pickersContainer);
            hideAndDisableViews(stubFragmentLayout, userEntryContainer);

            // pass data to spinner
            handleUnits(units);
            // restore user choices if available
            restorePreviousState();
        }
    }

    private void startUpdate() {
        Log.i("startUpdate()", "Starting update of dataSets");
        Context context = getActivity();
        if (context == null) {
            return;
        }

        boolean isConnectionAvailable = NetworkUtils.checkConnection(context);
        if (isConnectionAvailable) {
            showProgressInActionBar();
            // Hide dataentry button from screen
            perfomOutAnimation(getActivity(), R.anim.out_right, true, userEntryContainer);
            // Reset to default all previous user choices
            resetValues();

            // Prepare Intent and start service
            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_DATASETS);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            hideProgressInActionBar();
        }
    }

    protected void handleUnits(final ArrayList<OrganizationUnit> units) {
        ArrayList<String> labels = new ArrayList<String>();
        for (OrganizationUnit unit : units) {
            labels.add(unit.getLabel());
        }

        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onUnitSelected(units.get(pos));
            }
        };

        orgUnitPicker.enable();
        orgUnitPicker.updateContent(labels);
        orgUnitPicker.setOnItemClickListener(listener);
    }

    protected void onUnitSelected(OrganizationUnit unit) {
        orgUnitPicker.setText(unit.getLabel());
        orgUnitPicker.dismiss();
        orgUnitPicker.saveSelection(unit);

        info.setOrgUnitLabel(unit.getLabel());
        info.setOrgUnitId(unit.getId());

        handleDatasets(unit.getForms());
        disableViews(0);
    }

    protected void handleDatasets(final ArrayList<Form> datasets) {
        ArrayList<String> labels = new ArrayList<String>();
        for (Form form : datasets) {
            labels.add(form.getLabel());
        }

        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onDatasetSelected(datasets.get(pos));
            }
        };

        datasetPicker.enable();
        datasetPicker.updateContent(labels);
        datasetPicker.setOnItemClickListener(listener);
    }

    protected void onDatasetSelected(Form form) {
        datasetPicker.setText(form.getLabel());
        datasetPicker.saveSelection(form);
        datasetPicker.dismiss();

        info.setFormLabel(form.getLabel());
        info.setFormId(form.getId());

        handleDates(form.getOptions());
        disableViews(1);
    }

    protected void handleDates(FormOptions options) {
        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                onDateSelected(periodPicker.getDates().get(pos));
            }
        };

        System.out.println("PERIOD_TYPE: " + options.getPeriodType());
        System.out.println("ALLOW_FUTURE_PERIOD: " + options.getOpenFuturePeriods());

        periodPicker.enable();
        periodPicker.setOnItemClickListener(listener);
        periodPicker.setPeriodType(options.getPeriodType(), options.getOpenFuturePeriods());
    }

    protected void onDateSelected(DateHolder date) {
        periodPicker.setText(date.getLabel());
        periodPicker.saveSelection(date);
        periodPicker.dismiss();

        info.setPeriodLabel(date.getLabel());
        info.setPeriod(date.getDate());

        handleDataEntryInfo();
        if (!userEntryContainer.isShown()) {
            perfomInAnimation(getActivity(), R.anim.in_left, userEntryContainer);
        }
    }

    protected void handleDataEntryInfo() {
        if (info.getFormLabel() != null && info.getFormLabel() != null
                && info.getPeriodLabel() != null) {
            String unit = String.format("%s: %s", getString(R.string.organization_unit), info.getOrgUnitLabel());
            String period = String.format("%s: %s", getString(R.string.period), info.getPeriodLabel());

            choosenDataset.setText(info.getFormLabel());
            choosenUnit.setText(unit);
            choosenPeriod.setText(period);
        }
    }

    protected void startDataentryActivity() {
        if (info != null && getActivity() != null) {
            Intent intent = new Intent(getActivity(), AggregateReportDataEntryActivity.class);
            intent.putExtra(DatasetInfoHolder.TAG, info);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_up, R.anim.activity_open_exit);
        }
    }

    protected void disableViews(int level) {
        switch (level) {
            case 0:
                periodPicker.disable();
            case 1:
                if (userEntryContainer.isShown()) {
                    perfomOutAnimation(getActivity(), R.anim.out_right, true, userEntryContainer);
                }
        }
    }

    protected static void dismissAllPickers(BaseItemPicker... pickers) {
        for (BaseItemPicker picker : pickers) {
            if (picker != null && picker.isShowing()) {
                picker.dismiss();
            }
        }
    }

    private void showProgressInActionBar() {
        if (swipeRefreshLayout != null) {
            isRefreshing = true;

            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    private void hideProgressInActionBar() {
        if (swipeRefreshLayout != null) {
            isRefreshing = false;

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void showProgressBar() {
        hideAndDisableViews(contentView);
        enableViews(progressBar);
    }

    private void hideProgressBar() {
        hideAndDisableViews(progressBar);
        enableViews(contentView);
    }

    private void setEnabledSwipeRefreshLayout(boolean flag) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(flag);
        }
    }

    private void resetValues() {
        if (orgUnitPicker != null) {
            orgUnitPicker.disable();
        }

        if (datasetPicker != null) {
            datasetPicker.disable();
        }

        if (periodPicker != null) {
            periodPicker.disable();
        }

        savedUnit = null;
        savedForm = null;
        savedPeriod = null;

        info = new DatasetInfoHolder();
    }
}