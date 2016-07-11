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

package org.dhis2.mobile.processors;

import android.content.Context;

import com.google.gson.Gson;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.handlers.ImportSummariesHandler;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.network.URLConstants;
import org.dhis2.mobile.utils.NotificationBuilder;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;

import java.io.File;

public class OfflineDataProcessor {
    private static boolean isRunning;

    private OfflineDataProcessor() {
    }

    public static void upload(Context context) {
        isRunning = true;
        uploadOfflineReports(context);
        uploadProfileInfo(context);
        isRunning = false;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private static void uploadProfileInfo(Context context) {
        if (PrefUtils.accountInfoNeedsUpdate(context)) {
            String url = PrefUtils.getServerURL(context) + URLConstants.API_USER_ACCOUNT_URL;
            String creds = PrefUtils.getCredentials(context);
            String accInfo = TextFileUtils.readTextFile(context,
                    TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ACCOUNT_INFO);
            Response resp = HTTPClient.post(url, creds, accInfo);
            if (!HTTPClient.isError(resp.getCode())) {
                PrefUtils.setAccountUpdateFlag(context, false);
            }
        }
    }

    private static void uploadOfflineReports(Context context) {
        String path = TextFileUtils.getDirectoryPath(context, TextFileUtils.Directory.OFFLINE_DATASETS);
        File directory = new File(path);
        if (!directory.exists()) {
            return;
        }
        File[] reportFiles = directory.listFiles();
        String url = PrefUtils.getServerURL(context) + URLConstants.DATASET_UPLOAD_URL;
        String creds = PrefUtils.getCredentials(context);
        Gson gson = new Gson();
        if (reportFiles != null && reportFiles.length > 0) {
            for (File reportFile : reportFiles) {
                // Retrieve offline report from file system
                String report = TextFileUtils.readTextFile(reportFile);
                // Try to upload to server
                Response resp = HTTPClient.post(url, creds, report);
                // If upload was successful, notify user and delete offline
                // report
                if (!HTTPClient.isError(resp.getCode())) {
                    // Getting label of period and dataset
                    String jsonDatasetInfo = PrefUtils.getOfflineReportInfo(context, reportFile.getName());
                    DatasetInfoHolder info = gson.fromJson(jsonDatasetInfo, DatasetInfoHolder.class);

                    String description;
                    if (ImportSummariesHandler.isSuccess(resp.getBody())) {
                        description = ImportSummariesHandler.getDescription(resp.getBody(),
                                context.getString(R.string.import_successfully_completed));
                    } else {
                        description = ImportSummariesHandler.getDescription(resp.getBody(),
                                context.getString(R.string.import_failed));
                    }
                    String message = String.format("(%s) %s", info.getPeriodLabel(), info.getFormLabel());
                    String title = description;

                    // Firing notification to statusbar
                    NotificationBuilder.fireNotification(context, title, message);

                    // Removing uploaded data
                    TextFileUtils.removeFile(reportFile);
                    PrefUtils.removeOfflineReportInfo(context, reportFile.getName());
                }
            }
        }
    }
}