package org.dhis2.mobile.processors;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.models.CategoryOption;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.utils.IsTimely;
import org.dhis2.mobile.utils.KeyGenerator;
import org.dhis2.mobile.utils.NotificationBuilder;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.SMSBroadcastReceiver;
import org.dhis2.mobile.utils.TextFileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 8/10/16.
 */

/**
 * Sends an SMS. SMS sent is formatted specifically for use with DHIS2's SMS commands.
 */
public class SendSmsProcessor {
    public static final String TAG = SendSmsProcessor.class.getSimpleName();
    private static final String receiptOfFormKey = "rof";
    private static final String separator = "=";
    public static final String SMS_KEY = "SMSKey";

    public static void send (final Context context, DatasetInfoHolder info, ArrayList<Group> groups){
        String data = prepareContent(groups);
        String offlineData = prepareContentForOfflineSave(info, groups);


        saveDataset(context, offlineData, info);

        String key = info.getFormId()+info.getPeriod();
        if(!hasBeenCompleted(context, key)){
            sendSMS(context, Constants.SMS_NUMBER, data, info);
        }else{
            String title = context.getString(R.string.form_completion_dialog_title);
            String message = context.getString(R.string.form_completion_message);
            NotificationBuilder.fireNotificationWithReturnDialog(context, title , message );
        }


    }
    private static String prepareContent(ArrayList<Group> submissionData){
        KeyGenerator keyGenerator = new KeyGenerator();
        String commandName = Constants.COMMAND_NAME;

        String message = "";
        message += commandName+" ";
        //TODO: insert period
        //TODO: insert org unit


        //This is for the data elements and their values
        for (Group group : submissionData) {
            for (Field field : group.getFields()) {
                if(!field.getValue().equals("")) {
                    message += keyGenerator.generate(field.getDataElement(), field.getCategoryOptionCombo(), 2)+ separator;
                    message += field.getValue()+"|";

                }
            }
        }

        //Fill out submission method as SMS.
        message += receiptOfFormKey+ separator +Constants.SMS_SUBMISSION;

        return message;
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

    /**
            * Combines the dataset info and dataElements with their values into one JSON object and then returns it as a string
    * @param info DatasetInfoHolder
    * @param groups ArrayList<Group>
    * @return String
    */

    private static String prepareContentForOfflineSave(DatasetInfoHolder info, ArrayList<Group> groups) {
        JsonObject content = new JsonObject();
        JsonArray values = putFieldValuesInJson(groups);

        //Check whether a timely report has already been sent
        if(!IsTimely.hasBeenSet(groups)) {
            //Check whether the report was timely or not
            //substring is used so as to only get the week number
            String period = info.getPeriod();
            Boolean isTimely = IsTimely.check(new DateTime(), period);

            //Fill out timely dataElement
            JsonObject jField = new JsonObject();
            jField.addProperty(Field.DATA_ELEMENT, Constants.TIMELY);
            jField.addProperty(Field.VALUE, isTimely);
            values.add(jField);
        }

        //Fill out submission method
        JsonObject jField = new JsonObject();
        jField.addProperty(Field.DATA_ELEMENT, Constants.RECEIPT_OF_FORM);
        jField.addProperty(Field.VALUE, Constants.SMS_SUBMISSION);
        values.add(jField);




        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);

        content.addProperty(Constants.ORG_UNIT_ID, info.getOrgUnitId());
        content.addProperty(Constants.DATA_SET_ID, info.getFormId());
        content.addProperty(Constants.PERIOD, info.getPeriod());
        content.addProperty(Constants.COMPLETE_DATE, completeDate);
        content.add(Constants.DATA_VALUES, values);

        JsonArray categoryOptions = putCategoryOptionsInJson(info.getCategoryOptions());
        if (categoryOptions != null) {
            content.add(Constants.ATTRIBUTE_CATEGORY_OPTIONS, categoryOptions);
        }
        return content.toString();
    }

    private static JsonArray putCategoryOptionsInJson(List<CategoryOption> categoryOptions) {
        if (categoryOptions != null && !categoryOptions.isEmpty()) {
            JsonArray jsonOptions = new JsonArray();

            // processing category options
            for (CategoryOption categoryOption : categoryOptions) {
                jsonOptions.add(categoryOption.getId());
            }

            return jsonOptions;
        }

        return null;
    }

    private static JsonArray putFieldValuesInJson(ArrayList<Group> groups) {
        JsonArray jFields = new JsonArray();
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                JsonObject jField = new JsonObject();
                jField.addProperty(Field.DATA_ELEMENT, field.getDataElement());
                jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                jField.addProperty(Field.VALUE, field.getValue());
                jFields.add(jField);
            }
        }
        return jFields;
    }

    //Saves the dataset locally for future upload.
    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }

    private static Boolean hasBeenCompleted(Context context, String info){
        Boolean hasBeenCompleted = false;
        if(PrefUtils.getCompletionDate(context, info) != null ){
            hasBeenCompleted  = true;
        }

        return hasBeenCompleted;
    }

}
