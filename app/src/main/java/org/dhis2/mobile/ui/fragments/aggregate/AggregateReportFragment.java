/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.fragments.aggregate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.squareup.otto.Subscribe;

import org.dhis2.mobile.R;
import org.dhis2.mobile.api.models.DateHolder;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.persistence.loaders.CursorLoaderBuilder;
import org.dhis2.mobile.sdk.persistence.loaders.Transformation;
import org.dhis2.mobile.ui.activities.ReportEntryActivity;
import org.dhis2.mobile.ui.fragments.BaseFragment;
import org.dhis2.mobile.ui.fragments.aggregate.DataSetDialogFragment.OnDatasetSetListener;
import org.dhis2.mobile.ui.fragments.aggregate.OrgUnitDialogFragment.OnOrgUnitSetListener;
import org.dhis2.mobile.ui.fragments.aggregate.PeriodDialogFragment.OnPeriodSetListener;
import org.dhis2.mobile.ui.views.CardDetailedButton;
import org.dhis2.mobile.ui.views.CardTextViewButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AggregateReportFragment extends BaseFragment
        implements LoaderCallbacks<Boolean>, OnOrgUnitSetListener, OnDatasetSetListener, OnPeriodSetListener {
    private static final String STATE = "state:AggregateReportFragment";
    private static final int LOADER_ID = 345784834;

    @InjectView(R.id.progress_bar) SmoothProgressBar mProgressBar;
    @InjectView(R.id.unit_button) CardTextViewButton mOrgUnitButton;
    @InjectView(R.id.dataset_button) CardTextViewButton mDataSetButton;
    @InjectView(R.id.period_button) CardTextViewButton mPeriodButton;
    @InjectView(R.id.data_entry_button) CardDetailedButton mButton;

    private AggregateReportFragmentState mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_aggregate_report, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mOrgUnitButton.setEnabled(false);
        mDataSetButton.setEnabled(false);
        mPeriodButton.setEnabled(false);
        mButton.hide(false);

        if (savedInstanceState != null &&
                savedInstanceState.getParcelable(STATE) != null) {
            mState = savedInstanceState.getParcelable(STATE);
        }

        if (mState == null) {
            mState = new AggregateReportFragmentState();
        }

        mProgressBar.setVisibility(mState.isSyncInProcess() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refresh) {
            getDhisService().syncMetaData();
            Toast.makeText(getActivity(), "Syncing meta data",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(STATE, mState);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onUnitSelected(String orgUnitId, String orgUnitLabel) {
        mOrgUnitButton.setText(orgUnitLabel);
        mDataSetButton.setEnabled(true);

        mState.setOrgUnit(orgUnitId, orgUnitLabel);
        mState.resetDataSet();
        mState.resetPeriod();
        handleViews(0);
    }

    @Override
    public void onDataSetSelected(String dataSetId, String dataSetLabel) {
        mDataSetButton.setText(dataSetLabel);
        mPeriodButton.setEnabled(true);

        mState.setDataSet(dataSetId, dataSetLabel);
        mState.resetPeriod();
        handleViews(1);
    }

    @Override
    public void onPeriodSelected(DateHolder dateHolder) {
        mPeriodButton.setText(dateHolder.getLabel());
        mState.setPeriod(dateHolder);
        handleButton();
        handleViews(2);
    }

    private void handleButton() {
        String orgUnit = getString(R.string.organisation_unit) +
                ": " + mState.getOrgUnitLabel();
        String dataSet = getString(R.string.dataset) +
                ": " + mState.getDataSetLabel();
        String period = getString(R.string.period) +
                ": " + mState.getPeriod().getLabel();
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
        String orgUnitId = mState.getOrgUnitId();
        String orgUnitLabel = mState.getOrgUnitLabel();

        String dataSetId = mState.getDataSetId();
        String dataSetLabel = mState.getDataSetLabel();

        String period = mState.getPeriod().getDate();
        String periodLabel = mState.getPeriod().getLabel();

        Intent intent = ReportEntryActivity.newIntent(
                getActivity(),
                orgUnitId, orgUnitLabel,
                dataSetId, dataSetLabel,
                period, periodLabel
        );
        startActivity(intent);
    }

    @OnClick({
            R.id.unit_button, R.id.dataset_button,
            R.id.period_button, R.id.data_entry_button
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unit_button: {
                OrgUnitDialogFragment fragment = OrgUnitDialogFragment
                        .newInstance(this);
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.dataset_button: {
                DataSetDialogFragment fragment = DataSetDialogFragment
                        .newInstance(this, mState.getOrgUnitId());
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.period_button: {
                PeriodDialogFragment fragment = PeriodDialogFragment
                        .newInstance(this, mState.getDataSetId());
                fragment.show(getChildFragmentManager());
                break;
            }
            case R.id.data_entry_button: {
                // startReportEntryActivity();
                break;
            }
        }
    }

    @Subscribe
    public void onApiException(APIException apiException) {
        apiException.printStackTrace();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID) {
            /* return CursorLoaderBuilder.forUri(OrganisationUnits.CONTENT_URI)
                    .projection(DbManager.with(OrganisationUnit.class).getProjection())
                    .transformation(new OrgUnitTransformation())
                    .build(getActivity());
                    */
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> booleanLoader,
                               Boolean hasUnits) {
        if (booleanLoader != null &&
                booleanLoader.getId() == LOADER_ID) {
            mOrgUnitButton.setEnabled(hasUnits);
            if (!hasUnits) {
                return;
            }

            AggregateReportFragmentState backedUpState = new AggregateReportFragmentState(mState);
            if (!backedUpState.isOrgUnitEmpty()) {
                onUnitSelected(
                        backedUpState.getOrgUnitId(),
                        backedUpState.getOrgUnitLabel()
                );

                if (!backedUpState.isDataSetEmpty()) {
                    onDataSetSelected(
                            backedUpState.getDataSetId(),
                            backedUpState.getDataSetLabel()
                    );

                    if (!backedUpState.isPeriodEmpty()) {
                        onPeriodSelected(backedUpState.getPeriod());
                    }
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> booleanLoader) {
    }

    static class OrgUnitTransformation implements Transformation<Boolean> {

        @Override
        public Boolean transform(Context context, Cursor cursor) {
            return (cursor != null && cursor.getCount() > 0);
        }
    }
}
