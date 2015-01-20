package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;

import java.util.List;

public class SimpleAdapter extends BaseAdapter {
    private List<String> mItems;
	private LayoutInflater mInflater;

	public SimpleAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextViewHolder holder;
		View view;

		if (convertView == null) {
			View root = mInflater.inflate(R.layout.dialog_fragment_listview_item, parent, false);
			TextView textView = (TextView) root.findViewById(R.id.textview_item);

			holder = new TextViewHolder(textView);
			root.setTag(holder);
			view = root;
		} else {
			view = convertView;
			holder = (TextViewHolder) view.getTag();
		}

		holder.textView.setText(mItems.get(position));
		return view;
	}

	@Override
	public int getCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
	}

	@Override
	public Object getItem(int pos) {
        if (mItems != null && mItems.size() > 0) {
            return mItems.get(pos);
        } else {
            return 0;
        }
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

    public void swapData(List<String> items) {
        if (mItems != items) {
            mItems = items;
            notifyDataSetChanged();
        }
    }

	private class TextViewHolder {
		final TextView textView;

		public TextViewHolder(TextView textView) {
			this.textView = textView;
		}
	}
}