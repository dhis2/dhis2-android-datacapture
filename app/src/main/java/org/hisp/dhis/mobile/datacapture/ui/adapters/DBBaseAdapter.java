package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;

import java.util.List;

public abstract class DBBaseAdapter<T> extends BaseAdapter {
    private List<DBItemHolder<T>> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    public DBBaseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mData != null && mData.size() > position) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        DBItemHolder<T> item = (DBItemHolder<T>) getItem(position);
        if (item != null) {
            return item.getDatabaseId();
        } else {
            return -1;
        }
    }

    public void swapData(List<DBItemHolder<T>> data) {
        if (mData != data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }
}

