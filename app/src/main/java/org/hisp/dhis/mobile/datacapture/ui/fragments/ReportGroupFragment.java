package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.events.FieldValueChangeEvent;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportFieldHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DbRow;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFields;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroups;
import org.hisp.dhis.mobile.datacapture.ui.adapters.FieldAdapter;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.AutoCompleteRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.CheckBoxRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.DatePickerRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.OnFieldValueSetListener;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RadioButtonsRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.Row;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RowTypes;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.ValueEntryViewRow;
import org.hisp.dhis.mobile.datacapture.utils.BusProvider;

import java.util.ArrayList;
import java.util.List;

public class ReportGroupFragment extends Fragment
        implements LoaderCallbacks<CursorHolder<List<Row>>> {
    private static final int LOADER_ID = 438915134;
    private ListView mListView;
    private FieldAdapter mAdapter;

    public static ReportGroupFragment newInstance(int groupId) {
        ReportGroupFragment fragment = new ReportGroupFragment();
        Bundle args = new Bundle();

        args.putInt(ReportGroups.DB_ID, groupId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group_report, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new FieldAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<CursorHolder<List<Row>>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            int groupId = args.getInt(ReportGroups.DB_ID);
            final String SELECTION = ReportFields.GROUP_DB_ID + " = " + "'" + groupId + "'";
            return new FieldsLoader(getActivity(), ReportFields.CONTENT_URI,
                    ReportFieldHandler.PROJECTION, SELECTION, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<List<Row>>> loader,
                               CursorHolder<List<Row>> data) {
        if (loader != null && loader.getId() == LOADER_ID &&
                data != null && data.getData() != null) {
            List<Row> rows = data.getData();
            mAdapter.swapData(rows);
        }
    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<List<Row>>> loader) { }

    static class FieldsLoader extends AbsCursorLoader<List<Row>> {

        public FieldsLoader(Context context, Uri uri, String[] projection,
                            String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected List<Row> readDataFromCursor(Cursor cursor) {
            List<DbRow<Field>> fields = new ArrayList<>();
            List<Row> rows = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    fields.add(ReportFieldHandler.fromCursor(cursor));
                } while (cursor.moveToNext());
            }

            OnFieldValueChangedListener listener = new OnFieldValueChangedListener(getContext());
            for (DbRow<Field> dbItem : fields) {
                Field field = dbItem.getItem();

                Row row = null;
                if (field.getOptionSet() != null) {
                    OptionSet optionSet = readOptionSet(field.getOptionSet());
                    row = new AutoCompleteRow(dbItem, optionSet);
                } else if (RowTypes.TEXT.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.TEXT);
                } else if (RowTypes.LONG_TEXT.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.LONG_TEXT);
                } else if (RowTypes.NUMBER.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.NUMBER);
                } else if (RowTypes.INTEGER.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER);
                } else if (RowTypes.INTEGER_NEGATIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_NEGATIVE);
                } else if (RowTypes.INTEGER_ZERO_OR_POSITIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_ZERO_OR_POSITIVE);
                } else if (RowTypes.INTEGER_POSITIVE.name().equals(field.getType())) {
                    row = new ValueEntryViewRow(dbItem, RowTypes.INTEGER_POSITIVE);
                } else if (RowTypes.BOOLEAN.name().equals(field.getType())) {
                    row = new RadioButtonsRow(dbItem, RowTypes.BOOLEAN);
                } else if (RowTypes.GENDER.name().equals(field.getType())) {
                    row = new RadioButtonsRow(dbItem, RowTypes.GENDER);
                } else if (RowTypes.TRUE_ONLY.name().equals(field.getType())) {
                    row = new CheckBoxRow(dbItem);
                } else if (RowTypes.DATE.name().equals(field.getType())) {
                    row = new DatePickerRow(dbItem);
                }

                if (row != null) {
                    row.setListener(listener);
                    rows.add(row);
                }
            }

            return rows;
        }

        private OptionSet readOptionSet(String optionSetId) {
            /* final String SELECTION = KeyValues.KEY + " = " + "'" + optionSetId + "'" + " AND " +
                    KeyValues.TYPE + " = " + "'" + KeyValue.Type.DATASET_OPTION_SET.toString() + "'";
            Cursor cursor = getContext().getContentResolver().query(
                    KeyValues.CONTENT_URI, KeyValueHandler.PROJECTION, SELECTION, null, null
            );

            OptionSet optionSet = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                DbRow<KeyValue> dbItem = KeyValueHandler.fromCursor(cursor);
                cursor.close();

                if (dbItem != null && dbItem.getItem() != null &&
                        dbItem.getItem().getValue() != null) {
                    Gson gson = new Gson();
                    String jOptionSet = dbItem.getItem().getValue();
                    optionSet = gson.fromJson(jOptionSet, OptionSet.class);
                }
            } */

            //return optionSet;
            return null;
        }
    }

    private static class OnFieldValueChangedListener implements OnFieldValueSetListener {
        private Context context;

        public OnFieldValueChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public void onFieldValueSet(int fieldId, String value) {
            FieldValueChangeEvent event = new FieldValueChangeEvent();
            event.setFieldId(fieldId);
            event.setValue(value);
            BusProvider.getInstance().post(event);
            Toast.makeText(context, "posting event", Toast.LENGTH_SHORT).show();
        }
    }
}