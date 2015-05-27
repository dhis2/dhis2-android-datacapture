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
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.api.date.CustomDateIterator;
import org.dhis2.mobile.api.date.DateIteratorFactory;
import org.dhis2.mobile.api.models.DateHolder;
import org.dhis2.mobile.sdk.entities.DataSet;
import org.dhis2.mobile.sdk.persistence.loaders.CursorLoaderBuilder;
import org.dhis2.mobile.sdk.persistence.loaders.Transformation;
import org.dhis2.mobile.ui.adapters.SimpleAdapter;

import java.util.List;

public class PeriodDialogFragment extends DialogFragment
        implements LoaderCallbacks<DataSet>,
        View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = PeriodDialogFragment.class.getName();
    private static final int LOADER_ID = 345234575;

    private ListView mListView;
    private Button mPrevious;
    private Button mNext;

    private SimpleAdapter<DateHolder> mAdapter;
    private OnPeriodSetListener mListener;

    private CustomDateIterator<List<DateHolder>> mIterator;

    public static PeriodDialogFragment newInstance(OnPeriodSetListener listener,
                                                   String dataSetId) {
        PeriodDialogFragment fragment = new PeriodDialogFragment();
        Bundle args = new Bundle();
        // args.putString(DataSets.ID, dataSetId);
        fragment.setArguments(args);
        fragment.setOnItemClickListener(listener);
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
        View view = inflater.inflate(R.layout.dialog_fragment_listview_period, container, false);
        mListView = (ListView) view.findViewById(R.id.simple_listview);
        mPrevious = (Button) view.findViewById(R.id.previous);
        mNext = (Button) view.findViewById(R.id.next);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SimpleAdapter<>(getActivity());
        mAdapter.setStringExtractor(new ExtractPeriodLabel());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                mNext.setEnabled(true);
                mAdapter.swapData(mIterator.previous());
                break;
            }
            case R.id.next: {
                if (mIterator.hasNext()) {
                    List<DateHolder> dates = mIterator.next();
                    if (!mIterator.hasNext()) {
                        mNext.setEnabled(false);
                    }
                    mAdapter.swapData(dates);
                } else {
                    mNext.setEnabled(false);
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            DateHolder date = mAdapter.getItemSafely(position);
            if (date != null) {
                mListener.onPeriodSelected(date);
            }
            dismiss();
        }
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    public void setOnItemClickListener(OnPeriodSetListener listener) {
        mListener = listener;
    }

    @Override
    public Loader<DataSet> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ID && bundle != null) {
            /* String dataSetId = bundle.getString(DataSets.ID);
            Uri uri = DataSets.CONTENT_URI.buildUpon()
                    .appendPath(dataSetId).build();
            return CursorLoaderBuilder.forUri(uri)
                    .projection(DbManager.with(DataSet.class).getProjection())
                    .transformation(new TransformDataSet())
                    .build(getActivity()); */
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<DataSet> loader, DataSet dataSet) {
        if (loader != null && loader.getId() == LOADER_ID && dataSet != null) {
            mIterator = DateIteratorFactory.getDateIterator(
                    dataSet.getPeriodType(), dataSet.isAllowFuturePeriods()
            );

            mAdapter.swapData(mIterator.current());
            if (mIterator != null && mIterator.hasNext()) {
                mNext.setEnabled(true);
            } else {
                mNext.setEnabled(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<DataSet> dbRowLoader) {
    }

    public interface OnPeriodSetListener {
        public void onPeriodSelected(DateHolder date);
    }

    static class TransformDataSet implements Transformation<DataSet> {

        @Override
        public DataSet transform(Context context, Cursor cursor) {
            //return DbManager.with(DataSet.class).mapSingleItem(cursor, false);
            return null;
        }
    }

    static class ExtractPeriodLabel implements SimpleAdapter.ExtractStringCallback<DateHolder> {

        @Override
        public String getString(DateHolder object) {
            return object.getLabel();
        }
    }
}
