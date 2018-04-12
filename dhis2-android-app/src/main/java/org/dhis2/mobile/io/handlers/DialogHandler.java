package org.dhis2.mobile.io.handlers;

import android.app.Activity;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment;

public class DialogHandler {

    Activity activity;
    String message;

    public DialogHandler(String message){
        this.activity=AggregateReportFragment.getActiveActivity();
        this.message=message;
    }

    public DialogHandler(Activity activity, String message){
        this.activity=activity;
        this.message=message;
    }

    public void showMessage() {
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
}
