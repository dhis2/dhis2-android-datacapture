package org.dhis2.mobile.ui.adapters;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.dhis2.mobile.R;
import org.dhis2.mobile.sdk.entities.Category;
import org.dhis2.mobile.ui.fragments.AutoCompleteDialogFragment;
import org.dhis2.mobile.ui.fragments.aggregate.CategoryDialogFragment;
import org.dhis2.mobile.ui.views.CardTextViewButton;

import java.util.List;

public final class CategoryAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final FragmentManager mManager;
    private List<Category> mCategories;

    public CategoryAdapter(LayoutInflater inflater, FragmentManager manager) {
        mInflater = inflater;
        mManager = manager;
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
            view = mInflater
                    .inflate(R.layout.listview_card_text_view_button, parent, false);
            holder = new ViewHolder(view, mManager);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Category category = mCategories.get(position);
        holder.clickListener.setCategory(category);
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
        final OnButtonClickListener clickListener;

        public ViewHolder(View itemView, FragmentManager fragmentManager) {
            cardTextViewButton = (CardTextViewButton) itemView
                    .findViewById(R.id.category_option_picker);
            OnCategoryOptionSelectedListener onOptionSelectedListener
                    = new OnCategoryOptionSelectedListener(cardTextViewButton);
            clickListener = new OnButtonClickListener(fragmentManager,
                    onOptionSelectedListener);
            cardTextViewButton.setOnClickListener(clickListener);
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        private final FragmentManager mManager;
        private final OnCategoryOptionSelectedListener mListener;
        private Category mCategory;

        public OnButtonClickListener(FragmentManager manager,
                                     OnCategoryOptionSelectedListener listener) {
            mManager = manager;
            mListener = listener;
        }

        public void setCategory(Category category) {
            mCategory = category;
        }

        @Override
        public void onClick(View v) {
            CategoryDialogFragment dialogFragment = CategoryDialogFragment
                    .newInstance(mListener, mCategory.getId());
            dialogFragment.setDialogTitle(mCategory.getDisplayName());
            dialogFragment.show(mManager);
        }
    }

    private static class OnCategoryOptionSelectedListener
            implements AutoCompleteDialogFragment.OnOptionSelectedListener {
        private final CardTextViewButton mButton;

        public OnCategoryOptionSelectedListener(CardTextViewButton button) {
            mButton = button;
        }

        @Override
        public void onOptionSelected(int dialogId, int position,
                                     String id, String name, String blob) {
            mButton.setText(name);
        }
    }
}
