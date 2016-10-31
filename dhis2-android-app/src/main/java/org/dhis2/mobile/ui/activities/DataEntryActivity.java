package org.dhis2.mobile.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.io.Constants;
import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.network.HTTPClient;
import org.dhis2.mobile.network.NetworkUtils;
import org.dhis2.mobile.network.Response;
import org.dhis2.mobile.processors.CompulsoryDataProcessor;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.PosOrZeroIntegerRow2;
import org.dhis2.mobile.ui.fragments.AdditionalDiseasesFragment;
import org.dhis2.mobile.utils.IsDisabled;
import org.dhis2.mobile.utils.PrefUtils;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.ToastManager;
import org.dhis2.mobile.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class DataEntryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Form> {
    public static final String TAG = DataEntryActivity.class.getSimpleName();

    // state keys
    private static final String STATE_REPORT = "state:report";
    private static final String STATE_DOWNLOAD_ATTEMPTED = "state:downloadAttempted";
    private static final String STATE_DOWNLOAD_IN_PROGRESS = "state:downloadInProgress";

    private String compulsoryData;

    // loader ids
    private static final int LOADER_FORM_ID = 896927645;

    // views
    private View uploadButton;
    private View addDiseaseButton;
    private View persistentButtonsFooter;
    private RelativeLayout progressBarLayout;
    private AppCompatSpinner formGroupSpinner;

    // data entry view
    private ListView dataEntryListView;
    public List<FieldAdapter> adapters;

    // state
    private boolean downloadAttempted;

    //info
    private static DatasetInfoHolder infoHolder;

    //delete disease alert dialog
    private AlertDialog deleteDiseaseDialog;
    //compulsory disease alert dialog;
    private AlertDialog compulsoryDataDialog;

    // key for additional diseases that have been displayed on the list.
    public static final String ALREADY_DISPLAYED = "alreadyDisplayed";

    private Map additionalDiseaseIds = new HashMap();

    private EditText commentField;
    private IsDisabled isDisabled;

    public static void navigateTo(Activity activity, DatasetInfoHolder info) {
        if (info != null && activity != null) {
            infoHolder = info;
            Intent intent = new Intent(activity, DataEntryActivity.class);
            intent.putExtra(DatasetInfoHolder.TAG, info);



            activity.startActivity(intent);
            activity.overridePendingTransition(
                    R.anim.slide_up, R.anim.activity_open_exit);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);


        setupToolbar();
        setupFormSpinner();
        setupProgressBar(savedInstanceState);

        setupListView();
        persistentButtonsFooter = findViewById(R.id.persistent_buttons_footer);
        setupUploadButton();
        setupAddDiseaseBtn();
        setupDeleteDialog();
        setupCompulsoryFieldsDialog();
        isDisabled = new IsDisabled(getApplicationContext());

        // let's try to get latest values from API
        attemptToDownloadReport(savedInstanceState);

        // if we are downloading values, build form
        buildReportDataEntryForm(savedInstanceState);


    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(RECEIVER);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(RECEIVER, new IntentFilter(TAG));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapters != null) {
            ArrayList<Group> groups = new ArrayList<>();
            for (FieldAdapter adapter : adapters) {
                groups.add(adapter.getGroup());
            }

            outState.putParcelableArrayList(STATE_REPORT, groups);
            outState.putBoolean(STATE_DOWNLOAD_ATTEMPTED, downloadAttempted);
            outState.putBoolean(STATE_DOWNLOAD_IN_PROGRESS, isProgressBarVisible());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_down);
    }

    @Override
    public Loader<Form> onCreateLoader(int id, Bundle args) {
        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        if (id == LOADER_FORM_ID && info != null) {
            return new DataLoader(DataEntryActivity.this, info);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Form> loader, Form form) {
        if (loader != null && loader.getId() == LOADER_FORM_ID) {
            loadGroupsIntoAdapters(form.getGroups());
        }
    }

    @Override
    public void onLoaderReset(Loader<Form> loader) {
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setTitle(infoHolder.getOrgUnitLabel());
            String weekNumber = getString(R.string.toolbar_week_prefix) +" "+ infoHolder.getPeriodLabel().substring(1,3);
            toolbar.setSubtitle(weekNumber);
        }
    }

    private void setupFormSpinner() {
        formGroupSpinner = (AppCompatSpinner) findViewById(R.id.spinner_drop_down);

        if (formGroupSpinner != null) {
            formGroupSpinner.setVisibility(View.GONE);
        }
    }

    private void setupProgressBar(Bundle savedInstanceState) {
        progressBarLayout = (RelativeLayout) findViewById(
                R.id.relativelayout_progress_bar);

        if (savedInstanceState != null) {
            boolean downloadInProgress = savedInstanceState
                    .getBoolean(STATE_DOWNLOAD_IN_PROGRESS, false);

            if (downloadInProgress) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        } else {
            hideProgressBar();
        }
    }

    private void setupListView() {
        dataEntryListView = (ListView) findViewById(R.id.list_of_fields);
    }

    private void setupUploadButton() {
        uploadButton = findViewById(R.id.send_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upload();
            }
        });
    }

    private void setupAddDiseaseBtn(){
        addDiseaseButton = findViewById(R.id.add_button);
        addDiseaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdditionalDiseasesFragment additionalDiseasesFragment = new AdditionalDiseasesFragment();
                Bundle args = new Bundle();
                args.putString(ALREADY_DISPLAYED, additionalDiseaseIds.keySet().toString());
                additionalDiseasesFragment.setArguments(args);
                additionalDiseasesFragment.show(getSupportFragmentManager(), TAG);

            }
        });
    }

    private void setupDeleteDialog(){
        deleteDiseaseDialog =  new AlertDialog.Builder(this).create();
    }

    private void showDeleteDiseaseDialog(final String tag, final int pos){
        String title = getResources().getString(R.string.delete_disease_dialog_title);
        String confirmationText = getResources().getString(R.string.delete_disease_dialog_confirmation);
        String rejectionText = getResources().getString(R.string.delete_disease_dialog_rejection);
        deleteDiseaseDialog.setTitle(title);
        deleteDiseaseDialog.setMessage("Are you sure you want to delete "+
                additionalDiseaseIds.get(tag).toString().split("EIDSR-")[1]+" and all its values");
        deleteDiseaseDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirmationText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                additionalDiseaseIds.remove(tag);
                adapters.get(0).removeItemAtPosition(pos);
                deleteDiseaseDialog.dismiss();
            }
        });
        deleteDiseaseDialog.setButton(DialogInterface.BUTTON_NEGATIVE, rejectionText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDiseaseDialog.dismiss();
            }
        });
        deleteDiseaseDialog.setCanceledOnTouchOutside(false);
        deleteDiseaseDialog.show();
    }

    private void setupCompulsoryFieldsDialog(){
        compulsoryDataDialog = new AlertDialog.Builder(this).create();
    }

    private void showCompulsoryFieldsDialog(){
        compulsoryDataDialog.setTitle(getResources().getString(R.string.compulsory_data_dialog_title));
        compulsoryDataDialog.setMessage(getResources().getString(R.string.compulsory_data_dialog_message));
        compulsoryDataDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.compulsory_data_dialog_confirmation),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                compulsoryDataDialog.dismiss();
            }
        });
        compulsoryDataDialog.setCanceledOnTouchOutside(false);
        compulsoryDataDialog.show();
    }

    private void attemptToDownloadReport(Bundle savedInstanceState) {
        // first, we need to check if previous instances of
        // activities already tried to download values
        if (savedInstanceState != null) {
            downloadAttempted = savedInstanceState
                    .getBoolean(STATE_DOWNLOAD_ATTEMPTED, false);
        }

        if (!downloadAttempted && !isProgressBarVisible()) {
            downloadAttempted = true;

            // we need to check if connection is there first
            if (NetworkUtils.checkConnection(this)) {
                getLatestValues();
                getCompulsoryData();
            }else{
                compulsoryData = PrefUtils.getCompulsoryData(getApplicationContext(), infoHolder.getFormId());
            }
        }
    }

    private void buildReportDataEntryForm(Bundle savedInstanceState) {
        if (!isProgressBarVisible()) {
            List<Group> dataEntryGroups = null;

            if (savedInstanceState != null &&
                    savedInstanceState.containsKey(STATE_REPORT)) {
                dataEntryGroups = savedInstanceState
                        .getParcelableArrayList(STATE_REPORT);
            }

            // we did not load form before,
            // so we need to do so now
            if (dataEntryGroups == null) {
                getSupportLoaderManager().restartLoader(LOADER_FORM_ID, null, this).forceLoad();
            } else {
                loadGroupsIntoAdapters(dataEntryGroups);
            }
        }
    }

    private void showProgressBar() {
        ViewUtils.hideAndDisableViews(persistentButtonsFooter, uploadButton, dataEntryListView);
        ViewUtils.enableViews(progressBarLayout);
    }

    private void hideProgressBar() {
        ViewUtils.enableViews(persistentButtonsFooter, uploadButton, dataEntryListView);
        ViewUtils.hideAndDisableViews(progressBarLayout);
    }

    private boolean isProgressBarVisible() {
        return progressBarLayout.getVisibility() == View.VISIBLE;
    }

    private void loadGroupsIntoAdapters(List<Group> groups) {
        if (groups != null) {
            List<FieldAdapter> adapters = new ArrayList<>();

            try {
                for (Group group : groups) {
                    adapters.add(new FieldAdapter(group, this));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }



            setupAdapters(adapters);
        }
    }

    private void setupAdapters(final List<FieldAdapter> adapters) {
        this.adapters = adapters;

        if (adapters.size() == 1) {
            formGroupSpinner.setVisibility(View.GONE);
            dataEntryListView.setAdapter(adapters.get(0));
            return;
        }

        List<String> formGroupLabels = new ArrayList<>();
        for (FieldAdapter fieldAdapter : adapters) {
            formGroupLabels.add(fieldAdapter.getLabel());
        }

        Log.i("Forms", formGroupLabels.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                R.layout.spinner_item, formGroupLabels);
        adapter.setDropDownViewResource(R.layout.dropdown_spinner_item);

        formGroupSpinner.setVisibility(View.VISIBLE);
        formGroupSpinner.setAdapter(adapter);
        formGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataEntryListView.setAdapter(adapters.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // stub implementation
            }
        });
    }

    private void upload() {
        if (adapters == null) {
            ToastManager.makeToast(this, getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
            return;
        }



            ArrayList<Group> groups = new ArrayList<>();
            for (FieldAdapter adapter : adapters) {
                groups.add(adapter.getGroup());
            }

            //Add the comment in list view footer to group data.
            addFooterCommentToGroup(groups.get(0));

            DatasetInfoHolder info = getIntent().getExtras()
                    .getParcelable(DatasetInfoHolder.TAG);

        Intent intent = new Intent(this, WorkService.class);
        //Check if network is available. If not send via sms or else just upload via internet
        if(!NetworkUtils.checkConnection(getApplicationContext())){
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_SEND_VIA_SMS);
        }else {
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_DATASET);
        }
        intent.putExtra(DatasetInfoHolder.TAG, info);
        intent.putExtra(Group.TAG, groups);

        if(isInvalidForm(groups)){
            showCompulsoryFieldsDialog();
        }else {
            startService(intent);
            finish();
        }


    }

    private Boolean isInvalidForm(ArrayList<Group> groups){
        for(Group group: groups){
            for(Field field : group.getFields()){
                if(compulsoryData != null && compulsoryData.contains(field.getDataElement()) && !isDisabled.check(field)
                        && field.getValue().equals("")){
                    return true;
                }
            }
        }
        return false;
    }

    private void getLatestValues() {
        // this should be one operation (instead of two)
        showProgressBar();

        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD,
                WorkService.METHOD_DOWNLOAD_LATEST_DATASET_VALUES);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        startService(intent);
    }

    private void getCompulsoryData(){
        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);
        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD, WorkService.METHOD_DOWNLOAD_COMPULSORY_DATA);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        startService(intent);
    }

    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cxt, Intent intent) {
            hideProgressBar();

            //check if intent has a server response code. This would mean the WorkService has finished making its API call
            if(intent.getExtras().containsKey(Response.CODE)) {
                int code = intent.getExtras().getInt(Response.CODE);
                if (HTTPClient.isError(code)) {
                    // load form from disk
                    getSupportLoaderManager().restartLoader(LOADER_FORM_ID, null,
                            DataEntryActivity.this).forceLoad();
                    return;
                }

                if (intent.getExtras().containsKey(Response.BODY)) {
                    Form form = intent.getExtras().getParcelable(Response.BODY);

                    if (form != null) {
                        loadGroupsIntoAdapters(form.getGroups());
                        setupCommentRowAsFooter(form);
                    }
                }

                if(intent.getExtras().containsKey(CompulsoryDataProcessor.COMPULSORY_DATA)){
                    compulsoryData = intent.getExtras().getString(CompulsoryDataProcessor.COMPULSORY_DATA);
                }
            }
            //if not check if intent was called from a row in the listView. Meaning that the delete button was clicked.
            else if(intent.getExtras().containsKey(PosOrZeroIntegerRow2.TAG)){
                //Get tag (unique ID belonging to every row)
                String tag = intent.getExtras().getString(PosOrZeroIntegerRow2.TAG);
                ///get the view (Row) based on the TAG (unique ID)
                View view = dataEntryListView.findViewWithTag(tag);
                //Get the position of that view (Row) in the listView.
                int position = dataEntryListView.getPositionForView(view);
                showDeleteDiseaseDialog(tag, position);

            }
        }
    };

    private static class DataLoader extends AsyncTaskLoader<Form> {
        private final DatasetInfoHolder infoHolder;

        public DataLoader(Context context, DatasetInfoHolder infoHolder) {
            super(context);
            this.infoHolder = infoHolder;
        }

        @Override
        public Form loadInBackground() {
            if (infoHolder.getFormId() != null && TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.DATASETS, infoHolder.getFormId())) {
                Form form = loadForm();

                // try to fit values
                // from storage into form
                loadValuesIntoForm(form);

                return form;
            }
            return null;
        }

        private Form loadForm() {
            String jForm = TextFileUtils.readTextFile(
                    getContext(), TextFileUtils.Directory.DATASETS, infoHolder.getFormId());
            try {
                JsonObject jsonForm = JsonHandler.buildJsonObject(jForm);
                return JsonHandler.fromJson(jsonForm, Form.class);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void loadValuesIntoForm(Form form) {
            if (form == null || form.getGroups() == null || form.getGroups().isEmpty()) {
                return;
            }

            String reportKey = DatasetInfoHolder.buildKey(infoHolder);
            if (isEmpty(reportKey)) {
                return;
            }

            String report = loadReport(reportKey);
            if (isEmpty(report)) {
                return;
            }

            Map<String, String> fieldMap = new HashMap<>();

            try {
                JsonObject jsonReport = JsonHandler.buildJsonObject(report);
                JsonArray jsonElements = jsonReport.getAsJsonArray(Constants.DATA_VALUES);

                fieldMap = buildFieldMap(jsonElements);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            if (!fieldMap.keySet().isEmpty()) {
                // fill form with values

                for (Group group : form.getGroups()) {
                    if (group.getFields() == null || group.getFields().isEmpty()) {
                        continue;
                    }

                    for (Field field : group.getFields()) {
                        String key = buildFieldKey(field.getDataElement(),
                                field.getCategoryOptionCombo());

                        String value = fieldMap.get(key);
                        if (!isEmpty(value)) {
                            field.setValue(value);
                        }
                    }
                }
            }
        }

        private String loadReport(String reportKey) {
            if (isEmpty(reportKey)) {
                return null;
            }

            if (TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.OFFLINE_DATASETS, reportKey)) {
                String report = TextFileUtils.readTextFile(
                        getContext(), TextFileUtils.Directory.OFFLINE_DATASETS, reportKey);

                if (!isEmpty(report)) {
                    return report;
                }
            }

            return null;
        }

        private Map<String, String> buildFieldMap(JsonArray jsonFields) {
            Map<String, String> fieldMap = new HashMap<>();
            if (jsonFields == null) {
                return fieldMap;
            }

            for (JsonElement jsonElement : jsonFields) {
                if (jsonElement instanceof JsonObject) {
                    JsonElement jsonDataElement = (jsonElement.getAsJsonObject())
                            .get(Field.DATA_ELEMENT);
                    JsonElement jsonCategoryCombination = (jsonElement.getAsJsonObject())
                            .get(Field.CATEGORY_OPTION_COMBO);
                    JsonElement jsonValue = (jsonElement.getAsJsonObject())
                            .get(Field.VALUE);

                    String fieldKey = buildFieldKey(jsonDataElement.getAsString(),
                            jsonCategoryCombination.getAsString());
                    String value = jsonValue != null ? jsonValue.getAsString() : "";

                    fieldMap.put(fieldKey, value);
                }
            }

            return fieldMap;
        }

        private String buildFieldKey(String dataElement, String categoryOptionCombination) {
            if (!isEmpty(dataElement) && !isEmpty(categoryOptionCombination)) {
                return String.format(Locale.getDefault(), "%s.%s",
                        dataElement, categoryOptionCombination);
            }

            return null;
        }
    }
    public void addToDiseasesShown(String id, String label ){
        this.additionalDiseaseIds.put(id, label);
    }

    public void scrollToBottomOfListView(){
        this.dataEntryListView.smoothScrollToPosition(adapters.get(0).getCount());
    }

    private void setupCommentRowAsFooter(Form form){
        String backgroundColor = "#E0ECEA";
        final String title = "Comment";
        for(Group group : form.getGroups()){
            for(Field field: group.getFields()){
                if(field.getDataElement().equals(Constants.COMMENT_FIELD)){
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.listview_row_long_text, dataEntryListView, false);
                    footer.setBackgroundColor(Color.parseColor(backgroundColor));
                    dataEntryListView.addFooterView(footer, null, false);
                    TextView label = (TextView) footer.findViewById(R.id.text_label);
                    label.setText(title);
                    commentField = (EditText) findViewById(R.id.edit_long_text_row);
                    commentField.setText(field.getValue());
                }
            }
        }
    }

    private void addFooterCommentToGroup(Group group){
        Field comment = new Field();
        if(commentField != null) {
            comment.setDataElement(Constants.COMMENT_FIELD);
            comment.setValue(commentField.getText().toString());
            dataEntryListView.findViewById(R.id.edit_long_text_row);
            group.addField(comment);
        }
    }
}
