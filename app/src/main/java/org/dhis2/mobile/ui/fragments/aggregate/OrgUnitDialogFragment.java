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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.loaders.CursorLoaderBuilder;
import org.dhis2.mobile.sdk.persistence.loaders.Transformation;
import org.dhis2.mobile.ui.adapters.SimpleAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class OrgUnitDialogFragment extends DialogFragment implements LoaderCallbacks<List<OrganisationUnit>> {
    private static final String TAG = OrgUnitDialogFragment.class.getName();
    private static final int LOADER_ID = 243756345;

    @InjectView(R.id.simple_listview) ListView mListView;
    private SimpleAdapter<OrganisationUnit> mAdapter;
    private OnOrgUnitSetListener mListener;

    public static OrgUnitDialogFragment newInstance(OnOrgUnitSetListener listener) {
        OrgUnitDialogFragment fragment = new OrgUnitDialogFragment();
        fragment.setOnClickListener(listener);
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
            OrganisationUnit unit = mAdapter.getItemSafely(position);
            if (unit != null) {
                mListener.onUnitSelected(
                        unit.getId(), unit.getDisplayName()
                );
            }
        }
        dismiss();
    }

    public void setOnClickListener(OnOrgUnitSetListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    @Override
    public Loader<List<OrganisationUnit>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            /* return CursorLoaderBuilder.forUri(OrganisationUnits.CONTENT_URI)
                    .projection(DbManager.with(OrganisationUnit.class).getProjection())
                    .transformation(new OrgUnitTransform())
                    .build(getActivity()); */
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<OrganisationUnit>> loader,
                               List<OrganisationUnit> data) {
        mAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<OrganisationUnit>> loader) {
    }

    public interface OnOrgUnitSetListener {
        public void onUnitSelected(String orgUnitId, String orgUnitLabel);
    }

    static class OrgUnitTransform implements Transformation<List<OrganisationUnit>> {

        @Override
        public List<OrganisationUnit> transform(Context context, Cursor cursor) {
            //return DbManager.with(OrganisationUnit.class).map(cursor, false);
            return null;
        }
    }

    static class StringExtractor implements SimpleAdapter.ExtractStringCallback<OrganisationUnit> {

        @Override
        public String getString(OrganisationUnit object) {
            return object.getDisplayName();
        }
    }
}