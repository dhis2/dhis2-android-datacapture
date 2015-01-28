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

import com.google.gson.Gson;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.KeyValueHandler;
import org.hisp.dhis.mobile.datacapture.api.android.handlers.ReportFieldHandler;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.android.models.KeyValue;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.io.CursorHolder;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportFieldColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.ReportGroupColumns;
import org.hisp.dhis.mobile.datacapture.io.DBContract.KeyValueColumns;
import org.hisp.dhis.mobile.datacapture.ui.adapters.FieldAdapter;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.AutoCompleteRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.CheckBoxRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.DatePickerRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.EditTextRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RadioButtonsRow;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.Row;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.RowTypes;

import java.util.ArrayList;
import java.util.List;

public class ReportGroupFragment extends Fragment
        implements LoaderCallbacks<CursorHolder<ReportGroupFragment.FieldsHolder>> {
    private static final int LOADER_ID = 438915134;
    private ListView mListView;
    private FieldAdapter mAdapter;

    public static ReportGroupFragment newInstance(int groupId) {
        ReportGroupFragment fragment = new ReportGroupFragment();
        Bundle args = new Bundle();

        args.putInt(ReportGroupColumns.DB_ID, groupId);
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
    public Loader<CursorHolder<FieldsHolder>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id) {
            int groupdId = args.getInt(ReportGroupColumns.DB_ID);
            final String SELECTION = ReportFieldColumns.GROUP_DB_ID + " = " + "'" + groupdId + "'";
            return new FieldsLoader(getActivity(), ReportFieldColumns.CONTENT_URI,
                    ReportFieldHandler.PROJECTION, SELECTION, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<CursorHolder<FieldsHolder>> loader,
                               CursorHolder<FieldsHolder> data) {
        if (loader != null && loader.getId() == LOADER_ID &&
                data != null && data.getData() != null) {
            List<DBItemHolder<Field>> fields = data.getData().fields;
            List<Row> rows = data.getData().rows;
            mAdapter.swapData(rows);
        }

    }

    @Override
    public void onLoaderReset(Loader<CursorHolder<FieldsHolder>> loader) { }

    static class FieldsLoader extends AbsCursorLoader<FieldsHolder> {

        public FieldsLoader(Context context, Uri uri, String[] projection,
                            String selection, String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        protected FieldsHolder readDataFromCursor(Cursor cursor) {
            List<DBItemHolder<Field>> fields = new ArrayList<>();
            List<Row> rows = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    fields.add(ReportFieldHandler.fromCursor(cursor));
                } while(cursor.moveToNext());
            }

            for (DBItemHolder<Field> dbItem: fields) {
                Field field = dbItem.getItem();

                if (RowTypes.TEXT.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.TEXT));
                } else if (RowTypes.LONG_TEXT.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.LONG_TEXT));
                } else if (RowTypes.NUMBER.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.NUMBER));
                } else if (RowTypes.INTEGER.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.INTEGER));
                } else if (RowTypes.INTEGER_NEGATIVE.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.INTEGER_NEGATIVE));
                } else if (RowTypes.INTEGER_ZERO_OR_POSITIVE.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.INTEGER_ZERO_OR_POSITIVE));
                } else if (RowTypes.INTEGER_POSITIVE.name().equals(field.getType())) {
                    rows.add(new EditTextRow(field, RowTypes.INTEGER_POSITIVE));
                } else if (RowTypes.BOOLEAN.name().equals(field.getType())) {
                    rows.add(new RadioButtonsRow(field, RowTypes.BOOLEAN));
                } else if (RowTypes.GENDER.name().equals(field.getType())) {
                    rows.add(new RadioButtonsRow(field, RowTypes.GENDER));
                } else if (RowTypes.TRUE_ONLY.name().equals(field.getType())) {
                    rows.add(new CheckBoxRow(field));
                } else if (RowTypes.AUTO_COMPLETE.name().equals(field.getType())) {
                    OptionSet optionSet = readOptionSet(field.getOptionSet());
                    rows.add(new AutoCompleteRow(field, optionSet));
                } else if (RowTypes.DATE.name().equals(field.getType())) {
                    rows.add(new DatePickerRow(field));
                }
            }

            return new FieldsHolder(fields, rows);
        }

        private OptionSet readOptionSet(String optionSetId) {
            final String SELECTION = KeyValueColumns.KEY + " = " + "'" + optionSetId + "'" + " AND " +
                    KeyValueColumns.TYPE + " = " + "'" + KeyValue.Type.DATASET_OPTION_SET.toString() + "'";
            Cursor cursor = getContext().getContentResolver().query(
                    KeyValueColumns.CONTENT_URI, KeyValueHandler.PROJECTION, SELECTION, null, null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                DBItemHolder<KeyValue> dbItem = KeyValueHandler.fromCursor(cursor);
                cursor.close();

                if (dbItem != null && dbItem.getItem() != null &&
                        dbItem.getItem().getValue() != null) {
                    Gson gson = new Gson();
                    String jOptionSet = dbItem.getItem().getValue();
                    return gson.fromJson(jOptionSet, OptionSet.class);
                }
            }

            return null;
        }
    }

    static class FieldsHolder {
        public final List<DBItemHolder<Field>> fields;
        public final List<Row> rows;

        FieldsHolder(List<DBItemHolder<Field>> fields, List<Row> rows) {
            this.fields = fields;
            this.rows = rows;
        }
    }
}