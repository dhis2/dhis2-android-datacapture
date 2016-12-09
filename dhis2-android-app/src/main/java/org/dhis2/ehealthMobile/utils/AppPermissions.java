package org.dhis2.ehealthMobile.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.ui.activities.DataEntryActivity;

import java.util.HashMap;

/**
 * Created by george on 12/2/16.
 */

public class AppPermissions {
    public static final int MY_PERMISSIONS = 1;
    public static String[] requiredPermissions = new String[]{Manifest.permission.SEND_SMS};
    private static final String TITLE = "TITLE";
    private static final String MESSAGE = "MESSAGE";
    private static final String CONFIRMATION_TEXT = "CONFIRMATION_TEXT";

    private AppPermissions(){

    }

    public static int checkPermission(Context context, String permission){
        return  ContextCompat.checkSelfPermission(context,
                permission);
    }

    public static boolean isSMSPermissionGranted(Context context){
        return PackageManager.PERMISSION_GRANTED == checkPermission(context, Manifest.permission.SEND_SMS);
    }

    private static void showPermissionRationaleDialog(final Activity activity, HashMap dialogText){
        String title = dialogText.get(TITLE).toString();
        String message = dialogText.get(MESSAGE).toString();
        String confirmationText = dialogText.get(CONFIRMATION_TEXT).toString();

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, confirmationText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission(activity);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void requestPermission(Activity activity ) {
            ActivityCompat.requestPermissions(activity,requiredPermissions,
                    MY_PERMISSIONS);
    }

    public static void handleRequestResults(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity){
        switch (requestCode){
            case AppPermissions.MY_PERMISSIONS:{
                for(String permission: permissions){
                    if(permission.equals(Manifest.permission.SEND_SMS)){
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            //Permission granted ヽ(´▽`)/
                            DataEntryActivity dataEntryActivity = (DataEntryActivity) activity;
                            dataEntryActivity.upload();
                        } else {
                            // permission denied, ¯\_(⊙︿⊙)_/¯
                            //call the upload method again, but this time it'll call the report upload service.
                            //Without an internet connection this will then store the data locally for upload when there is one.
                            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)){
                                Context context = activity.getApplicationContext();
                                HashMap <String, String> dialogText = new HashMap<>();
                                dialogText.put(TITLE, context.getString(R.string.sms_permission_dialog_title));
                                dialogText.put(MESSAGE, context.getString(R.string.sms_permission_dialog_message));
                                dialogText.put(CONFIRMATION_TEXT, context.getString(R.string.sms_permission_dialog_confirmation));
                                AppPermissions.showPermissionRationaleDialog(activity, dialogText);
                            }else {
                                DataEntryActivity dataEntryActivity = (DataEntryActivity) activity;
                                dataEntryActivity.upload();
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean canShowRationale(Activity activity, String permission){
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
}
