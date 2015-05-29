package org.dhis2.mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.dhis2.mobile.R;
import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.ui.views.CardTextViewButton;

import java.util.List;

public final class CategoryAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<Category> mCategories;

    public CategoryAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        if (mCategories != null) {
            return mCategories.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mCategories != null) {
            return mCategories.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.listview_card_text_view_button,
                    parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Category category = mCategories.get(position);
        holder.cardTextViewButton.setHint(category.getDisplayName());

        return view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void swapData(List<Category> categories) {
        boolean notifyAdapter = mCategories != categories;
        mCategories = categories;

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        final CardTextViewButton cardTextViewButton;

        public ViewHolder(View itemView) {
            cardTextViewButton = (CardTextViewButton) itemView
                    .findViewById(R.id.category_option_picker);
        }
    }
}
