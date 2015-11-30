/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.google.gson.JsonObject;

import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.holders.ProgramInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;

public class SEWRDataEntryActivity extends BaseDataEntryActivity implements LoaderManager.LoaderCallbacks<Form> {
    private static final int DATA_LOADER_ID = 8571034;
	private ProgramInfoHolder info;
	
	private int selectedAdapter = -1;
	private int selectedListViewPos = -1;

    private static class DataLoader extends AsyncTaskLoader<Form> {
        private String formId;

        public DataLoader(Context context, String formId) {
            super(context);
            this.formId = formId;
        }

        @Override
        public Form loadInBackground() {
            if (formId != null && TextFileUtils.doesFileExist(getContext(), TextFileUtils.Directory.PROGRAMS, formId)) {
                String jForm = TextFileUtils.readTextFile(getContext(), TextFileUtils.Directory.PROGRAMS, formId);
                if (jForm != null) {
                    try {
                        JsonObject jsonForm = JsonHandler.buildJsonObject(jForm);
                        return JsonHandler.fromJson(jsonForm, Form.class);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (ParsingException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		info = getIntent().getExtras().getParcelable(ProgramInfoHolder.TAG);
		
		ArrayList<Group> groups = null;
		if (savedInstanceState != null) {
			groups = savedInstanceState.getParcelableArrayList(RETRIEVED_DATA);			
			selectedAdapter = savedInstanceState.getInt(SELECTED_ADAPTER, -1);
			selectedListViewPos = savedInstanceState.getInt(SELECTED_LISTVIEW_ITEM, -1);
		}

        if (groups != null) {
            loadGroupsIntoAdapters(groups);
        } else {
            loadForm();
        }
	}

    @Override
    public Loader<Form> onCreateLoader(int id, Bundle args) {
        if (info != null) {
            String formId = info.getFormId();
            return new DataLoader(SEWRDataEntryActivity.this, formId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Form> loader, Form form) {
        SEWRDataEntryActivity.this.onLoadFinished(form);
    }

    @Override
    public void onLoaderReset(Loader<Form> loader) { }

    @Override
    protected void upload() {
        ArrayList<Group> groups = new ArrayList<Group>();
        for (FieldAdapter adapter : getAdapters()) {
            groups.add(adapter.getGroup());
        }

		Intent intent = new Intent(this, WorkService.class);
		intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_SEWR);
        intent.putExtra(Group.TAG, groups);
        intent.putExtra(ProgramInfoHolder.TAG, info);
		startService(intent);
        finish();
    }

    private void loadForm() {
        showProgressBar();
        getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this).forceLoad();
    }

    private void onLoadFinished(Form form) {
        hideProgressBar();
        if (form != null) {
            loadGroupsIntoAdapters(form.getGroups());
        }
    }

    protected void onAdaptersReady(ArrayList<FieldAdapter> adapters) {
        setAdapters(adapters);
        restoreListViewSelection(selectedAdapter, selectedListViewPos);
    }
}
