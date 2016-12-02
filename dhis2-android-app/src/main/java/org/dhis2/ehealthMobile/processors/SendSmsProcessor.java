package org.dhis2.ehealthMobile.processors;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.google.gson.Gson;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.io.Constants;
import org.dhis2.ehealthMobile.io.holders.DatasetInfoHolder;
import org.dhis2.ehealthMobile.io.models.Field;
import org.dhis2.ehealthMobile.io.models.Group;
import org.dhis2.ehealthMobile.utils.IsTimely;
import org.dhis2.ehealthMobile.utils.KeyGenerator;
import org.dhis2.ehealthMobile.utils.NotificationBuilder;
import org.dhis2.ehealthMobile.utils.PrefUtils;
import org.dhis2.ehealthMobile.utils.SMSBroadcastReceiver;
import org.dhis2.ehealthMobile.utils.TextFileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by george on 8/10/16.
 */

/**
 * Sends an SMS. SMS sent is formatted specifically for use with DHIS2's SMS commands.
 */
public class SendSmsProcessor {
    public static final String TAG = SendSmsProcessor.class.getSimpleName();
    private static final String receiptOfFormKey = "rof";
    private static final String dateReceivedKey = "dr";
    private static final String timelyKey = "tr";
    private static final String cmdSeparator = " ";  //SMS command separator
    private static final String kVSeparator = "=";  //Key value separator
    private static final String dVSeparator = "|";  //Data value separator
    private static final String periodFormat = "ddMM";
    public static final String SMS_KEY = "SMSKey";

    public static void send (final Context context, DatasetInfoHolder info, ArrayList<Group> groups){
        String data = prepareContent(info, groups);
        String offlineData = ReportUploadProcessor.prepareContent(info, groups);

        saveDataset(context, offlineData, info);

        String submissionId = info.getFormId()+info.getPeriod();
        if (!hasBeenCompleted(context, submissionId)) {
            sendSMS(context, PrefUtils.getSmsNumber(context), data, info);
        } else {
            String title = context.getString(R.string.form_completion_dialog_title);
            String message = context.getString(R.string.form_completion_message);
            NotificationBuilder.fireNotificationWithReturnDialog(context, title , message );
        }
    }

    private static String prepareContent(DatasetInfoHolder info, ArrayList<Group> submissionData){
        KeyGenerator keyGenerator = new KeyGenerator();
        String commandName = Constants.COMMAND_NAME;

        String message = "";
        message += commandName + cmdSeparator;

        // The date generated here is the current day of the week within the
        // given week number, which DHIS2 seems to be fine with.
        int weekNumber = Integer.parseInt(info.getPeriodLabel().substring(1,3));
        DateTime period = new DateTime().withWeekOfWeekyear(weekNumber);
        DateTimeFormatter format = DateTimeFormat.forPattern(periodFormat);
        message += period.toString(format) + cmdSeparator;

        //TODO: insert org unit

        //This is for the data elements and their values
        for (Group group : submissionData) {
            for (Field field : group.getFields()) {
                if(!field.getValue().equals("")) {
                    message += keyGenerator.generate(field.getDataElement(), field.getCategoryOptionCombo(), 2) + kVSeparator;
                    message += sanitiseValue(field.getValue()) + dVSeparator;
                }
            }
        }

        //Fill out submission method as SMS.
        message += receiptOfFormKey + kVSeparator + Constants.SMS_SUBMISSION;

        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        //Fill out date received method
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);
        message += dVSeparator + dateReceivedKey + kVSeparator + completeDate;

        // Add Timeliness
        Boolean isTimely = IsTimely.check(new DateTime(), info.getPeriod());
        message += dVSeparator + timelyKey + kVSeparator + String.valueOf(isTimely);

        return message;
    }

    /**
     * Ensures none of the dataValues include a separator character
     * @param dataValue String The dataValue to sanitise.
     */
    private static String sanitiseValue(final String dataValue) {
        String sanitisedValue = dataValue;
        sanitisedValue = sanitisedValue.replace(cmdSeparator, "_");
        sanitisedValue = sanitisedValue.replace(dVSeparator, "_");
        sanitisedValue = sanitisedValue.replace(kVSeparator, "_");

        return sanitisedValue;
    }

    /**
     * Sends an SMS
     * @param context Context
     * @param phoneNumber String The phone number the sms should be sent to.
     * @param message String The message that should be sent.
     */
    private static void sendSMS(final Context context, String phoneNumber, String message, DatasetInfoHolder info) {
        String SMSKey = info.getFormId()+info.getPeriod();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        ArrayList<PendingIntent> sentMessagePIs = new ArrayList<>();
        ArrayList<PendingIntent> deliveredMessagePIs = new ArrayList<>();

        final PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SMSBroadcastReceiver.SEND_SMS_ACTION).putExtra(SMS_KEY, SMSKey), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SMSBroadcastReceiver.DELIVERED_SMS_ACTION).putExtra(SMS_KEY, SMSKey), 0);

        for(String msg: parts){
            sentMessagePIs.add(sentPI);
            deliveredMessagePIs.add(deliveredPI);
        }
        PrefUtils.saveSMSStatus(context, SMSKey, "Failed");
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentMessagePIs, deliveredMessagePIs);

    }

    //Saves the dataset locally for future upload.
    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }

    private static Boolean hasBeenCompleted(Context context, String submissionId){
        Boolean hasBeenCompleted = false;
        if(PrefUtils.getCompletionDate(context, submissionId) != null ){
            hasBeenCompleted  = true;
        }

        return hasBeenCompleted;
    }

}
