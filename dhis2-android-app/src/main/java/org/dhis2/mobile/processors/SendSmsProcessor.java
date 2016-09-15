package org.dhis2.mobile.processors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.models.CategoryOption;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.utils.KeyGenerator;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 8/10/16.
 */
public class SendSmsProcessor {
    public static final String TAG = SendSmsProcessor.class.getSimpleName();

    public static void send (Context context, DatasetInfoHolder info, ArrayList<Group> groups){
        String data = prepareContent(info, groups, context);
        //insert destination number
        sendSMS(context, Constants.SMS_NUMBER, data);
        if (!NetworkUtils.checkConnection(context)) {
            saveDataset(context, data, info);
            return;
        }


    }
    private static String prepareContent(DatasetInfoHolder info, ArrayList<Group> groups, Context context){
        JsonObject content = new JsonObject();
        KeyGenerator generator = new KeyGenerator();
        Log.d("Generator", generator.parse(groups, "command"));
        JsonArray values = putFieldValuesInJson(groups);

        // Retrieve current date
        LocalDate currentDate = new LocalDate();
        String completeDate = currentDate.toString(Constants.DATE_FORMAT);

        //Retrieve username
        String username = PrefUtils.getUserName(context);

        /**
         Field title names have to be shortened to get more data sent via sms
         Username = uN

         Oragnisation Unit = oU
         Dataset = dSet
         Period = p
         Complete Date = cD
         Data Values = dV
         **/


        content.addProperty("uN", username);
        content.addProperty("oU", info.getOrgUnitId());
        content.addProperty("dSet", info.getFormId());
        content.addProperty("p", info.getPeriod());
        content.addProperty("cD", completeDate);
        content.add("dV", values);

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
                if(!field.getValue().equals("")) {
                    JsonObject jField = new JsonObject();
                    jField.addProperty("De", field.getDataElement());
                    // jField.addProperty(Field.CATEGORY_OPTION_COMBO, field.getCategoryOptionCombo());
                    jField.addProperty("val", field.getValue());
                    jFields.add(jField);
                }
            }
        }
        return jFields;
    }

    private static void sendSMS(final Context context, String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context,"SMS sent!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void saveDataset(Context context, String data, DatasetInfoHolder info) {
        String key = DatasetInfoHolder.buildKey(info);
        Gson gson = new Gson();
        String jsonReportInfo = gson.toJson(info);
        PrefUtils.saveOfflineReportInfo(context, key, jsonReportInfo);
        TextFileUtils.writeTextFile(context, TextFileUtils.Directory.OFFLINE_DATASETS, key, data);
    }

}
