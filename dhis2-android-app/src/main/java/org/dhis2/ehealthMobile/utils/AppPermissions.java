package org.dhis2.ehealthMobile.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.dhis2.ehealthMobile.R;

/**
 * Created by george on 12/2/16.
 */

public class AppPermissions {
    public static final int MY_PERMISSIONS_SEND_SMS = 1;

    public static int checkSMSPermission(Context context){
        return  ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS);
    }

    public static boolean isSMSPermissionGranted(Context context){
        return PackageManager.PERMISSION_GRANTED == checkSMSPermission(context);
    }

    public static void showSMSPermissionExplanationDialog(Context context, final Activity activity){
        String title = context.getString(R.string.sms_permission_dialog_title);
        String message = context.getString(R.string.sms_permission_dialog_message);
        String confirmationText = context.getString(R.string.sms_permission_dialog_confirmation);

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, confirmationText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestSMSPermission(activity);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void requestSMSPermission(Activity activity) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_SEND_SMS);
    }
}
