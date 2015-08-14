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

package org.hisp.dhis.android.datacapture.ui.fragments.aggregate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.datacapture.R;
import org.hisp.dhis.android.datacapture.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.datacapture.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataSet;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.UnitToDataSetRelation;
import org.hisp.dhis.android.datacapture.ui.adapters.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.datacapture.ui.fragments.AutoCompleteDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSetDialogFragment extends AutoCompleteDialogFragment
        implements LoaderManager.LoaderCallbacks<List<OptionAdapterValue>> {
    public static final int ID = 23464235;
    private static final int LOADER_ID = 340962123;
    private static final String ORG_UNIT_ID = "args:orgUnitId";

    public static DataSetDialogFragment newInstance(OnOptionSelectedListener listener,
                                                    String orgUnitId) {
        DataSetDialogFragment fragment = new DataSetDialogFragment();
        Bundle args = new Bundle();
        args.putString(ORG_UNIT_ID, orgUnitId);
        fragment.setOnOptionSetListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogTitle(getResources().getString(R.string.dialog_data_sets));
        setDialogId(ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<OptionAdapterValue>> onCreateLoader(int id, Bundle bundle) {
        if (LOADER_ID == id && bundle != null) {
            String orgUnitId = bundle.getString(ORG_UNIT_ID);
            List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            tablesToTrack.add(UnitToDataSetRelation.class);
            tablesToTrack.add(DataSet.class);
            return new DbLoader<>(getActivity().getApplication(),
                    tablesToTrack, new DataSetsQuery(orgUnitId));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<OptionAdapterValue>> loader,
                               List<OptionAdapterValue> data) {
        if (loader != null && loader.getId() == LOADER_ID) {
            getAdapter().swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<OptionAdapterValue>> loader) {
        if (loader != null && loader.getId() == LOADER_ID) {
            getAdapter().swapData(null);
        }
    }

    static class DataSetsQuery implements Query<List<OptionAdapterValue>> {
        private final String mOrgUnitId;

        public DataSetsQuery(String orgUnitId) {
            mOrgUnitId = orgUnitId;
        }

        @Override public List<OptionAdapterValue> query(Context context) {
            List<DataSet> dataSets = OrganisationUnit
                    .queryRelatedDataSetsFromDb(mOrgUnitId);
            Collections.sort(dataSets, DataSet.DISPLAY_NAME_MODEL_COMPARATOR);
            List<OptionAdapterValue> adapterValues = new ArrayList<>();
            for (DataSet dataSet : dataSets) {
                adapterValues.add(new OptionAdapterValue(
                        dataSet.getId(), dataSet.getDisplayName(), dataSet.getCategoryCombo().getId()
                ));
            }
            return adapterValues;
        }
    }
}
