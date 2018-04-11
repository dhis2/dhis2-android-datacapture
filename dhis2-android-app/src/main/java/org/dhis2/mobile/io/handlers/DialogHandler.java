package org.dhis2.mobile.io.handlers;

import android.app.Activity;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment;

public class DialogHandler {

    public static void showMessage(final String message, final Activity activity) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                builder.setTitle(
                        activity.getString(R.string.network_error))
                        .setMessage(message)
                        .setNeutralButton(activity.getString(android.R.string.ok), null).create();
                builder.show();
            }
        };
        DataEntryActivity.runInHandler(runnable);
    }

    public static void showMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AggregateReportFragment.getActiveActivity());
                builder.setTitle(
                        AggregateReportFragment.getActiveActivity().getString(R.string.network_error))
                        .setMessage(message)
                        .setNeutralButton(AggregateReportFragment.getActiveActivity().getString(android.R.string.ok), null).create();
                builder.show();
            }
        };
        DataEntryActivity.runInHandler(runnable);
    }
}
