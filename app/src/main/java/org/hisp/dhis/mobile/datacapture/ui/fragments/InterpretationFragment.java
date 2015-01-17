package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
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
import android.widget.GridView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.mobile.datacapture.BusProvider;
import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.InterpretationSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.events.OnInterpretationsSyncEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.InterpretationHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.State;
import org.hisp.dhis.mobile.datacapture.api.models.Interpretation;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.InterpretationColumns;
import org.hisp.dhis.mobile.datacapture.ui.adapters.InterpretationAdapter;
import org.hisp.dhis.mobile.datacapture.ui.dialogs.CommentsDialog;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class InterpretationFragment extends BaseFragment
        implements LoaderCallbacks<CursorHolder<List<DBItemHolder<Interpretation>>>>,
        InterpretationAdapter.OnItemClickListener {
    private static final int LOADER_ID = 366916901;
    private static final String STATE_PROGRESS = "stateProgress";

    private GridView mGridView;
    private InterpretationAdapter mAdapter;
    private SmoothProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interpretation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);
        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_PROGRESS)) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        mAdapter = new InterpretationAdapter(getActivity());
        mAdapter.setCallback(this);

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putBoolean(STATE_PROGRESS, mProgressBar.isShown());
        super.onSaveInstanceState(out);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_interpretation_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh_interpretations) {
            BusProvider.getInstance().post(new InterpretationSyncEvent());
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.progressiveStart();
            return true;
        }
        return false;
    }

    @Subscribe
    public void onInterpretationsSyncEvent(OnInterpretationsSyncEvent event) {
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public Loader<CursorHolder<List<DBItemHolder<Interpretation>>>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            final String SELECTION = InterpretationColumns.STATE + " = " + "'" + State.GETTING.toString() + "'";
            return new InterpretationLoader(getActivity(), InterpretationColumns.CONTENT_URI,
                    InterpretationHandler.PROJECTION, SELECTION, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DBItemHolder<Interpretation>>>> loader,
                               CursorHolder<List<DBItemHolder<Interpretation>>> data) {
        if (LOADER_ID == loader.getId() && data != null && data.getData() != null) {
            mAdapter.swapData(data.getData());
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DBItemHolder<Interpretation>>>> loader) {
    }

    @Override
    public void onItemClicked(DBItemHolder<Interpretation> interpretation) {

    }

    @Override
    public void onEditInterpretation(DBItemHolder<Interpretation> interpretation) {

    }

    @Override
    public void onDeleteInterpretation(DBItemHolder<Interpretation> interpretation) {

    }

    @Override
    public void onShowCommentsDialog(DBItemHolder<Interpretation> interpretation) {
        CommentsDialog dialog = new CommentsDialog();
        dialog.setData(interpretation.getItem().getComments());
        dialog.show(getChildFragmentManager(), CommentsDialog.COMMENTS_DIALOG);
    }

    public static class InterpretationLoader extends AbsCursorLoader<List<DBItemHolder<Interpretation>>> {

        public InterpretationLoader(Context context, Uri uri, String[] projection,
                                    String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DBItemHolder<Interpretation>> readDataFromCursor(Cursor cursor) {
            List<DBItemHolder<Interpretation>> dbItems = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    dbItems.add(InterpretationHandler.fromCursor(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }

            return dbItems;
        }
    }
}
