package org.dhis2.mobile.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.OrganizationUnit;
import org.dhis2.mobile.ui.adapters.PickerAdapter;
import org.dhis2.mobile.ui.models.Picker;
import org.dhis2.mobile.utils.TextFileUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AggregateReportFragment2 extends Fragment
        implements LoaderManager.LoaderCallbacks<Picker> {
    private static final String TAG = AggregateReportFragment2.class.getName();
    private static final int AGGREGATE_REPORT_LOADER_ID = TAG.length();

    private ListView pickerListView;
    private PickerAdapter pickerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aggregate_report_fragment_layout_2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pickerListView = (ListView) view.findViewById(R.id.listview_pickers);
        pickerAdapter = new PickerAdapter(getChildFragmentManager(), getActivity());

        pickerListView.setAdapter(pickerAdapter);
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

    /* This class is responsible for async. data loading from storage */
    private static class DataLoader extends AsyncTaskLoader<Picker> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Picker loadInBackground() {
            String jSourceUnits;
            if (TextFileUtils.doesFileExist(getContext(),
                    TextFileUtils.Directory.ROOT, TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS)) {
                jSourceUnits = TextFileUtils.readTextFile(getContext(),
                        TextFileUtils.Directory.ROOT, TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
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
            Picker rootNode = null;

            if (units != null && !units.isEmpty()) {
                rootNode = Picker.create(chooseOrganisationUnit);

                for (OrganizationUnit organisationUnit : units) {
                    Picker organisationUnitPicker = Picker.create(
                            organisationUnit.getId(), organisationUnit.getLabel(),
                            chooseDataSet, rootNode);

                    if (organisationUnit.getForms() != null &&
                            !organisationUnit.getForms().isEmpty()) {

                        // going through data set
                        for (Form dataSet : organisationUnit.getForms()) {
                            Picker programPicker = Picker.create(dataSet.getId(),
                                    dataSet.getLabel(), organisationUnitPicker);
                            organisationUnitPicker.addChild(programPicker);
                        }
                    }

                    rootNode.addChild(organisationUnitPicker);
                }
            }

            return rootNode;
        }
    }
}
