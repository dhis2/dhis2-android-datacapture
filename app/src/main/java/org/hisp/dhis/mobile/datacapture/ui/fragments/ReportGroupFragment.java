package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.io.AbsCursorLoader;
import org.hisp.dhis.mobile.datacapture.ui.adapters.FieldAdapter;
import org.hisp.dhis.mobile.datacapture.ui.adapters.rows.Row;

import java.util.ArrayList;
import java.util.List;

public class ReportGroupFragment extends Fragment {
    private ListView mListView;
    private FieldAdapter mAdapter;

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

    static class FieldsLoader extends AbsCursorLoader<FieldsHolder> {

        public FieldsLoader(Context context) {
            super(context);
        }

        @Override
        protected FieldsHolder readDataFromCursor(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                List<DBItemHolder<Field>> fields = new ArrayList<>();
                do {

                } while(cursor.moveToNext());
            }
            return null;
        }
    }

    static class FieldsHolder {
        public final List<DBItemHolder<Field>> fields;
        public final List<Row> mRows;

        FieldsHolder(List<DBItemHolder<Field>> fields, List<Row> mRows) {
            this.fields = fields;
            this.mRows = mRows;
        }
    }
}