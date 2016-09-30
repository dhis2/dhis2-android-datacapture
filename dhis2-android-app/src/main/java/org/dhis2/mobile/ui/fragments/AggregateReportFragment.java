package org.dhis2.mobile.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Category;
import org.dhis2.mobile.io.models.CategoryOption;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.FormOptions;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.ui.adapters.PickerAdapter;
import org.dhis2.mobile.ui.adapters.PickerAdapter.OnPickerListChangeListener;
import org.dhis2.mobile.ui.models.Filter;
import org.dhis2.mobile.ui.models.Picker;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.ToastManager;
import org.dhis2.mobile.utils.date.DateHolder;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.dhis2.mobile.utils.ViewUtils.perfomInAnimation;
import static org.dhis2.mobile.utils.ViewUtils.perfomOutAnimation;

public class AggregateReportFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Picker> {
    public static final String TAG = AggregateReportFragment.class.getName();
    public static final int AGGREGATE_REPORT_LOADER_ID = TAG.length();

    // index of pickers in list
    private static final int ORG_UNIT_PICKER_ID = 0;
    private static final int DATASET_PICKER_ID = 1;

    // state keys
    private static final String STATE_PICKERS_ONE = "state:pickersOne";
    private static final String STATE_PICKERS_TWO = "state:pickersTwo";
    private static final String STATE_PICKERS_PERIOD = "state:pickersPeriod";
    private static final String STATE_IS_REFRESHING = "state:isRefreshing";

    // generic picker adapters
    private PickerAdapter pickerAdapterOne;
    private PickerAdapter pickerAdapterTwo;

    // period picker views
    private LinearLayout periodPickerLinearLayout;
    private TextView periodPickerTextView;

    // data entry button
    private LinearLayout dataEntryButton;
    private TextView formTextView;
    private TextView formDescriptionTextView;
    private TextView organisationUnitTextView;

    // swipe refresh layout
    private SwipeRefreshLayout swipeRefreshLayout;
    private View stubLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aggregate_report, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        setupStubLayout(root);
        setupDataEntryButton(root);
        setupPickerRecyclerViews(root, savedInstanceState);
        setupSwipeRefreshLayout(root, savedInstanceState);

        if (savedInstanceState == null) {
            loadData();
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(onFormsUpdateListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(onFormsUpdateListener, new IntentFilter(TAG));
    }

    @Override
    public Loader<Picker> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Picker> loader, Picker data) {
        pickerAdapterOne.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<Picker> loader) {
        // stub implementation
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (pickerAdapterOne != null) {
            pickerAdapterOne.onSaveInstanceState(STATE_PICKERS_ONE, outState);
        }

        if (pickerAdapterTwo != null) {
            pickerAdapterTwo.onSaveInstanceState(STATE_PICKERS_TWO, outState);
        }

        if (periodPickerLinearLayout != null && periodPickerLinearLayout.getTag() != null) {
            outState.putParcelable(STATE_PICKERS_PERIOD,
                    (DateHolder) periodPickerLinearLayout.getTag());
        }

        if (swipeRefreshLayout != null) {
            outState.putBoolean(STATE_IS_REFRESHING, swipeRefreshLayout.isRefreshing());
        }

        super.onSaveInstanceState(outState);
    }

    private void loadData() {
        getLoaderManager().restartLoader(AGGREGATE_REPORT_LOADER_ID, null, this).forceLoad();
    }

    private void setupStubLayout(View view) {
        stubLayout = view.findViewById(R.id.pull_to_refresh_stub_screen);
        stubLayout.setVisibility(View.GONE);
    }

    private void setupDataEntryButton(View root) {
        dataEntryButton = (LinearLayout) root.findViewById(R.id.user_data_entry);
        formTextView = (TextView) root.findViewById(R.id.choosen_form);
        formDescriptionTextView = (TextView) root.findViewById(R.id.form_description);
        organisationUnitTextView = (TextView) root.findViewById(R.id.choosen_unit);

        dataEntryButton.setVisibility(View.GONE);
    }

    private void setupPickerRecyclerViews(View root, Bundle savedInstanceState) {
        // setting up period picker
        periodPickerLinearLayout = (LinearLayout) root.findViewById(R.id.linearlayout_picker);
        periodPickerTextView = (TextView) root.findViewById(R.id.textview_picker);

        periodPickerLinearLayout.setVisibility(View.GONE);
        periodPickerLinearLayout.setTag(null);

        ImageView periodPickerImageView = (ImageView) root.findViewById(R.id.imageview_cancel);
        periodPickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateSelected(null);
            }
        });

        // picker recycler views
        pickerAdapterOne = new PickerAdapter.Builder()
                .context(getActivity())
                .fragmentManager(getChildFragmentManager())
                .build();
        pickerAdapterTwo = new PickerAdapter.Builder()
                .context(getActivity())
                .fragmentManager(getChildFragmentManager())
                .renderPseudoRoots()
                .build();

        LinearLayoutManager layoutManagerOne = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManagerTwo = new LinearLayoutManager(getActivity());

        layoutManagerOne.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManagerTwo.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView pickerRecyclerViewOne = (RecyclerView) root
                .findViewById(R.id.recyclerview_pickers_one);
        RecyclerView pickerRecyclerViewTwo = (RecyclerView) root
                .findViewById(R.id.recyclerview_pickers_two);

        pickerRecyclerViewTwo.setLayoutManager(layoutManagerTwo);
        pickerRecyclerViewOne.setLayoutManager(layoutManagerOne);

        pickerRecyclerViewTwo.setAdapter(pickerAdapterTwo);
        pickerRecyclerViewOne.setAdapter(pickerAdapterOne);

        pickerAdapterOne.setOnPickerListChangeListener(new OnPickerListChangeListener() {
            @Override
            public void onPickerListChanged(List<Picker> pickers) {
                AggregateReportFragment.this.onPickerListChanged(pickers);
            }
        });

        pickerAdapterTwo.setOnPickerListChangeListener(new OnPickerListChangeListener() {
            @Override
            public void onPickerListChanged(List<Picker> pickers) {
                AggregateReportFragment.this.onPickerSelected();
            }
        });

        pickerAdapterOne.onRestoreInstanceState(STATE_PICKERS_ONE, savedInstanceState);
        pickerAdapterTwo.onRestoreInstanceState(STATE_PICKERS_TWO, savedInstanceState);

        // restoring state of period picker afterwards
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(STATE_PICKERS_PERIOD)) {
            DateHolder dateHolder = savedInstanceState.getParcelable(STATE_PICKERS_PERIOD);

            if (dateHolder != null) {
                periodPickerLinearLayout.setTag(dateHolder);
                periodPickerTextView.setText(dateHolder.getLabel());

                // we need to try to render data entry button
                onPickerSelected();
            }
        }
    }

    private void setupSwipeRefreshLayout(View root, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                startUpdate();
            }
        };

        @ColorInt
        int blue = R.color.actionbar_blue;

        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setColorSchemeColors(blue, blue);

        PrefUtils.State datasetState = PrefUtils.getResourceState(
                getActivity(), PrefUtils.Resources.DATASETS);

        boolean isRefreshing = false;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(STATE_IS_REFRESHING)) {
            isRefreshing = savedInstanceState.getBoolean(STATE_IS_REFRESHING, false);
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            isRefreshing = datasetState == PrefUtils.State.REFRESHING;
        }

        if (!isRefreshing) {
            boolean needsUpdate = datasetState == PrefUtils.State.OUT_OF_DATE;
            boolean isConnectionAvailable = NetworkUtils.checkConnection(getActivity());

            if (needsUpdate && isConnectionAvailable) {
                startUpdate();
            }
        } else {
            showProgressBar();
        }
    }

    private void onPickerListChanged(List<Picker> pickers) {
        if (pickers != null && !pickers.isEmpty()) {
            Picker lastPicker = pickers.get(pickers.size() - 1);
            Picker lastPickerChild = lastPicker.getSelectedChild();

            if (lastPickerChild != null && lastPickerChild.areChildrenPseudoRoots()) {
                // enable period picker as well
                if (lastPickerChild.getTag() != null &&
                        lastPickerChild.getTag() instanceof FormOptions) {
                    handlePeriodPicker((FormOptions) lastPickerChild.getTag());
                }

                // we need to disconnect pseudo roots from node
                pickerAdapterTwo.swapData(
                        lastPickerChild.buildUpon()
                                .parent(null)
                                .build());
            } else {
                // hide period picker
                periodPickerLinearLayout.setVisibility(View.GONE);
                periodPickerLinearLayout.setTag(null);
                periodPickerTextView.setText(null);

                // clear category pickers
                pickerAdapterTwo.swapData(null);
            }

            // hiding empty state message
            stubLayout.setVisibility(View.GONE);

            onPickerSelected();
        } else {
            // showing empty state message
            stubLayout.setVisibility(View.VISIBLE);
        }
    }

    private void handlePeriodPicker(final FormOptions options) {
        final String choosePeriodPrompt = getString(R.string.choose_period);
        periodPickerTextView.setText(choosePeriodPrompt);

        periodPickerLinearLayout.setVisibility(View.VISIBLE);
        periodPickerLinearLayout.setTag(null);

        periodPickerTextView.setText(choosePeriodPrompt);
        periodPickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeriodPicker periodPicker = PeriodPicker
                        .newInstance(
                                choosePeriodPrompt,
                                options.getPeriodType(),
                                options.getOpenFuturePeriods());
                periodPicker.setOnPeriodClickListener(new PeriodPicker.OnPeriodClickListener() {
                    @Override
                    public void onPeriodClicked(DateHolder dateHolder) {
                        onDateSelected(dateHolder);
                    }
                });
                periodPicker.show(getChildFragmentManager());
            }
        });
    }

    private void onDateSelected(DateHolder date) {
        String label = getString(R.string.choose_period);
        if (date != null) {
            label = date.getLabel();
        }

        periodPickerLinearLayout.setTag(date);
        periodPickerTextView.setText(label);

        onPickerSelected();
    }

    private void onPickerSelected() {
        DatasetInfoHolder datasetInfoHolder = new DatasetInfoHolder();

        // we need to traverse all views and pick up their states
        List<Picker> pickerListOne = pickerAdapterOne.getData();
        List<Picker> pickerListTwo = pickerAdapterTwo.getData();
        DateHolder pickerPeriodDateHolder = null;

        if (periodPickerLinearLayout.getTag() != null) {
            pickerPeriodDateHolder = (DateHolder) periodPickerLinearLayout.getTag();
        }

        // if we have everything in place, we can show data entry button
        if (pickerPeriodDateHolder == null) {
            handleDataEntryButton(null);
            return;
        }

        // set period to dataSetInfoHolder
        datasetInfoHolder.setPeriod(pickerPeriodDateHolder.getDate());
        datasetInfoHolder.setPeriodLabel(pickerPeriodDateHolder.getLabel());

        if (!areAllPrimaryPickersPresent(pickerListOne)) {
            handleDataEntryButton(null);
            return;
        }

        // set set organisation unit and data set ids
        Picker orgUnitPickerChild = pickerListOne.get(ORG_UNIT_PICKER_ID).getSelectedChild();
        datasetInfoHolder.setOrgUnitId(orgUnitPickerChild.getId());
        datasetInfoHolder.setOrgUnitLabel(orgUnitPickerChild.getName());

        Picker dataSetPickerChild = pickerListOne.get(DATASET_PICKER_ID).getSelectedChild();
        datasetInfoHolder.setFormId(dataSetPickerChild.getId());
        datasetInfoHolder.setFormLabel(dataSetPickerChild.getName());

        // traverse pseudo roots (categories) and set filter values to
        // category options
        List<Picker> categoryPickers = pickerAdapterTwo.getData();
        if (categoryPickers != null && !categoryPickers.isEmpty()) {
            for (Picker categoryPicker : categoryPickers) {
                if (categoryPicker.getChildren() == null || categoryPicker.getChildren().isEmpty()) {
                    continue;
                }

                for (Picker categoryOptionPicker : categoryPicker.getChildren()) {
                    List<Filter> categoryOptionFilters = categoryOptionPicker.getFilters();
                    if (categoryOptionFilters == null || categoryOptionFilters.isEmpty()) {
                        continue;
                    }

                    Log.d(TAG, categoryOptionPicker.getName());
                    for (Filter categoryOptionFilter : categoryOptionFilters) {
                        if (categoryOptionFilter instanceof OrganisationUnitsFilter) {
                            ((OrganisationUnitsFilter) categoryOptionFilter)
                                    .setOrganisationUnitId(orgUnitPickerChild.getId());
                        }

                        if (categoryOptionFilter instanceof PeriodFilter) {
                            DateTime selectedDate = null;

                            // parsing selected date
                            if (!TextUtils.isEmpty(pickerPeriodDateHolder.getDate())) {
                                selectedDate = DateTime.parse(pickerPeriodDateHolder.getDateTime());
                            }

                            ((PeriodFilter) categoryOptionFilter).setSelectedDate(selectedDate);
                        }
                    }
                }
            }
        }

        if (dataSetPickerChild.isLeaf()) {
            handleDataEntryButton(datasetInfoHolder);
            return;
        }

        if (dataSetPickerChild.areChildrenPseudoRoots()) {
            // we need to check if all pseudo roots have values
            if (!areAllSecondaryPickersPresent(pickerListTwo)) {
                handleDataEntryButton(null);
                return;
            }

            List<CategoryOption> categoryOptions = new ArrayList<>();

            // building list of selected category options
            for (Picker categoryPicker : pickerListTwo) {
                Picker categoryPickerChild = categoryPicker.getSelectedChild();
                categoryOptions.add((CategoryOption) categoryPickerChild.getTag());
            }

            datasetInfoHolder.setCategoryOptions(categoryOptions);
            handleDataEntryButton(datasetInfoHolder);
        }
    }

    private boolean areAllPrimaryPickersPresent(List<Picker> pickers) {
        return pickers != null && pickers.size() > 1 &&
                pickers.get(ORG_UNIT_PICKER_ID) != null &&
                pickers.get(ORG_UNIT_PICKER_ID).getSelectedChild() != null &&
                pickers.get(DATASET_PICKER_ID) != null &&
                pickers.get(DATASET_PICKER_ID).getSelectedChild() != null;
    }

    private boolean areAllSecondaryPickersPresent(List<Picker> pickers) {
        if (pickers == null) {
            return false;
        }

        for (Picker secondaryPicker : pickers) {
            if (secondaryPicker.getSelectedChild() == null) {
                return false;
            }
        }

        return true;
    }

    private void handleDataEntryButton(final DatasetInfoHolder info) {
        if (info != null) {
            String unit = String.format("%s: %s",
                    getString(R.string.organization_unit), info.getOrgUnitLabel());
            String period = String.format("%s: %s",
                    getString(R.string.period), info.getPeriodLabel());

            // setting labels
            formTextView.setText(info.getFormLabel());
            formDescriptionTextView.setText(period);
            organisationUnitTextView.setText(unit);

            dataEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataEntryActivity.navigateTo(getActivity(), info);
                }
            });

            // dataEntryButton.setVisibility(View.VISIBLE);
            if (!dataEntryButton.isShown()) {
                perfomInAnimation(getActivity(), R.anim.in_left, dataEntryButton);
            }
        } else {
            // reset all strings
            formTextView.setText("");
            formDescriptionTextView.setText("");
            organisationUnitTextView.setText("");

            // hide button
            // dataEntryButton.setVisibility(View.GONE);
            if (dataEntryButton.isShown()) {
                perfomOutAnimation(getActivity(), R.anim.out_right, true, dataEntryButton);
            }
        }
    }

    private void showProgressBar() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void hideProgressBar() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startUpdate() {
        Log.i("startUpdate()", "Starting update of dataSets");

        Context context = getActivity();
        if (context == null) {
            return;
        }

        boolean isConnectionAvailable = NetworkUtils.checkConnection(context);
        if (isConnectionAvailable) {
            showProgressBar();

            // Prepare Intent and start service
            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_DATASETS);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            hideProgressBar();
        }
    }

    /* this BroadcastReceiver waits for response with updates from service */
    private BroadcastReceiver onFormsUpdateListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressBar();

            int networkStatusCode = intent.getExtras().getInt(Response.CODE);
            int parsingStatusCode = intent.getExtras().getInt(JsonHandler.PARSING_STATUS_CODE);

            if (HTTPClient.isError(networkStatusCode)) {
                String message = HTTPClient.getErrorMessage(getActivity(), networkStatusCode);
                ToastManager.makeToast(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            if (parsingStatusCode != JsonHandler.PARSING_OK_CODE) {
                String message = getString(R.string.bad_response);
                ToastManager.makeToast(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            loadData();
        }
    };

    /* This class is responsible for async. data loading from storage */
    private static class DataLoader extends AsyncTaskLoader<Picker> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Picker loadInBackground() {
            String jSourceUnits;
            if (TextFileUtils.doesFileExist(getContext(), TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS)) {
                jSourceUnits = TextFileUtils.readTextFile(getContext(), TextFileUtils.Directory.ROOT,
                        TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
            } else {
                return null;
            }

            ArrayList<OrganizationUnit> units = null;
            try {
                JsonArray jUnits = JsonHandler.buildJsonArray(jSourceUnits);
                Type type = new TypeToken<ArrayList<OrganizationUnit>>() {
                    // capturing type
                }.getType();
                units = JsonHandler.fromJson(jUnits, type);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            String chooseOrganisationUnit = getContext().getString(R.string.choose_unit);
            String chooseDataSet = getContext().getString(R.string.choose_data_set);
            String choose = getContext().getString(R.string.choose);
            Picker rootNode = null;

            if (units != null && !units.isEmpty()) {
                rootNode = new Picker.Builder()
                        .hint(chooseOrganisationUnit)
                        .build();

                for (OrganizationUnit organisationUnit : units) {
                    Picker organisationUnitPicker = new Picker.Builder()
                            .id(organisationUnit.getId())
                            .name(organisationUnit.getLabel())
                            .hint(chooseDataSet)
                            .parent(rootNode)
                            .build();

                    if (organisationUnit.getForms() == null ||
                            organisationUnit.getForms().isEmpty()) {
                        continue;
                    }

                    // going through data set
                    for (Form dataSet : organisationUnit.getForms()) {
                        FormOptions formOptions = null;
                        if (dataSet.getOptions() != null) {
                            // we need to pull out options from dataset and set them as tag
                            formOptions = dataSet.getOptions();
                        }

                        Picker dataSetPicker = new Picker.Builder()
                                .id(dataSet.getId())
                                .name(dataSet.getLabel())
                                .parent(organisationUnitPicker)
                                .tag(formOptions)
                                .build();
                        organisationUnitPicker.addChild(dataSetPicker);

                        if (dataSet.getCategoryCombo() == null ||
                                dataSet.getCategoryCombo().getCategories() == null) {
                            continue;
                        }

                        for (Category category : dataSet.getCategoryCombo().getCategories()) {
                            String label = String.format(Locale.getDefault(), "%s %s",
                                    choose, category.getLabel());
                            Picker categoryPicker = new Picker.Builder()
                                    .id(category.getId())
                                    .hint(label)
                                    .parent(dataSetPicker)
                                    .asPseudoRoot()
                                    .build();
                            dataSetPicker.addChild(categoryPicker);

                            if (category.getCategoryOptions() == null ||
                                    category.getCategoryOptions().isEmpty()) {
                                continue;
                            }

                            for (CategoryOption option : category.getCategoryOptions()) {
                                Picker categoryOptionPicker = new Picker.Builder()
                                        .id(option.getId())
                                        .name(option.getLabel())
                                        .parent(categoryPicker)
                                        .tag(option)
                                        .build();

                                // building filters
                                OrganisationUnitsFilter organisationUnitsFilter =
                                        new OrganisationUnitsFilter(option.getOrganisationUnits());

                                // we need to parse dates which are located within option
                                DateTime startDate = null;
                                DateTime endDate = null;

                                if (!TextUtils.isEmpty(option.getStartDate())) {
                                    startDate = DateTime.parse(option.getStartDate());
                                }

                                if (!TextUtils.isEmpty(option.getEndDate())) {
                                    endDate = DateTime.parse(option.getEndDate());
                                }

                                PeriodFilter periodFilter = new PeriodFilter(startDate, endDate);

                                // adding filters which will be triggered in PickerItemAdapter
                                categoryOptionPicker.addFilter(organisationUnitsFilter);
                                categoryOptionPicker.addFilter(periodFilter);

                                categoryPicker.addChild(categoryOptionPicker);
                            }
                        }
                    }

                    rootNode.addChild(organisationUnitPicker);
                }
            }

            return rootNode;
        }
    }

    private static class OrganisationUnitsFilter implements Filter, Serializable {
        private final List<String> organisationUnitIds;
        private String organisationUnitId;

        OrganisationUnitsFilter(List<String> organisationUnitIds) {
            this.organisationUnitIds = organisationUnitIds;
        }

        void setOrganisationUnitId(String organisationUnitId) {
            this.organisationUnitId = organisationUnitId;
        }

        @Override
        public boolean apply() {
            return !(organisationUnitIds == null || organisationUnitId == null) &&
                    !organisationUnitIds.contains(organisationUnitId);
        }
    }

    private static class PeriodFilter implements Filter, Serializable {
        private final DateTime startDate;
        private final DateTime endDate;
        private DateTime selectedDate;

        PeriodFilter(DateTime startDate, DateTime endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        void setSelectedDate(DateTime selectedDate) {
            this.selectedDate = selectedDate;
        }

        @Override
        public boolean apply() {
            if ((startDate == null && endDate == null) || selectedDate == null) {
                return false;
            }

            if (startDate != null && endDate != null) {
                // return true, if criteria is not between two dates
                // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
                return !(selectedDate.isAfter(startDate) && selectedDate.isBefore(endDate));
            }

            if (startDate != null) {
                // return true, if criteria is before startDate
                // return startDate.isBefore(selectedDate);
                return !(selectedDate.isAfter(startDate));
            }

            // return true, if criteria is after endDate
            // return endDate.isAfter(selectedDate);
            return !(selectedDate.isBefore(endDate));
        }
    }
}
