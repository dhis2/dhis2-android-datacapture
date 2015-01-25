package org.hisp.dhis.mobile.datacapture.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.CreateReportEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportGroupHandler;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Group;
import org.hisp.dhis.mobile.datacapture.api.models.Report;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportColumns;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import java.util.List;

public class ReportEntryActivity extends ActionBarActivity
        implements LoaderCallbacks<CursorHolder<List<DBItemHolder<Group>>>> {
    private static final String ORG_UNIT_ID_EXTRA = "extra:orgUnitId";
    private static final String PERIOD_EXTRA = "extra:Period";
    private static final String DATASET_ID_EXTRA = "extra:dataSetId";

    private static final int LOADER_ID = 89254134;

    private ListView mListView;
    private ProgressBar mProgressBar;

    public static Intent newIntent(FragmentActivity activity,
                                   String orgUnitId, String dataSetId, String period) {
        Intent intent = new Intent(activity, ReportEntryActivity.class);
        intent.putExtra(ORG_UNIT_ID_EXTRA, orgUnitId);
        intent.putExtra(DATASET_ID_EXTRA, dataSetId);
        intent.putExtra(PERIOD_EXTRA, period);
        return intent;
    }

    private static Report getReportFromBundle(Bundle extras) {
        Report report = new Report();

        if (extras != null) {
            String orgUnitId = extras.getString(ORG_UNIT_ID_EXTRA);
            String dataSetId = extras.getString(DATASET_ID_EXTRA);
            String periodExtra = extras.getString(PERIOD_EXTRA);

            report.setOrgUnit(orgUnitId);
            report.setDataSet(dataSetId);
            report.setPeriod(periodExtra);
        }

        return report;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_entry);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mListView = (ListView) findViewById(R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
        if (savedInstanceState == null) {
            CreateReportEvent event = new CreateReportEvent();
            event.setReport(getReportFromBundle(getIntent().getExtras()));
            BusProvider.getInstance().post(event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<CursorHolder<List<DBItemHolder<Group>>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            Report report = getReportFromBundle(args);
            final String ORG_UNIT = ReportColumns.ORG_UNIT_ID + " = " + "'" + report.getOrgUnit() + "'";
            final String DATASET = ReportColumns.DATASET_ID + " = " + "'" + report.getDataSet() + "'";
            final String PERIOD = ReportColumns.PERIOD + " = " + "'" + report.getPeriod() + "'";
            final String SELECTION = ORG_UNIT + " AND " + DATASET + " AND " + PERIOD;
            return new ReportLoader(this, ReportColumns.CONTENT_URI,
                    ReportGroupHandler.PROJECTION, SELECTION, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<DBItemHolder<Group>>>> loader,
                               CursorHolder<List<DBItemHolder<Group>>> data) {
        if (loader != null && LOADER_ID == loader.getId() && data != null) {

        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<DBItemHolder<Group>>>> loader) {

    }

    static class ReportLoader extends AbsCursorLoader<List<DBItemHolder<Group>>> {

        public ReportLoader(Context context, Uri uri, String[] projection,
                            String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<DBItemHolder<Group>> readDataFromCursor(Cursor cursor) {
            if (cursor != null) {
                System.out.println("Count of values: " + cursor.getCount());
            }
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    System.out.println("Cursor label: " + cursor.getString(1));
                } while(cursor.moveToNext());
                //return ReportHandler.fromCursor(cursor);
                return null;
            } else {
                return null;
            }
        }
    }
}
