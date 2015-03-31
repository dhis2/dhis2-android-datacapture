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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.persistence.database.DbContract;
import org.dhis2.mobile.sdk.persistence.database.DbContract.UnitDataSets;
import org.dhis2.mobile.sdk.persistence.handlers.DataSetHandler;
import org.dhis2.mobile.sdk.persistence.loaders.CursorLoaderBuilder;
import org.dhis2.mobile.sdk.persistence.loaders.Transformation;
import org.dhis2.mobile.ui.adapters.SimpleAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class DataSetDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<DataSet>> {
    private static final int LOADER_ID = 340962123;
    private static String TAG = DataSetDialogFragment.class.getName();
    @InjectView(R.id.simple_listview) ListView mListView;
    private SimpleAdapter<DataSet> mAdapter;
    private OnDatasetSetListener mListener;

    public static DataSetDialogFragment newInstance(OnDatasetSetListener listener,
                                                    String orgUnitId) {
        DataSetDialogFragment fragment = new DataSetDialogFragment();
        Bundle args = new Bundle();
        args.putString(UnitDataSets.ORGANISATION_UNIT_ID, orgUnitId);
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_listview, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter<>(getActivity());
        mAdapter.setStringExtractor(new StringExtractor());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }


    @OnItemClick(R.id.simple_listview)
    public void onItemClick(int position) {
        if (mListener != null) {
            DataSet dataSet = mAdapter.getItemSafely(position);
            if (dataSet != null) {
                mListener.onDataSetSelected(
                        dataSet.getId(), dataSet.getDisplayName()
                );
            }
        }
        dismiss();
    }

    @Override
    public Loader<List<DataSet>> onCreateLoader(int id, Bundle bundle) {
        if (LOADER_ID == id && bundle != null) {
            String orgUnitId = bundle.getString(UnitDataSets.ORGANISATION_UNIT_ID);
            Uri uri = DbContract.OrganisationUnits.buildUriWithDataSets(orgUnitId);
            return CursorLoaderBuilder.forUri(uri)
                    .projection(DataSetHandler.PROJECTION)
                    .transformation(new DataSetTransform())
                    .build(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<DataSet>> listLoader,
                               List<DataSet> dbRows) {
        mAdapter.swapData(dbRows);
    }

    @Override
    public void onLoaderReset(Loader<List<DataSet>> listLoader) {
    }

    public void setOnClickListener(OnDatasetSetListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public interface OnDatasetSetListener {
        public void onDataSetSelected(String dataSetId, String dataSetName);
    }

    static class DataSetTransform implements Transformation<List<DataSet>> {

        @Override
        public List<DataSet> transform(Context context, Cursor cursor) {
            return DataSetHandler.map(cursor, false);
        }
    }

    static class StringExtractor implements SimpleAdapter.ExtractStringCallback<DataSet> {

        @Override
        public String getString(DataSet object) {
            return object.getDisplayName();
        }
    }
}
