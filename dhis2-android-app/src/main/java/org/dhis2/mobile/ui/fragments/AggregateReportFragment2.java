package org.dhis2.mobile.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Category;
import org.dhis2.mobile.io.models.CategoryOption;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.ui.adapters.PickerAdapter;
import org.dhis2.mobile.ui.models.Picker;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.ToastManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import static org.dhis2.mobile.utils.ViewUtils.perfomOutAnimation;

public class AggregateReportFragment2 extends Fragment
        implements LoaderManager.LoaderCallbacks<Picker> {
    private static final String TAG = AggregateReportFragment2.class.getName();
    private static final int AGGREGATE_REPORT_LOADER_ID = TAG.length();

    private PickerAdapter pickerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aggregate_report_fragment_layout_2, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        pickerAdapter = new PickerAdapter(getChildFragmentManager(), getActivity());

        ListView pickerListView = (ListView) root.findViewById(R.id.listview_pickers);
        pickerListView.setAdapter(pickerAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);

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

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(AGGREGATE_REPORT_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<Picker> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Picker> loader, Picker data) {
        if (data != null) {
            pickerAdapter.swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Picker> loader) {
        // stub implementation
    }


    private void startUpdate() {
        Log.i("startUpdate()", "Starting update of dataSets");
        Context context = getActivity();
        if (context == null) {
            return;
        }

        boolean isConnectionAvailable = NetworkUtils.checkConnection(context);
        if (isConnectionAvailable) {
            // showProgressInActionBar();
            // Hide dataentry button from screen
            // perfomOutAnimation(getActivity(), R.anim.out_right, true, userEntryContainer);
            // Reset to default all previous user choices
            // resetValues();

            // Prepare Intent and start service
            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_DATASETS);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            // hideProgressInActionBar();
        }
    }

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

                    if (organisationUnit.getForms() != null &&
                            !organisationUnit.getForms().isEmpty()) {

                        // going through data set
                        for (Form dataSet : organisationUnit.getForms()) {
                            Picker dataSetPicker = new Picker.Builder()
                                    .id(dataSet.getId())
                                    .name(dataSet.getLabel())
                                    .parent(organisationUnitPicker)
                                    .build();
                            organisationUnitPicker.addChild(dataSetPicker);

                            System.out.println("CategoryCombo: " + dataSet.getCategoryCombo());

                            if (dataSet.getCategoryCombo() != null &&
                                    dataSet.getCategoryCombo().getCategories() != null) {

                                for (Category category : dataSet.getCategoryCombo().getCategories()) {
                                    String label = String.format(Locale.getDefault(), "%s %s",
                                            choose, category.getLabel());
                                    Picker categoryPicker = new Picker.Builder()
                                            .id(category.getId())
                                            .hint(label)
                                            .parent(dataSetPicker)
                                            .asRoot()
                                            .build();
                                    dataSetPicker.addChild(categoryPicker);

                                    System.out.println("Category: " + category.getLabel());

                                    if (category.getCategoryOptions() != null &&
                                            !category.getCategoryOptions().isEmpty()) {
                                        for (CategoryOption option : category.getCategoryOptions()) {
                                            Picker categoryOptionPicker = new Picker.Builder()
                                                    .id(option.getId())
                                                    .name(option.getLabel())
                                                    .parent(categoryPicker)
                                                    .build();
                                            categoryPicker.addChild(categoryOptionPicker);

                                            System.out.println("CategoryOption: " + option.getLabel());
                                        }
                                    }
                                }
                            }
                        }
                    }

                    rootNode.addChild(organisationUnitPicker);
                }
            }

            return rootNode;
        }
    }
}
