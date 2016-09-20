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

package org.dhis2.mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.processors.FormsDownloadProcessor;
import org.dhis2.mobile.processors.LoginProcessor;
import org.dhis2.mobile.processors.MyProfileProcessor;
import org.dhis2.mobile.processors.OfflineDataProcessor;
import org.dhis2.mobile.processors.RemoveDataProcessor;
import org.dhis2.mobile.processors.ReportDownloadProcessor;
import org.dhis2.mobile.processors.ReportUploadProcessor;
import org.dhis2.mobile.processors.SendSmsProcessor;
import org.dhis2.mobile.ui.activities.LoginActivity;
import org.dhis2.mobile.ui.fragments.MyProfileFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkService extends Service {
    public static final String METHOD = "method";
    public static final String METHOD_UPDATE_PROFILE_INFO = "updateProfileInfo";
    public static final String METHOD_UPLOAD_PROFILE_INFO = "uploadProfileInfo";
    public static final String METHOD_LOGIN_USER = "loginUser";
    public static final String METHOD_UPDATE_DATASETS = "updateDatasets";
    public static final String METHOD_DOWNLOAD_LATEST_DATASET_VALUES = "downloadLatestDatasetValues";
    public static final String METHOD_UPLOAD_DATASET = "aggregateReportUploadProcessor";
    public static final String METHOD_OFFLINE_DATA_UPLOAD = "offlineDataUploading";
    public static final String METHOD_REMOVE_ALL_DATA = "removeAllData";
    public static final String METHOD_SEND_VIA_SMS = "sendViaSms";

    // maximum number of threads in thread pool
    private static final int QUANTITY_OF_THREADS = 3;
    private static final String TAG = WorkService.class.getSimpleName();

    private ExecutorService executor;

    // This ArrayList is used to track running tasks
    private ArrayList<Runnable> tasks;

    @Override
    public void onCreate() {
        super.onCreate();

        executor = Executors.newFixedThreadPool(QUANTITY_OF_THREADS);
        tasks = new ArrayList<>();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        final Bundle extras = intent.getExtras();
        final Runnable task = new Runnable() {

            @Override
            public void run() {
                runMethod(extras, getBaseContext());
                // this method is called in order
                // to remove current task from
                // list of running tasks
                onTaskFinished(this);
            }
        };

        // add task to list of running tasks,
        // and execute it
        tasks.add(task);
        executor.execute(task);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void runMethod(Bundle extras, Context context) {
        if (extras == null || extras.getString(METHOD) == null) {
            return;
        }

        final String methodName = extras.getString(METHOD);
        Log.i(TAG, methodName);

        if (METHOD_UPLOAD_PROFILE_INFO.equals(methodName)) {
            ArrayList<Field> fields = extras.getParcelableArrayList(MyProfileFragment.GROUP);
            MyProfileProcessor.uploadProfileInfo(context, fields);
        }

        if (METHOD_UPDATE_PROFILE_INFO.equals(methodName)) {
            MyProfileProcessor.updateProfileInfo(context);
        }

        if (METHOD_LOGIN_USER.equals(methodName)) {
            String username = extras.getString(LoginActivity.USERNAME);
            String server = extras.getString(LoginActivity.SERVER);
            String creds = extras.getString(LoginActivity.CREDENTIALS);
            LoginProcessor.loginUser(context, server, creds, username);
        }

        if (METHOD_UPDATE_DATASETS.equals(methodName)) {
            FormsDownloadProcessor.updateDatasets(context);
        }

        if (METHOD_DOWNLOAD_LATEST_DATASET_VALUES.equals(methodName)) {
            DatasetInfoHolder info = extras.getParcelable(DatasetInfoHolder.TAG);
            ReportDownloadProcessor.download(context, info);
        }

        if (METHOD_UPLOAD_DATASET.equals(methodName)) {
            DatasetInfoHolder info = extras.getParcelable(DatasetInfoHolder.TAG);
            ArrayList<Group> groups = extras.getParcelableArrayList(Group.TAG);
            ReportUploadProcessor.upload(context, info, groups);
        }

        if (METHOD_OFFLINE_DATA_UPLOAD.equals(methodName)) {
            OfflineDataProcessor.upload(context);
        }

        if (METHOD_REMOVE_ALL_DATA.equals(methodName)) {
            RemoveDataProcessor.removeData(context);
        }

        if (METHOD_SEND_VIA_SMS.equals(methodName)){
            DatasetInfoHolder info = extras.getParcelable(DatasetInfoHolder.TAG);
            ArrayList<Group> groups = extras.getParcelableArrayList(Group.TAG);
            SendSmsProcessor.send(context, info, groups);
        }
    }

    private void onTaskFinished(Runnable obj) {
        // remove task of list of running tasks
        tasks.remove(obj);

        // kill service if there is no any running tasks
        if (tasks.size() == 0) {
            stopSelf();
        }
    }
}
