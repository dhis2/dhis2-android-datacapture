package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.ui.dialogs.ListViewDialogFragment;
import org.hisp.dhis.mobile.datacapture.ui.views.SelectorView;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;
import org.hisp.dhis.mobile.datacapture.utils.ObjectHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AggregateReportFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<CursorHolder<List<OrganisationUnit>>>,
        ListViewDialogFragment.OnDialogItemClickListener, View.OnClickListener {
    private static final String STATE_OBJECT_ID = "state:StateObjectId";
    private static final int ORG_UNIT_DIALOG = 6732824;
    private static final int DATASET_DIALOG = 5431432;
    private static final int PERIOD_DIALOG = 2344653;
    private static final int LOADER_ID = 345784834;

    private SmoothProgressBar mProgressBar;

    private SelectorView mOrgUnitView;
    private SelectorView mDataSetView;

    private List<OrganisationUnit> mUnits;

    private ReportFragmentState mState;

    private ListViewDialogFragment mOrgUnitDialog;
    private ListViewDialogFragment mDataSetDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aggregate_report_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);

        mOrgUnitView = (SelectorView) view.findViewById(R.id.org_unit_selector);
        mDataSetView = (SelectorView) view.findViewById(R.id.dataset_selector);

        mOrgUnitDialog = ListViewDialogFragment.newInstance(ORG_UNIT_DIALOG);
        mDataSetDialog = ListViewDialogFragment.newInstance(DATASET_DIALOG);

        mOrgUnitView.setOnClickListener(this);
        mDataSetView.setOnClickListener(this);

        if (savedInstanceState != null &&
                savedInstanceState.getInt(STATE_OBJECT_ID, -1) > 0) {
            int id = savedInstanceState.getInt(STATE_OBJECT_ID, -1);
            mState = (ReportFragmentState) ObjectHolder.getInstance().pop(id);
        }

        if (mState != null && mState.isIsSyncInProgress()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mState = new ReportFragmentState();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int id = ObjectHolder.getInstance().put(mState);
        outState.putInt(STATE_OBJECT_ID, id);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onDataSetSyncEvent(OnDatasetSyncEvent event) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public Loader<CursorHolder<List<OrganisationUnit>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            final String ORH_UNITS_KEY = KeyValue.Type.ORG_UNITS_WITH_DATASETS.toString();
            String SELECTION = KeyValueColumns.KEY + " = " + "'" + ORH_UNITS_KEY + "'" + " AND " +
                    KeyValueColumns.TYPE + " = " + "'" + ORH_UNITS_KEY + "'";
            return new UnitsLoader(getActivity(), KeyValueColumns.CONTENT_URI,
                    KeyValueHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<OrganisationUnit>>> loader,
                               CursorHolder<List<OrganisationUnit>> data) {
        if (loader != null && LOADER_ID == loader.getId() &&
                data != null && data.getData() != null) {
            mUnits = data.getData();
            handleUnits();
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<OrganisationUnit>>> loader) {
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.org_unit_selector: {
                mOrgUnitDialog.show(getChildFragmentManager(),
                        ListViewDialogFragment.TAG);
                break;
            }
            case R.id.dataset_selector: {
                mDataSetDialog.show(getChildFragmentManager(),
                        ListViewDialogFragment.TAG);
                break;
            }
        }
    }

    @Override
    public void onItemClickListener(int dialogId, int position) {
        switch (dialogId) {
            case ORG_UNIT_DIALOG: {
                OrganisationUnit unit = mUnits.get(position);
                mState.setOrgUnit(unit);
                mOrgUnitView.setText(unit.getLabel());
                handleDataSets();
                break;
            }
            case DATASET_DIALOG: {
                OrganisationUnit unit = mState.getOrgUnit();
                DataSet dataSet = unit.getDataSets().get(position);
                mState.setDataSet(dataSet);
                mDataSetView.setText(dataSet.getLabel());
                break;
            }
            case PERIOD_DIALOG: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported dialog");
            }
        }
    }

    private void handleUnits() {
        ArrayList<String> labels = new ArrayList<>();
        for (OrganisationUnit unit : mUnits) {
            labels.add(unit.getLabel());
        }

        mOrgUnitDialog.swapData(labels);
        mOrgUnitView.setEnabled(true);
    }

    private void handleDataSets() {
        List<DataSet> dataSets = mState.getOrgUnit().getDataSets();
        ArrayList<String> labels = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            labels.add(dataSet.getLabel());
        }

        mDataSetDialog.swapData(labels);
        mDataSetView.setEnabled(true);
    }

    private void handlePeriod() {
        DataSet dataSet = mState.getDataSet();
        // do stuff here
    }

    public static class UnitsLoader extends AbsCursorLoader<List<OrganisationUnit>> {

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
                Type type = new TypeToken<List<OrganisationUnit>>() {
                }.getType();
                units = gson.fromJson(dbItem.getItem().getValue(), type);
            }

            return units;
        }
    }

    static class ReportFragmentState {
        private OrganisationUnit mOrgUnit;
        private DataSet mDataSet;
        private DateHolder mDateHolder;
        private boolean mIsSyncInProgress;

        public OrganisationUnit getOrgUnit() {
            return mOrgUnit;
        }

        public void setOrgUnit(OrganisationUnit orgUnit) {
            mOrgUnit = orgUnit;
        }

        public DataSet getDataSet() {
            return mDataSet;
        }

        public void setDataSet(DataSet dataSet) {
            mDataSet = dataSet;
        }

        public DateHolder getDateHolder() {
            return mDateHolder;
        }

        public void setDateHolder(DateHolder dateHolder) {
            mDateHolder = dateHolder;
        }

        public boolean isIsSyncInProgress() {
            return mIsSyncInProgress;
        }

        public void setIsSyncInProgress(boolean isSyncInProgress) {
            mIsSyncInProgress = isSyncInProgress;
        }
    }
}
