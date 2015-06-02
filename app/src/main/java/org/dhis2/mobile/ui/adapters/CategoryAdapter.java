package org.dhis2.mobile.ui.adapters;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.fragments.AutoCompleteDialogFragment.OnOptionSelectedListener;
import org.dhis2.mobile.ui.fragments.aggregate.AggregateReportFragmentState.CategoryState;
import org.dhis2.mobile.ui.fragments.aggregate.CategoryDialogFragment;
import org.dhis2.mobile.ui.views.CardTextViewButton;

import java.util.List;

public final class CategoryAdapter extends BaseAdapter {
    private static final String EMPTY_FIELD = "";
    private final LayoutInflater mInflater;
    private final FragmentManager mManager;
    private final OnOptionSelectedListener mListener;
    private List<CategoryState> mCategoryStates;

    public CategoryAdapter(LayoutInflater inflater, FragmentManager manager,
                           OnOptionSelectedListener listener) {
        mInflater = inflater;
        mManager = manager;
        mListener = listener;
    }

    @Override
    public int getCount() {
        if (mCategoryStates != null) {
            return mCategoryStates.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mCategoryStates != null) {
            return mCategoryStates.get(position);
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
            view = mInflater.inflate(
                    R.layout.listview_card_text_view_button, parent, false);
            holder = new ViewHolder(view, mManager, mListener);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        CategoryState categoryState = mCategoryStates.get(position);
        String optionName = EMPTY_FIELD;
        if (categoryState.isCategoryOptionSelected()) {
            optionName = categoryState.getCategoryOptionName();
        }

        holder.clickListener.setCategory(categoryState);
        holder.cardTextViewButton.setHint(categoryState.getCategoryName());
        holder.cardTextViewButton.setText(optionName);
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

    public void swapData(List<CategoryState> categories) {
        boolean notifyAdapter = mCategoryStates != categories;
        mCategoryStates = categories;

        if (notifyAdapter) {
            notifyDataSetChanged();
        }
    }

    public List<CategoryState> getData() {
        return mCategoryStates;
    }

    static class ViewHolder {
        final CardTextViewButton cardTextViewButton;
        final OnButtonClickListener clickListener;

        public ViewHolder(View itemView, FragmentManager fragmentManager,
                          OnOptionSelectedListener listener) {
            cardTextViewButton = (CardTextViewButton) itemView
                    .findViewById(R.id.category_option_picker);
            clickListener = new OnButtonClickListener(
                    fragmentManager, listener);
            cardTextViewButton.setOnClickListener(clickListener);
        }
    }

    private static class OnButtonClickListener implements View.OnClickListener {
        private final FragmentManager mManager;
        private final OnOptionSelectedListener mListener;
        private CategoryState mCategoryState;

        public OnButtonClickListener(FragmentManager manager,
                                     OnOptionSelectedListener listener) {
            mManager = manager;
            mListener = listener;
        }

        public void setCategory(CategoryState categoryState) {
            mCategoryState = categoryState;
        }

        @Override
        public void onClick(View v) {
            CategoryDialogFragment dialogFragment = CategoryDialogFragment
                    .newInstance(mListener, mCategoryState.getCategoryId());
            dialogFragment.setDialogTitle(mCategoryState.getCategoryName());
            dialogFragment.show(mManager);
        }
    }
}