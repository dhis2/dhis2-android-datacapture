package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.KeyValueHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.DateHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.api.models.DataSet;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValues;
import org.hisp.dhis.mobile.datacapture.ui.activities.ReportEntryActivity;
import org.hisp.dhis.mobile.datacapture.ui.dialogs.ListViewDialogFragment;
import org.hisp.dhis.mobile.datacapture.ui.dialogs.ListViewDialogFragment.OnDialogItemClickListener;
import org.hisp.dhis.mobile.datacapture.ui.dialogs.PeriodDialogFragment;
import org.hisp.dhis.mobile.datacapture.ui.views.CardDetailedButton;
import org.hisp.dhis.mobile.datacapture.ui.views.CardTextViewButton;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.utils.ObjectHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AggregateReportFragment extends BaseFragment
        implements View.OnClickListener, LoaderCallbacks<CursorHolder<List<OrganisationUnit>>> {
    private static final String STATE = "state:AggregateReportFragment";
    private static final int LOADER_ID = 345784834;

    private SmoothProgressBar mProgressBar;
    private CardTextViewButton mOrgUnitButton;
    private CardTextViewButton mDataSetButton;
    private CardTextViewButton mPeriodButton;
    private CardDetailedButton mButton;

    private ListViewDialogFragment mOrgUnitDialog;
    private ListViewDialogFragment mDataSetDialog;
    private PeriodDialogFragment mPeriodDialog;

    private FragmentState mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aggregate_report_layout, container, false);

        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);

        mOrgUnitButton = (CardTextViewButton) view.findViewById(R.id.org_unit_button);
        mDataSetButton = (CardTextViewButton) view.findViewById(R.id.dataset_button);
        mPeriodButton = (CardTextViewButton) view.findViewById(R.id.period_button);
        mButton = (CardDetailedButton) view.findViewById(R.id.data_entry_button);

        mOrgUnitDialog = new ListViewDialogFragment();
        mDataSetDialog = new ListViewDialogFragment();
        mPeriodDialog = new PeriodDialogFragment();

        mOrgUnitButton.setOnClickListener(this);
        mDataSetButton.setOnClickListener(this);
        mPeriodButton.setOnClickListener(this);
        mButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mOrgUnitButton.setEnabled(false);
        mDataSetButton.setEnabled(false);
        mPeriodButton.setEnabled(false);
        mButton.hide(false);

        if (savedInstanceState != null &&
                savedInstanceState.getInt(STATE, -1) > 0) {
            int id = savedInstanceState.getInt(STATE, -1);
            mState = (FragmentState) ObjectHolder.getInstance().pop(id);
        }

        if (mState == null) {
            mState = new FragmentState();
        }

        mProgressBar.setVisibility(mState.isSyncInProcess() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh) {
            mProgressBar.setVisibility(View.VISIBLE);
            BusProvider.getInstance().post(new DatasetSyncEvent());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.org_unit_button: {
                mOrgUnitDialog.show(getChildFragmentManager());
                break;
            }
            case R.id.dataset_button: {
                mDataSetDialog.show(getChildFragmentManager());
                break;
            }
            case R.id.period_button: {
                mPeriodDialog.show(getChildFragmentManager());
                break;
            }
            case R.id.data_entry_button: {
                startReportEntryActivity();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        mState.setSyncInProcess(mProgressBar.isShown());
        out.putInt(STATE, ObjectHolder.getInstance().put(mState));
        super.onSaveInstanceState(out);
    }

    @Override
    public Loader<CursorHolder<List<OrganisationUnit>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            final String ORH_UNITS_KEY = KeyValue.Type.ORG_UNITS_WITH_DATASETS.toString();
            String SELECTION = KeyValues.KEY + " = " + "'" + ORH_UNITS_KEY + "'" + " AND " +
                    KeyValues.TYPE + " = " + "'" + ORH_UNITS_KEY + "'";
            return new UnitsLoader(getActivity(), KeyValues.CONTENT_URI,
                    KeyValueHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<OrganisationUnit>>> loader,
                               CursorHolder<List<OrganisationUnit>> data) {
        if (loader != null && LOADER_ID == loader.getId() &&
                data != null && data.getData() != null) {
            handleUnits(data.getData());

            // restoring from saved state
            OrganisationUnit unit = mState.getOrganisationUnit();
            DataSet dataSet = mState.getDataSet();
            DateHolder period = mState.getPeriod();

            if (unit != null) {
                onUnitSelected(unit);

                if (dataSet != null) {
                    onDataSetSelected(dataSet);

                    if (period != null) {
                        onPeriodSelected(period);
                    }
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<OrganisationUnit>>> loader) {
    }

    @Subscribe
    public void onDataSetSyncEvent(OnDatasetSyncEvent event) {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void handleUnits(final List<OrganisationUnit> units) {
        List<String> labels = new ArrayList<>();
        for (OrganisationUnit unit : units) {
            labels.add(unit.getLabel());
        }

        mOrgUnitButton.setEnabled(true);
        mOrgUnitDialog.swapData(labels);
        mOrgUnitDialog.setOnItemClickListener(new OnDialogItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                onUnitSelected(units.get(position));
            }
        });
    }

    private void onUnitSelected(OrganisationUnit unit) {
        mOrgUnitButton.setText(unit.getLabel());
        mState.setOrganisationUnit(unit);
        mState.setDataSet(null);
        mState.setPeriod(null);
        handleDataSets(unit.getDataSets());
        handleViews(0);
    }

    private void handleDataSets(final List<DataSet> dataSets) {
        List<String> labels = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            labels.add(dataSet.getLabel());
        }

        mDataSetButton.setEnabled(true);
        mDataSetDialog.swapData(labels);
        mDataSetDialog.setOnItemClickListener(new OnDialogItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                onDataSetSelected(dataSets.get(position));
            }
        });
    }

    private void onDataSetSelected(DataSet dataSet) {
        mDataSetButton.setText(dataSet.getLabel());
        mState.setDataSet(dataSet);
        mState.setPeriod(null);
        handlePeriod(dataSet);
        handleViews(1);
    }

    private void handlePeriod(DataSet dataSet) {
        mPeriodButton.setEnabled(true);
        mPeriodDialog.setPeriodType(dataSet.getOptions().getPeriodType(),
                dataSet.getOptions().isAllowFuturePeriods());
        mPeriodDialog.setOnItemClickListener(new PeriodDialogFragment.OnDialogItemClickListener() {
            @Override
            public void onItemClickListener(DateHolder date) {
                onPeriodSelected(date);
            }
        });
    }

    private void onPeriodSelected(DateHolder dateHolder) {
        mPeriodButton.setText(dateHolder.getLabel());
        mState.setPeriod(dateHolder);
        handleButton();
        handleViews(2);
    }

    private void handleButton() {
        String orgUnit = getString(R.string.organization_unit) + ": " +
                mState.getOrganisationUnit().getLabel();
        String dataSet = getString(R.string.dataset) + ": " +
                mState.getDataSet().getLabel();
        String period = getString(R.string.period) + ": " +
                mState.getPeriod().getLabel();
        mButton.setFirstLineText(orgUnit);
        mButton.setSecondLineText(dataSet);
        mButton.setThirdLineText(period);
    }

    private void handleViews(int level) {
        switch (level) {
            case 0:
                mPeriodButton.setEnabled(false);
            case 1:
                mButton.hide(true);
                break;
            case 2:
                mButton.show(true);
        }
    }

    private void startReportEntryActivity() {
        String orgUnitId = mState.getOrganisationUnit().getId();
        String dataSetId = mState.getDataSet().getId();
        String period = mState.getPeriod().getDate();
        Intent intent = ReportEntryActivity.newIntent(
                getActivity(), orgUnitId, dataSetId, period
        );
        startActivity(intent);
    }

    static class UnitsLoader extends AbsCursorLoader<List<OrganisationUnit>> {
        public UnitsLoader(Context context, Uri uri, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<OrganisationUnit> readDataFromCursor(Cursor cursor) {
            List<OrganisationUnit> units = null;

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                DBItemHolder<KeyValue> dbItem = KeyValueHandler.fromCursor(cursor);
                Gson gson = new Gson();
                Type type = new TypeToken<List<OrganisationUnit>>() { }.getType();
                units = gson.fromJson(dbItem.getItem().getValue(), type);
            }

            return units;
        }
    }

    static class FragmentState {
        private boolean isSyncInProcess;
        private OrganisationUnit organisationUnit;
        private DataSet dataSet;
        private DateHolder period;

        public boolean isSyncInProcess() {
            return isSyncInProcess;
        }

        public void setSyncInProcess(boolean isSyncInProcess) {
            this.isSyncInProcess = isSyncInProcess;
        }

        public DataSet getDataSet() {
            return dataSet;
        }

        public void setDataSet(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        public OrganisationUnit getOrganisationUnit() {
            return organisationUnit;
        }

        public void setOrganisationUnit(OrganisationUnit organisationUnit) {
            this.organisationUnit = organisationUnit;
        }

        public DateHolder getPeriod() {
            return period;
        }

        public void setPeriod(DateHolder period) {
            this.period = period;
        }
    }
}
