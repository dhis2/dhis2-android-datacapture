package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.hisp.dhis.mobile.datacapture.R;

public class ListSelectorView extends AbsSelectorView {
    private ListView mListView;

    public ListSelectorView(Context context) {
        super(context);
    }

    public ListSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, ViewGroup container) {
        mListView = (ListView) inflater.inflate(R.layout.listview, container, false);
        return mListView;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (mListView != null) {
            mListView.setOnItemClickListener(onItemClickListener);
        }
    }

    public void setListViewAdapter(ListAdapter adapter) {
        if (mListView != null) {
            mListView.setAdapter(adapter);
        }
    }
}
