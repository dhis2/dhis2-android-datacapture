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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.TextFileUtils.Directory;
import org.dhis2.mobile.utils.ToastManager;

import java.util.ArrayList;

public class AggregateReportDataEntryActivity extends BaseDataEntryActivity implements LoaderCallbacks<Form> {
    public static final String TAG = AggregateReportDataEntryActivity.class.getSimpleName();
    private static final String DOWNLOAD_ATTEMPTED = "downloadAttempted";
    private static final String DOWNLOAD_IN_PROGRESS = "downloadInProgress";

    private static final int DATA_LOADER_ID = 896927645;
    private static final int DATA_PARSER_ID = 834923487;

    private DatasetInfoHolder info;

    private boolean downloadAttempted = false;
    private boolean downloadInProgress = false;

    private int selectedAdapter = -1;
    private int selectedListViewPos = -1;

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cxt, Intent intent) {
            downloadInProgress = false;
            hideDownloadStatusLabel();

            int code = intent.getExtras().getInt(Response.CODE);
            if (HTTPClient.isError(code)) {
                loadForm();
                return;
            }

            getSupportLoaderManager().restartLoader(DATA_PARSER_ID,
                    intent.getExtras(), AggregateReportDataEntryActivity.this).forceLoad();
        }
    };

    private static class DataLoader extends AsyncTaskLoader<Form> {
        private String formId;

        public DataLoader(Context context, String formId) {
            super(context);
            this.formId = formId;
        }

        @Override
        public Form loadInBackground() {
            if (formId != null && TextFileUtils.doesFileExist(getContext(), Directory.DATASETS, formId)) {
                String jForm = TextFileUtils.readTextFile(getContext(), Directory.DATASETS, formId);
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

    private static class DataParser extends AsyncTaskLoader<Form> {
        private String form;

        public DataParser(Context context, String form) {
            super(context);
            this.form = form;
        }

        @Override
        public Form loadInBackground() {
            if (form != null) {
                try {
                    JsonObject jsonForm = JsonHandler.buildJsonObject(form);
                    return JsonHandler.fromJson(jsonForm, Form.class);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getIntent().getExtras().getParcelable(DatasetInfoHolder.TAG);

        ArrayList<Group> groups = null;
        if (savedInstanceState != null) {
            groups = savedInstanceState.getParcelableArrayList(RETRIEVED_DATA);
            downloadAttempted = savedInstanceState.getBoolean(DOWNLOAD_ATTEMPTED, false);
            downloadInProgress = savedInstanceState.getBoolean(DOWNLOAD_IN_PROGRESS, false);

            selectedAdapter = savedInstanceState.getInt(SELECTED_ADAPTER, -1);
            selectedListViewPos = savedInstanceState.getInt(SELECTED_LISTVIEW_ITEM, -1);
        }

        if (!downloadAttempted && !downloadInProgress) {
            downloadAttempted = true;
            if (NetworkUtils.checkConnection(this)) {
                downloadInProgress = true;
                getLatestValues();
            }
        }

        if (!downloadInProgress) {
            if (groups == null) {
                loadForm();
            } else {
                loadGroupsIntoAdapters(groups);
            }
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (downloadInProgress) {
            showDownloadStatusLabel();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(TAG));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOAD_ATTEMPTED, downloadAttempted);
        outState.putBoolean(DOWNLOAD_IN_PROGRESS, downloadInProgress);
    }

    @Override
    public Loader<Form> onCreateLoader(int id, Bundle args) {
        if (id == DATA_PARSER_ID && args != null && args.getString(Response.BODY) != null) {
            return new DataParser(this, args.getString(Response.BODY));
        } else if (id == DATA_LOADER_ID && info != null) {
            return new DataLoader(AggregateReportDataEntryActivity.this, info.getFormId());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Form> loader, Form form) {
        if (loader != null && loader.getId() == DATA_PARSER_ID) {
            if (form == null || form.getGroups() == null || form.getGroups().size() == 0) {
                loadForm();
            } else {
                AggregateReportDataEntryActivity.this.onLoadFinished(form);
            }
        } else if (loader != null && loader.getId() == DATA_LOADER_ID) {
            onLoadFinished(form);
        }
    }

    @Override
    public void onLoaderReset(Loader<Form> loader) { }

    @Override
    protected void upload() {
        if (getAdapters() == null) {
            ToastManager.makeToast(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<Group> groups = new ArrayList<Group>();
        for (FieldAdapter adapter : getAdapters()) {
            groups.add(adapter.getGroup());
        }

        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_DATASET);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        intent.putExtra(Group.TAG, groups);
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

    private void getLatestValues() {
        showProgressBar();
        showDownloadStatusLabel();
        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD, WorkService.METHOD_DOWNLOAD_LATEST_DATASET_VALUES);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        startService(intent);
    }
}