package org.dhis2.mobile.utils;

import android.content.Context;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.handlers.ImportSummariesHandler;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.Response;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncLogger {

    public static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void log(Context context, Response response,
            DatasetInfoHolder datasetInfoHolder, Boolean isOffline) {

        String description = getResponseDescription(context, response);

        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.LOG,
                TextFileUtils.FileNames.LOG.toString(),
                getLogMessage(context, datasetInfoHolder, isOffline) + " " + description,
                getFormattedDate());
    }

    public static String getResponseDescription(Context context, Response response) {
        String description;
        if (ImportSummariesHandler.isSuccess(response.getBody())) {
            description = ImportSummariesHandler.getDescription(response.getBody(),
                    context.getString(R.string.import_successfully_completed));
        } else {
            description = ImportSummariesHandler.getDescription(response.getBody(),
                    context.getString(R.string.import_failed));
        }

        return description;
    }

    public static void logNetworkError(Context context, Response resp,
            DatasetInfoHolder datasetInfoHolder, Boolean isOffline) {
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.LOG,
                TextFileUtils.FileNames.LOG.toString(),
                getErrorMessage(context, datasetInfoHolder, resp, isOffline),
                getFormattedDate());
    }

    private static String getLogMessage(Context context, DatasetInfoHolder datasetInfoHolder,
            Boolean isOffline) {
        String message = String.format(context.getString(R.string.log_report_data),
                datasetInfoHolder.getFormLabel(), datasetInfoHolder.getPeriodLabel());
        if (isOffline) {
            return message + " " + context.getString(
                    R.string.log_message_offline_report);
        } else {
            return message;
        }

    }

    public static String getErrorMessage(Context context, DatasetInfoHolder datasetInfoHolder,
            Response resp, Boolean isOffline) {
        return getLogMessage(context, datasetInfoHolder, isOffline) + context.getString(
                R.string.network_error) + ": "
                + HTTPClient.getErrorMessage(context, resp.getCode());
    }

    private static String getFormattedDate() {
        return new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date());
    }

    public static String getNotification(DatasetInfoHolder datasetInfoHolder) {
        return String.format("(%s) %s", datasetInfoHolder.getPeriodLabel(), datasetInfoHolder.getFormLabel());
    }
}
