package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Dashboard;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;
import org.hisp.dhis.mobile.datacapture.ui.activities.DashboardItemDetailActivity;
import org.hisp.dhis.mobile.datacapture.ui.adapters.DashboardItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment implements LoaderCallbacks<CursorHolder<List<DBItemHolder<DashboardItem>>>>,
        DashboardItemAdapter.OnItemClickListener {
    private static final int LOADER_ID = 74734523;
    private GridView mGridView;
    private DashboardItemAdapter mAdapter;

    public static DashboardFragment newInstance(DBItemHolder<Dashboard> dashboard) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();

        args.putInt(DashboardItemColumns.DASHBOARD_DB_ID, dashboard.getDatabaseId());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new DashboardItemAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // getLoaderManager().initLoader(LOADER_ID, getArguments(), DashboardFragment.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    getLoaderManager().initLoader(LOADER_ID, getArguments(), DashboardFragment.this);
                }
            }
        }, 1000);
    }

    @Override
    public Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            int dashboardId = getArguments().getInt(DashboardItemColumns.DASHBOARD_DB_ID);
            String SELECTION = DashboardItemColumns.DASHBOARD_DB_ID + " = " + dashboardId + " AND " +
                    DashboardItemColumns.STATE + " != " + "'" + State.DELETING + "'" + " AND " +
                    DashboardItemColumns.TYPE + " != " + "'" + DashboardItem.TYPE_REPORT_TABLES + "'" + " AND " +
                    DashboardItemColumns.TYPE + " != " + "'" + DashboardItem.TYPE_MESSAGES + "'";
            return new ItemsLoader(getActivity(), DashboardItemColumns.CONTENT_URI,
                    DashboardItemHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> loader,
                               CursorHolder<List<DBItemHolder<DashboardItem>>> data) {
        if (loader != null && loader.getId() == LOADER_ID && mAdapter != null) {
            mAdapter.swapData(data.getData());
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> loader) {
        // reset the state of screen
    }

    @Override
    public void onItemClick(DBItemHolder<DashboardItem> dbItem) {
        if (dbItem != null) {
            Intent intent = DashboardItemDetailActivity.prepareIntent(getActivity(),
                    dbItem.getDatabaseId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemShareInterpretation(DBItemHolder<DashboardItem> dbItem) {

    }

    public static class ItemsLoader extends AbsCursorLoader<List<DBItemHolder<DashboardItem>>> {

        public ItemsLoader(Context context, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DBItemHolder<DashboardItem>> readDataFromCursor(Cursor cursor) {
            List<DBItemHolder<DashboardItem>> items = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    DBItemHolder<DashboardItem> item = DashboardItemHandler.fromCursor(cursor);
                    items.add(item);
                } while (cursor.moveToNext());
            }

            return items;
        }
    }
}
