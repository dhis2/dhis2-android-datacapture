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
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.Model;

import org.dhis2.mobile.R;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.loaders.DbLoader;
import org.dhis2.mobile.sdk.persistence.loaders.Query;
import org.dhis2.mobile.ui.adapters.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.dhis2.mobile.ui.fragments.AutoCompleteDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrgUnitDialogFragment extends AutoCompleteDialogFragment
        implements LoaderCallbacks<List<OptionAdapterValue>> {
    private static final String TAG = OrgUnitDialogFragment.class.getName();
    private static final int LOADER_ID = 243756345;
    public static final int ID = 573455;

    public static OrgUnitDialogFragment newInstance(OnOptionSelectedListener listener) {
        OrgUnitDialogFragment fragment = new OrgUnitDialogFragment();
        fragment.setOnOptionSetListener(listener);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogTitle(getResources().getString(R.string.dialog_organisation_units));
        setDialogId(ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<OptionAdapterValue>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            List<Class<? extends Model>> tablesToTrack = new ArrayList<>();
            tablesToTrack.add(OrganisationUnit.class);
            return new DbLoader<>(getActivity().getApplicationContext(),
                    tablesToTrack, new OrgUnitQuery());
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

    static class OrgUnitQuery implements Query<List<OptionAdapterValue>> {

        @Override public List<OptionAdapterValue> query(Context context) {
            List<OrganisationUnit> units = new Select()
                    .from(OrganisationUnit.class).queryList();
            Collections.sort(units, OrganisationUnit.DISPLAY_NAME_MODEL_COMPARATOR);
            List<OptionAdapterValue> optionAdapterValues = new ArrayList<>();
            for (OrganisationUnit unit : units) {
                optionAdapterValues.add(new OptionAdapterValue(
                        unit.getId(), unit.getDisplayName(), null));
            }
            return optionAdapterValues;
        }
    }
}