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

package org.dhis2.ehealthMobile.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.WorkService;
import org.dhis2.ehealthMobile.io.handlers.UserAccountHandler;
import org.dhis2.ehealthMobile.io.models.Field;
import org.dhis2.ehealthMobile.io.models.Group;
import org.dhis2.ehealthMobile.network.HTTPClient;
import org.dhis2.ehealthMobile.network.NetworkUtils;
import org.dhis2.ehealthMobile.network.Response;
import org.dhis2.ehealthMobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.dhis2.ehealthMobile.utils.PrefUtils.Resources;
import org.dhis2.ehealthMobile.utils.PrefUtils.State;
import org.dhis2.ehealthMobile.utils.TextFileUtils;
import org.dhis2.ehealthMobile.utils.TextFileUtils.Directory;
import org.dhis2.ehealthMobile.utils.TextFileUtils.FileNames;
import org.dhis2.ehealthMobile.utils.ToastManager;
import org.dhis2.ehealthMobile.utils.ViewUtils;

import java.util.ArrayList;

public class MyProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Group> {
    public static final String TAG = "org.dhis2.mobile.ui.fragments.MyProfileFragment";
    public static final String ON_UPLOAD_FINISHED_LISTENER_TAG = "onUploadFinishedListenerTag";
    public static final String ON_UPDATE_FINISHED_LISTENER_TAG = "onUpdateFinishedListenerTag";
    public static final String GROUP = "group";

    private static final int MY_PROFILE_LOADER_ID = TAG.length();
    private static final String IS_REFRESHING = "isRefreshing";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mList;
    private ProgressBar mProgressBar;
    private FloatingActionButton mUploadButton;
    private FieldAdapter mAdapter;
    private boolean mIsRefreshing;

    private BroadcastReceiver mOnProfileInfoUploadListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context con, Intent intent) {
            hideProgressInActionBar();
            showUploadButton(true);

            int code = intent.getExtras().getInt(Response.CODE);
            Context context = getActivity();
            String message;
            if (HTTPClient.isError(code)) {
                message = context.getString(R.string.profile_changes_saved);
            } else {
                message = context.getString(R.string.profile_changes_uploaded);
            }
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver mOnProfileInfoUpdateListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context con, Intent intent) {
            hideProgressInActionBar();
            loadData();

            int code = intent.getExtras().getInt(Response.CODE);
            if (HTTPClient.isError(code)) {
                Context context = getActivity();
                String message = HTTPClient.getErrorMessage(context, code);
                ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            }
        }
    };

    // Work out loading stuff here
    private static class DataLoader extends AsyncTaskLoader<Group> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Group loadInBackground() {
            String info = TextFileUtils.readTextFile(getContext(),
                    Directory.ROOT,
                    FileNames.ACCOUNT_INFO);

            ArrayList<Field> fields = UserAccountHandler.toFields(getContext(), info);

            Group group = null;
            if (fields != null) {
                group = new Group(MyProfileFragment.TAG, fields);
            }

            return group;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);
        mList = (ListView) mSwipeRefreshLayout.findViewById(R.id.list_of_fields);
        mProgressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        mUploadButton = (FloatingActionButton) root.findViewById(R.id.upload_button);

        ViewUtils.hideAndDisableViews(mList, mUploadButton, mProgressBar);

        OnRefreshListener listener = new OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!mIsRefreshing) {
                    startUpdate();
                }
            }
        };

        int blue = R.color.actionbar_blue;
        int grey = R.color.light_grey;

        mSwipeRefreshLayout.setOnRefreshListener(listener);
        mSwipeRefreshLayout.setColorScheme(blue, grey, blue, grey);

        mUploadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startUpload();
            }
        });

        // restoring previous state of fragment
        restoreFromPreviousState(savedInstanceState);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle fragmentSavedState = new Bundle();

        if (mAdapter != null && mAdapter.getGroup() != null) {
            fragmentSavedState.putParcelable(GROUP, mAdapter.getGroup());
        }

        if (mSwipeRefreshLayout != null) {
            fragmentSavedState.putBoolean(IS_REFRESHING, mSwipeRefreshLayout.isRefreshing());
        }

        outState.putBundle(TAG, fragmentSavedState);
        super.onSaveInstanceState(outState);
    }

    private void restoreFromPreviousState(Bundle savedInstanceState) {
        Bundle fragmentSavedState;
        if (savedInstanceState == null ||
                savedInstanceState.getBundle(TAG) == null) {
            fragmentSavedState = new Bundle();
        } else {
            fragmentSavedState = savedInstanceState.getBundle(TAG);
        }

        boolean isRefreshing = fragmentSavedState.getBoolean(IS_REFRESHING, false);
        State state = PrefUtils.getResourceState(getActivity(), Resources.PROFILE_DETAILS);

        if (!isRefreshing) {
            isRefreshing = state == State.REFRESHING;
        }

        if (isRefreshing) {
            showProgressInActionBar();
            hideUploadButton(false);
        } else {
            boolean needsUpdate = state == State.OUT_OF_DATE;
            boolean isConnectionAvailable = NetworkUtils.checkConnection(getActivity());

            if (needsUpdate && isConnectionAvailable) {
                startUpdate();
            }
        }

        Group group = fragmentSavedState.getParcelable(GROUP);
        if (group == null) {
            loadData();
        } else {
            onLoadFinished(group, false);
        }
    }

    private void loadData() {
        showProgressBar(false);
        hideUploadButton(false);
        setEnabledSwipeRefreshLayout(false);

        getLoaderManager().restartLoader(MY_PROFILE_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
        manager.registerReceiver(mOnProfileInfoUploadListener, new IntentFilter(ON_UPLOAD_FINISHED_LISTENER_TAG));
        manager.registerReceiver(mOnProfileInfoUpdateListener, new IntentFilter(ON_UPDATE_FINISHED_LISTENER_TAG));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
        manager.unregisterReceiver(mOnProfileInfoUploadListener);
        manager.unregisterReceiver(mOnProfileInfoUpdateListener);
        super.onPause();
    }

    @Override
    public Loader<Group> onCreateLoader(int id, Bundle args) {
        return new DataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Group> loader, Group group) {
        onLoadFinished(group, true);
    }

    private void onLoadFinished(Group group, boolean withAnimation) {
        Log.i("onLoadFinished()", "isCalled");
        mAdapter = new FieldAdapter(group, getActivity());
        mList.setAdapter(mAdapter);

        hideProgressBar(withAnimation);
        setEnabledSwipeRefreshLayout(true);

        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
            showUploadButton(withAnimation);
        }
    }

    @Override
    public void onLoaderReset(Loader<Group> loader) {
    }

    private void startUpload() {
        if (getActivity() != null && mAdapter != null) {
            showProgressInActionBar();
            hideUploadButton(true);

            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_PROFILE_INFO);
            intent.putParcelableArrayListExtra(GROUP, mAdapter.getGroup().getFields());
            getActivity().startService(intent);
        }
    }

    private void startUpdate() {
        Context context = getActivity();
        if (context == null) {
            return;
        }

        boolean isConnectionAvailable = NetworkUtils.checkConnection(context);
        if (isConnectionAvailable) {
            System.out.println("startUpdate() is called from MyProfileFragment");
            showProgressInActionBar();
            hideUploadButton(true);

            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_PROFILE_INFO);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            hideProgressInActionBar();
        }
    }

    private void showProgressBar(boolean withAnimation) {
        if (withAnimation) {
            ViewUtils.perfomOutAnimation(getActivity(), R.anim.fade_out, true, mList);
        } else {
            ViewUtils.hideAndDisableViews(mList);
        }
        ViewUtils.enableViews(mProgressBar);
    }

    private void hideProgressBar(boolean withAnimation) {
        ViewUtils.hideAndDisableViews(mProgressBar);
        if (withAnimation) {
            ViewUtils.perfomInAnimation(getActivity(), R.anim.fade_in, mList);
        } else {
            ViewUtils.enableViews(mList);
        }
    }

    private void showUploadButton(boolean withAnimation) {
        if (withAnimation) {
            ViewUtils.perfomInAnimation(getActivity(), R.anim.fade_in, mUploadButton);
        } else {
            ViewUtils.enableViews(mUploadButton);
        }
    }

    private void hideUploadButton(boolean withAnimation) {
        if (withAnimation) {
            ViewUtils.perfomOutAnimation(getActivity(), R.anim.fade_out, true, mUploadButton);
        } else {
            ViewUtils.hideAndDisableViews(mUploadButton);
        }
    }

    private void showProgressInActionBar() {
        if (mSwipeRefreshLayout != null) {
            mIsRefreshing = true;

            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    private void hideProgressInActionBar() {
        if (mSwipeRefreshLayout != null) {
            mIsRefreshing = false;

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setEnabledSwipeRefreshLayout(boolean flag) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(flag);
        }
    }
}