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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.utils.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class BaseDataEntryActivity extends BaseActivity {
	public static final String RETRIEVED_DATA = "retrievedData";
	public static final String SELECTED_ADAPTER = "selectedAdapters";
	public static final String SELECTED_LISTVIEW_ITEM = "selectedListViewItem";

    private class FieldAdapterLoader extends AsyncTask<Void, Void, ArrayList<FieldAdapter>> {
        private ArrayList<Group> groups;
        private WeakReference<Context> context;

        public FieldAdapterLoader(Context context, ArrayList<Group> groups) {
            this.context = new WeakReference<Context>(context);
            this.groups = groups;
        }

        @Override
        public void onPreExecute() {
            showProgressBar();
            hideDownloadStatusLabel();
        }

        @Override
        protected ArrayList<FieldAdapter> doInBackground(Void... params) {
            if (context.get() != null && groups != null) {
                try {
                    ArrayList<FieldAdapter> adapters = new ArrayList<FieldAdapter>();
                    for (Group group : groups) {
                        adapters.add(new FieldAdapter(group, context.get()));
                    }
                    return adapters;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<FieldAdapter> adapters) {
            hideProgressBar();
            hideDownloadStatusLabel();
            onAdaptersReady(adapters);
        }
    }

    private View mUploadButton;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mDownloadStatusLabel;
    private ArrayList<FieldAdapter> mAdapters;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_data_entry);

		mUploadButton = findViewById(R.id.upload_button);
		mListView = (ListView) findViewById(R.id.list_of_fields);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mDownloadStatusLabel = (TextView) findViewById(R.id.downloading_values_message);

		mUploadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upload();
            }
        });

        showProgressBar();
        hideDownloadStatusLabel();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mAdapters != null) {
			ArrayList<Group> groups = new ArrayList<Group>();
			for (FieldAdapter adapter : mAdapters) {
				groups.add(adapter.getGroup());
			}
			outState.putParcelableArrayList(RETRIEVED_DATA, groups);
			outState.putInt(SELECTED_ADAPTER, getSupportActionBar().getSelectedNavigationIndex());
			outState.putInt(SELECTED_LISTVIEW_ITEM, mListView.getFirstVisiblePosition());
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_down);
	}

    protected void loadGroupsIntoAdapters(ArrayList<Group> groups) {
        if (groups != null) {
            new FieldAdapterLoader(this, groups).execute();
        }
    }

	protected void setAdapters(ArrayList<FieldAdapter> adapters) {
        this.mAdapters = adapters;
		if (mAdapters == null || mAdapters.size() == 0) {
			return;
		}

		if (mAdapters.size() == 1) {
			mListView.setAdapter(mAdapters.get(0));
		} else if (mAdapters.size() > 1) {
			ActionBar actionBar = getSupportActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            ArrayList<String> groupLabels = new ArrayList<String>();
			for (int i = 0; i < mAdapters.size(); i++) {
				groupLabels.add(mAdapters.get(i).getLabel());
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
					R.layout.spinner_item, groupLabels);
			adapter.setDropDownViewResource(R.layout.dropdown_spinner_item);

			OnNavigationListener navigationListener = new OnNavigationListener() {

				@Override
				public boolean onNavigationItemSelected(int position, long id) {
					mListView.setAdapter(mAdapters.get(position));
					return true;
				}
			};

			actionBar.setListNavigationCallbacks(adapter, navigationListener);
		}
	}

    protected void restoreListViewSelection(final int savedAdapterPos,
                                            final int savedListPosition) {
        ActionBar actionBar = getSupportActionBar();
        if (savedAdapterPos >= 0) {
            actionBar.setSelectedNavigationItem(savedAdapterPos);
        }

        if (savedListPosition >= 0) {
            // Delayed execution is necessary because mListView needs
            // time to draw content which is being provided by adapter.
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListView.setSelection(savedListPosition);
                }

            }, 50);
        }
    }

	protected ArrayList<FieldAdapter> getAdapters() {
		return mAdapters;
	}

    protected void showProgressBar() {
        ViewUtils.hideAndDisableViews(mUploadButton, mListView);
        ViewUtils.enableViews(mProgressBar);
    }

    protected void hideProgressBar() {
        ViewUtils.enableViews(mUploadButton, mListView);
        ViewUtils.hideAndDisableViews(mProgressBar);
    }

    protected void hideDownloadStatusLabel() {
        ViewUtils.disableViews(mDownloadStatusLabel);
    }

    protected void showDownloadStatusLabel() {
        ViewUtils.enableViews(mDownloadStatusLabel);
    }

    protected abstract void onAdaptersReady(ArrayList<FieldAdapter> adapters);
	protected abstract void upload();
}
