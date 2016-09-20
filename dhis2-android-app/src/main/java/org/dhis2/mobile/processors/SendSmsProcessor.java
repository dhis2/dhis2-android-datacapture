package org.dhis2.mobile.processors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.utils.KeyGenerator;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;

/**
 * Created by george on 8/10/16.
 */
public class SendSmsProcessor {
    public static final String TAG = SendSmsProcessor.class.getSimpleName();

    public static void send (Context context, DatasetInfoHolder info, ArrayList<Group> groups){
        String data = prepareContent(groups);
        //insert destination number
        sendSMS(context, Constants.SMS_NUMBER, data);

        saveDataset(context, data, info);

    }
    private static String prepareContent(ArrayList<Group> submissionData){
        KeyGenerator keyGenerator = new KeyGenerator();
        String commandName = "command";

        String message = "";
        message += commandName+" ";
        //TODO: insert period
        //TODO: insert org unit


        //This is for the data elements and their values
        for (Group group : submissionData) {
            for (Field field : group.getFields()) {
                if(!field.getValue().equals("")) {
                    message += keyGenerator.generate(field.getDataElement(), field.getCategoryOptionCombo(), 2)+",";
                    message += field.getValue()+",";

                }
            }
        }

        return message;
    }

    /**
     * Sends an SMS
     * @param context Context
     * @param phoneNumber String The phone number the sms should be sent to.
     * @param message String The message that should be sent.
     */
    private static void sendSMS(final Context context, String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

        //Display a toast when the sms has been sent. A handler is used so as not to run on the main thread.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context,"SMS sent!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Saves the dataset locally for future upload.
    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }

}
