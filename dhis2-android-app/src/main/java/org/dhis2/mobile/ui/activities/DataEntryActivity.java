package org.dhis2.mobile.ui.activities;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.utils.TextFileUtils;
import org.dhis2.mobile.utils.ToastManager;
import org.dhis2.mobile.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataEntryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Form> {
    public static final String TAG = DataEntryActivity.class.getSimpleName();

    // state keys
    private static final String STATE_REPORT = "state:report";
    private static final String STATE_DOWNLOAD_ATTEMPTED = "state:downloadAttempted";
    private static final String STATE_DOWNLOAD_IN_PROGRESS = "state:downloadInProgress";

    // loader ids
    private static final int LOADER_FORM_ID = 896927645;

    // views
    private View uploadButton;
    private RelativeLayout progressBarLayout;
    private AppCompatSpinner formGroupSpinner;
    private Form currentForm;

    // data entry view
    private static ListView dataEntryListView;
    private List<FieldAdapter> adapters;

    // state
    private boolean downloadAttempted;

    public static void navigateTo(Activity activity, DatasetInfoHolder info) {
        if (info != null && activity != null) {
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
        setupUploadButton();

        // let's try to get latest values from API
        attemptToDownloadReport(savedInstanceState);

        // if we are downloading values, build form
        buildReportDataEntryForm(savedInstanceState);
        
        setupKeyboardObserver();
    }

    private void setupKeyboardObserver() {
        final View rootView = findViewById(R.id.data_entry_container);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    rootView.findViewById(R.id.upload_button).setVisibility(View.GONE);
                }
                else {
                    rootView.findViewById(R.id.upload_button).setVisibility(View.VISIBLE);
                }
            }
        });
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
        System.out.println("loader reset");
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setToolbarTitle(String title){
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
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
        dataEntryListView.addFooterView(dataEntryListView.inflate(getApplicationContext(), R.layout.listview_row_footer, null));
    }

    private void setupUploadButton() {
        uploadButton = findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upload();
            }
        });
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
        ViewUtils.hideAndDisableViews(uploadButton, dataEntryListView);
        ViewUtils.enableViews(progressBarLayout);
    }

    private void hideProgressBar() {
        ViewUtils.enableViews(uploadButton, dataEntryListView);
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
            if(adapters.get(0).getLabel()!=null && !adapters.get(0).getLabel().equals(FieldAdapter.FORM_WITHOUT_SECTION)){
                setToolbarTitle(adapters.get(0).getLabel());
            }
            return;
        }

        List<String> formGroupLabels = new ArrayList<>();
        for (FieldAdapter fieldAdapter : adapters) {
            formGroupLabels.add(fieldAdapter.getLabel());
        }

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
        if (currentForm.isFieldCombinationRequired() && !validateFieldsCombined(groups)) {
            ToastManager.makeToast(this, getString(R.string.all_questions_compulsories_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validateFields(groups)){
            ToastManager.makeToast(this, getString(R.string.compulsory_empty_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DatasetInfoHolder info = getIntent().getExtras()
                .getParcelable(DatasetInfoHolder.TAG);

        Intent intent = new Intent(this, WorkService.class);
        intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPLOAD_DATASET);
        intent.putExtra(DatasetInfoHolder.TAG, info);
        intent.putExtra(Group.TAG, groups);

        startService(intent);
        finish();
    }

    private boolean validateFieldsCombined(ArrayList<Group> groups) {
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.getValue() == null || field.getValue().isEmpty()) {
                    for (Field fieldCompare : group.getFields()) {
                        if (field.getDataElement().equals(fieldCompare.getDataElement())
                                && fieldCompare.getValue() != null
                                && !fieldCompare.getValue().isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean validateFields(ArrayList<Group> groups) {
        for (Group group:groups){
            for(Field field:group.getFields()){
                if(field.isCompulsory() && (field.getValue()==null || field.getValue().equals(""))){
                    return false;
                }
            }
        }
        return true;
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

    private final BroadcastReceiver RECEIVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cxt, Intent intent) {
            hideProgressBar();

            int code = intent.getExtras().getInt(Response.CODE);
            int parsingStatusCode = intent.getExtras().getInt(JsonHandler.PARSING_STATUS_CODE);

            if (HTTPClient.isError(code) || parsingStatusCode != JsonHandler.PARSING_OK_CODE) {
                // load form from disk
                Toast.makeText(cxt, cxt.getString(R.string.error_loading_dataset), Toast.LENGTH_SHORT).show();
                getSupportLoaderManager().restartLoader(LOADER_FORM_ID, null,
                        DataEntryActivity.this).forceLoad();
                return;
            }

            if (intent.getExtras().containsKey(Response.BODY)) {
                Form form = intent.getExtras().getParcelable(Response.BODY);
                currentForm = form;

                if (form != null) {
                    loadGroupsIntoAdapters(form.getGroups());
                }
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
    public static class CustomOnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            final TextView view = v;
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                int position=dataEntryListView.getPositionForView(v);
                dataEntryListView.smoothScrollToPosition(position+1);
                dataEntryListView.postDelayed(new Runnable() {
                    public void run() {
                        TextView nextField = (TextView)view.focusSearch(View.FOCUS_DOWN);
                        if(nextField != null) {
                            nextField.requestFocus();
                        }
                    }
                }, 200);
                return true;
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (anyFieldEdited()) {
            showAlertDialogExit();
        } else {
            super.onBackPressed();
        }
    }

    private boolean anyFieldEdited() {
        ArrayList<Group> groups = new ArrayList<>();
        if(adapters==null){
            return false;
        }
        for (FieldAdapter adapter : adapters) {
            groups.add(adapter.getGroup());
        }
        for (Group group : groups) {
            for (Field field : group.getFields()) {
                if (field.isEdited()) {
                    return true;
                }
            }
        }
        return false;
    }


    private void showAlertDialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_exit_survey_title);
        builder.setMessage(R.string.dialog_exit_survey_message);

        builder.setPositiveButton(R.string.dialog_exit_survey_yes,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        builder.setNegativeButton(R.string.dialog_exit_survey_no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
