package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.DashboardItemHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.DashboardItemColumns;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<CursorHolder<List<DBItemHolder<DashboardItem>>>> {
    private static final int LOADER_ID = 74734523;

    public static DashboardFragment newInstance(int dashboardId) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();

        args.putInt(DashboardItemColumns.DASHBOARD_DB_ID, dashboardId);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_dashboard, group, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            String SELECTION = DashboardItemColumns.STATE + " != " + '"' + State.DELETING + '"' +
                    " AND " + DashboardItemColumns.DASHBOARD_DB_ID +
                    " = " + getArguments().getInt(DashboardItemColumns.DASHBOARD_DB_ID);
            return new ItemsLoader(getActivity(), DashboardItemColumns.CONTENT_URI,
                    DashboardItemHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> loader,
                               CursorHolder<List<DBItemHolder<DashboardItem>>> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            for (DBItemHolder<DashboardItem> item : data.getData()) {
                System.out.println("Item {id, type}: " + item.getItem().getId() +
                " " + item.getItem().getType());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DBItemHolder<DashboardItem>>>> loader) {
        // reset the state of screen
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
