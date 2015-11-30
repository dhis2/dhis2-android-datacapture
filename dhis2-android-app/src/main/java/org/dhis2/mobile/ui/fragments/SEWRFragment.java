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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.models.Coordinates;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.holders.ProgramInfoHolder;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.ui.activities.SEWRDataEntryActivity;
import org.dhis2.mobile.ui.pickers.BaseItemPicker;
import org.dhis2.mobile.ui.pickers.CoordinatesPicker;
import org.dhis2.mobile.ui.pickers.CoordinatesPicker.OnProgressListener;
import org.dhis2.mobile.ui.pickers.DatePicker;
import org.dhis2.mobile.ui.pickers.DatePickerDialog.OnDateSetListener;
import org.dhis2.mobile.ui.pickers.ItemPicker;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.PrefUtils.Resources;
import org.dhis2.mobile.utils.PrefUtils.State;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.utils.ToastManager;
import org.joda.time.LocalDate;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.dhis2.mobile.utils.ViewUtils.enableViews;
import static org.dhis2.mobile.utils.ViewUtils.hideAndDisableViews;
import static org.dhis2.mobile.utils.ViewUtils.perfomInAnimation;
import static org.dhis2.mobile.utils.ViewUtils.perfomOutAnimation;

public class SEWRFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<OrganizationUnit>> {
    public static final String TAG = "org.dhis2.mobile.ui.fragments.SEWRFragment";
    private static final int SINGLE_EVENT_FRAGMENT_ID = TAG.length();
    private static final String IS_REFRESHING = "isRefreshing";

    private ItemPicker orgUnitPicker;
    private ItemPicker programPicker;
    private DatePicker datePicker;

    private View programInfo;
    private CoordinatesPicker coordsPicker;

    private View userEntryContainer;
    private View contentView;
    private View stubFragmentView;
    private View pickersContainer;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView choosenProgram;
    private TextView choosenOrgUnit;
    private TextView programDescription;

    private ProgramInfoHolder info;
    private boolean isRefreshing;

    private OrganizationUnit savedUnit;
    private Form savedForm;
    private String savedEventDate;
    private Coordinates savedCoords;

    /* This class is responsible for async. data loading from storage */
    private static class DataLoader extends AsyncTaskLoader<ArrayList<OrganizationUnit>> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public ArrayList<OrganizationUnit> loadInBackground() {
            ArrayList<OrganizationUnit> units = null;
            TextFileUtils.Directory dir = TextFileUtils.Directory.ROOT;
            TextFileUtils.FileNames name = TextFileUtils.FileNames.ORG_UNITS_WITH_SEWR;
            if (TextFileUtils.doesFileExist(getContext(), dir, name)) {
                String unitsStr = TextFileUtils.readTextFile(getContext(), dir, name);

                JsonReader reader = new JsonReader(new StringReader(unitsStr));
                reader.setLenient(true);
                Type type = new TypeToken<ArrayList<OrganizationUnit>>() {}.getType();

                Gson gson = new Gson();
                units = gson.fromJson(reader, type);
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
        View root = inflater.inflate(R.layout.single_event_fragment_layout, container, false);

        contentView = root.findViewById(R.id.content);
        pickersContainer = root.findViewById(R.id.pickers_container);
        stubFragmentView = root.findViewById(R.id.pull_to_refresh_stub_screen);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);

        hideAndDisableViews(pickersContainer, stubFragmentView);
        SwipeRefreshLayout.OnRefreshListener onPullListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    startUpdate();
                }
            }
        };

        int blue = R.color.actionbar_blue;
        int grey = R.color.light_grey;

        swipeRefreshLayout.setOnRefreshListener(onPullListener);
        swipeRefreshLayout.setColorScheme(blue, grey, blue, grey);

        programInfo = root.findViewById(R.id.program_info);

        userEntryContainer = root.findViewById(R.id.user_data_entry);
        choosenOrgUnit = (TextView) root.findViewById(R.id.choosen_unit);
        choosenProgram = (TextView) root.findViewById(R.id.choosen_form);
        programDescription = (TextView) root.findViewById(R.id.form_description);

        info = new ProgramInfoHolder();

        OnProgressListener listener = new OnProgressListener() {

            @Override
            public void onProgressChanged(Coordinates coords) {
                onCoordinatesSelected(coords);
            }
        };

        orgUnitPicker = new ItemPicker(getActivity(), root, R.id.org_unit_dialog_invoker,
                R.string.organization_unit, R.string.choose_unit);
        programPicker = new ItemPicker(getActivity(), root, R.id.program_dialog_invoker,
                R.string.program, R.string.choose_program);
        datePicker = new DatePicker(getActivity(), programInfo, R.id.period_picker);
        coordsPicker = new CoordinatesPicker(programInfo, listener);

        userEntryContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                startDataentryActivity();
            }
        });

        // restore values from bundle
        if (savedInstanceState != null) {
            Bundle valuesBundle = savedInstanceState.getBundle(TAG);
            if (valuesBundle != null) {
                savedUnit = valuesBundle.getParcelable(OrganizationUnit.TAG);
                savedForm = valuesBundle.getParcelable(Form.TAG);
                savedEventDate = valuesBundle.getString(Constants.EVENT_DATE);
                savedCoords = valuesBundle.getParcelable(Coordinates.TAG);
                isRefreshing = valuesBundle.getBoolean(IS_REFRESHING, false);
            }
        }

        State programState = PrefUtils.getResourceState(getActivity(), Resources.SINGLE_EVENTS_WITHOUT_REGISTRATION);

        if (!isRefreshing) {
            isRefreshing = programState == State.REFRESHING;
        }

        if (!isRefreshing) {
            boolean needsUpdate = programState == State.OUT_OF_DATE;
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
    public void onSaveInstanceState(Bundle outState) {
        Bundle savedState = new Bundle();
        savedState.putParcelable(OrganizationUnit.TAG, orgUnitPicker.getSavedSelection());
        savedState.putParcelable(Form.TAG, programPicker.getSavedSelection());
        savedState.putString(Constants.EVENT_DATE, datePicker.getSavedSelection());
        savedState.putParcelable(Coordinates.TAG, coordsPicker.getSavedSelection());
        savedState.putBoolean(IS_REFRESHING, isRefreshing);
        outState.putBundle(TAG, savedState);
        super.onSaveInstanceState(outState);
    }

    protected void restorePreviousState() {
        if (savedUnit != null) {
            onUnitSelected(savedUnit);

            if (savedForm != null) {
                onProgramSelected(savedForm);

                if (savedEventDate != null) {
                    onDateSelected(savedEventDate, savedForm);

                    if (savedCoords != null) {
                        coordsPicker.restoreSelection(savedCoords);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        dismissAllPickers(orgUnitPicker, programPicker);
        if (datePicker != null && datePicker.isShowing()) {
            datePicker.dismiss();
        }

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
    public Loader<ArrayList<OrganizationUnit>> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<OrganizationUnit>> loader, ArrayList<OrganizationUnit> units) {
        onLoadFinished(units);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<OrganizationUnit>> loader) { }

    private void startUpdate() {
        Log.i("SEWRFragment", "Starting update of programs");
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
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_SINGLE_EVENTS_WITHOUT_REG);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            hideProgressInActionBar();
        }
    }

    private void loadData() {
        showProgressBar();
        setEnabledSwipeRefreshLayout(false);

        getLoaderManager().restartLoader(SINGLE_EVENT_FRAGMENT_ID, null, this).forceLoad();
    }

    private void onLoadFinished(ArrayList<OrganizationUnit> units) {
        hideProgressBar();
        setEnabledSwipeRefreshLayout(true);

        // if there is not any forms available, show stub screen
        if (units == null) {
            enableViews(stubFragmentView);
            hideAndDisableViews(pickersContainer);
            String message = getString(R.string.no_forms_found);
            ToastManager.makeToast(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            perfomInAnimation(getActivity(), R.anim.fade_in, pickersContainer);
            hideAndDisableViews(stubFragmentView, userEntryContainer);

            // pass data to spinner
            handleUnits(units);
            // restore user choices if available
            restorePreviousState();
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
        orgUnitPicker.saveSelection(unit);
        orgUnitPicker.dismiss();

        info.setOrgUnitLabel(unit.getLabel());
        info.setOrgUnitId(unit.getId());

        handlePrograms(unit.getForms());
        disableViews(0);
    }

    protected void handlePrograms(final ArrayList<Form> datasets) {
        ArrayList<String> labels = new ArrayList<String>();
        for (Form form : datasets) {
            labels.add(form.getLabel());
        }

        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onProgramSelected(datasets.get(pos));
            }
        };

        programPicker.enable();
        programPicker.updateContent(labels);
        programPicker.setOnItemClickListener(listener);
    }

    protected void onProgramSelected(Form form) {
        programPicker.setText(form.getLabel());
        programPicker.saveSelection(form);
        programPicker.dismiss();

        info.setFormLabel(form.getLabel());
        info.setFormId(form.getId());
        info.setProgramDescription(form.getOptions().getDescription());

        handleEventDate(form);
        disableViews(1);
    }

    protected void handleEventDate(final Form form) {
        String dateDescription = form.getOptions().getDateOfIncidentDescription();

        OnDateSetListener listener = new OnDateSetListener() {

            @Override
            public void onDateSet(LocalDate date) {
                onDateSelected(date.toString(Constants.DATE_FORMAT), form);
            }
        };

        datePicker.enable();
        datePicker.setText(dateDescription);
        datePicker.setDialogTitle(dateDescription);
        datePicker.setOnDateSetListener(listener);
    }

    protected void onDateSelected(String date, Form form) {
        datePicker.setText(date);
        datePicker.saveSelection(date);

        info.setEventDate(date);

        String capCoords = form.getOptions().getCaptureCoordinates();
        boolean captureCoords = capCoords != null ? capCoords.equals(Field.TRUE) : false;
        if (captureCoords) {
            if (!coordsPicker.isShown()) {
                coordsPicker.show();
            }
            hideUserEntry();
        } else {
            handleDataEntryInfo();
            showUserEntry();
        }
    }

    protected void onCoordinatesSelected(Coordinates coords) {
        if (coords != null && coords.hasCoordValues()) {
            info.setCoordinates(coords);
            handleDataEntryInfo();
            showUserEntry();
        } else {
            hideUserEntry();
        }
    }

    protected void handleDataEntryInfo() {
        if (info.getFormLabel() != null && info.getOrgUnitLabel() != null) {
            String orgUnitInfo = String.format("%s: %s",
                    getString(R.string.organization_unit), info.getOrgUnitLabel());
            String programInfo = String.format("%s: %s",
                    getString(R.string.description), info.getProgramDescription());

            choosenProgram.setText(info.getFormLabel());
            choosenOrgUnit.setText(orgUnitInfo);
            programDescription.setText(programInfo);
        }
    }

    protected void disableViews(int level) {
        switch (level) {
            case 0:
                datePicker.disable();
            case 1:
                coordsPicker.hide();
            default:
                hideUserEntry();
        }
    }

    protected static void dismissAllPickers(BaseItemPicker... pickers) {
        for (BaseItemPicker picker : pickers) {
            if (picker != null && picker.isShowing()) {
                picker.dismiss();
            }
        }
    }

    protected void showUserEntry() {
        if (!userEntryContainer.isShown()) {
            perfomInAnimation(getActivity(), R.anim.in_left, userEntryContainer);
        }
    }

    protected void hideUserEntry() {
        if (userEntryContainer.isShown()) {
            perfomOutAnimation(getActivity(), R.anim.out_right, false, userEntryContainer);
        }
    }

    protected void startDataentryActivity() {
        if (info != null && getActivity() != null) {
            Intent intent = new Intent(getActivity(), SEWRDataEntryActivity.class);
            intent.putExtra(ProgramInfoHolder.TAG, info);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_up, R.anim.activity_open_exit);
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

    private void setEnabledSwipeRefreshLayout(boolean flag) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(flag);
        }
    }

    private void resetValues() {
        if (orgUnitPicker != null) {
            orgUnitPicker.disable();
        }

        if (programPicker != null) {
            programPicker.disable();
        }

        if (datePicker != null) {
            datePicker.disable();
        }

        if (coordsPicker != null) {
            coordsPicker.hide();
        }

        savedUnit = null;
        savedForm = null;
        savedEventDate = null;
        savedCoords = null;

        info = new ProgramInfoHolder();
    }
}