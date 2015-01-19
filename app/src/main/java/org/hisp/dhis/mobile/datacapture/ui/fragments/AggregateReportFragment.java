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

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.DatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnDatasetSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.KeyValueHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.api.models.OrganisationUnit;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;

import java.lang.reflect.Type;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AggregateReportFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<CursorHolder<List<OrganisationUnit>>> {
    private static final String STATE_PROGRESS = "progressState";
    private static final int LOADER_ID = 345784834;

    private SmoothProgressBar mProgressBar;

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
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(STATE_PROGRESS)) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PROGRESS, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onDatasetSyncEvent(OnDatasetSyncEvent event) {
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

        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<OrganisationUnit>>> loader) {
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
                Type type = new TypeToken<List<OrganisationUnit>>() { }.getType();
                units = gson.fromJson(dbItem.getItem().getValue(), type);
            }

            return units;
        }
    }
}
