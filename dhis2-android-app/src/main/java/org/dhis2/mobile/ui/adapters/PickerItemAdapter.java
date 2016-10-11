package org.dhis2.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.models.Filter;
import org.dhis2.mobile.ui.models.Picker;

import java.util.ArrayList;
import java.util.List;

import static org.dhis2.mobile.utils.Preconditions.checkNotNull;

public class PickerItemAdapter extends BaseAdapter {
    // view inflater
    private final LayoutInflater inflater;

    // Adapter data
    private Picker currentPicker;
    private final List<Picker> originalPickers;
    private final List<Picker> filteredPickers;
    private final Context context;

    private OnPickerItemClickListener onPickerItemClickListener;

    public PickerItemAdapter(Context context, Picker picker) {
        this.context = checkNotNull(context, "context must not be null!");
        this.inflater = LayoutInflater.from(context);
        this.currentPicker = checkNotNull(picker, "Picker must not be null");

        this.originalPickers = new ArrayList<>();
        this.filteredPickers = new ArrayList<>();

        // will filter picker items based on filters
        List<Picker> filteredPickerItems = processPickers(picker);

        originalPickers.addAll(filteredPickerItems);
        filteredPickers.addAll(filteredPickerItems);
    }

    public PickerItemAdapter(Context context) {
        this.context = checkNotNull(context, "context must not be null!");
        this.inflater = LayoutInflater.from(context);
        this.originalPickers = new ArrayList<>();
        this.filteredPickers = new ArrayList<>();
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener onPickerItemClickListener) {
        this.onPickerItemClickListener = onPickerItemClickListener;
    }

    public void swapData(Picker picker) {
        currentPicker = picker;
        originalPickers.clear();
        filteredPickers.clear();

        if (picker != null) {
            // we need to pre-filter each picker
            // based on attached Filters

            List<Picker> filteredPickerItems = processPickers(picker);

            originalPickers.addAll(filteredPickerItems);
            filteredPickers.addAll(filteredPickerItems);
        }

        notifyDataSetChanged();
    }

    private List<Picker> processPickers(Picker picker) {
        List<Picker> filteredPickerItems = new ArrayList<>();
        if (picker.getChildren() != null && !picker.getChildren().isEmpty()) {
            for (Picker pickerItem : picker.getChildren()) {
                if (!applyFilters(pickerItem.getFilters())) {
                    filteredPickerItems.add(pickerItem);
                }
            }
        }

        return filteredPickerItems;
    }

    private static boolean applyFilters(List<Filter> filters) {
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                if (filter.apply()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Picker getData() {
        return currentPicker;
    }

    public void filter(String query) {
        filteredPickers.clear();

        query = query.toLowerCase();
        for (Picker picker : originalPickers) {
            if (picker.getName() != null && picker.getName().toLowerCase().contains(query)) {
                filteredPickers.add(picker);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredPickers.size();
    }

    @Override
    public Object getItem(int index) {
        return filteredPickers.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view;
        PickerItemViewHolder viewHolder;

        if (convertView == null) {
            view = inflater.inflate(
                    R.layout.listview_row_picker_item, parent, false);
            viewHolder = new PickerItemViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (PickerItemViewHolder) view.getTag();
        }

        onBindViewHolder(viewHolder, index);
        return view;
    }

    private void onBindViewHolder(PickerItemViewHolder holder, int position) {
        Picker picker = filteredPickers.get(position);

        if (this.currentPicker != null && this.currentPicker.getSelectedChild() != null &&
                picker.equals(this.currentPicker.getSelectedChild())) {
            holder.updateViewHolder(picker, true);
        } else {
            holder.updateViewHolder(picker, false);
        }
    }

    private class PickerItemViewHolder {
        final TextView textViewLabel;
        final OnClickListener onTextViewLabelClickListener;

        public PickerItemViewHolder(View itemView) {
            this.textViewLabel = (TextView) itemView.findViewById(R.id.text_item);
            this.onTextViewLabelClickListener = new OnClickListener();

            // TODO fix the problem with clicks
            // ColorStateList colorStateList = new ColorStateList(
            //        new int[][]{
            //                // for selected state
            //                new int[]{android.R.attr.state_selected},
            //
            //                // default color state
            //                new int[]{}
            //        },
            //        new int[]{
            //                ContextCompat.getColor(context, R.color.dark_navy_blue),
            //                textViewLabel.getCurrentTextColor()
            //        });
            // this.textViewLabel.setTextColor(colorStateList);

            this.textViewLabel.setOnClickListener(onTextViewLabelClickListener);
        }

        public void updateViewHolder(Picker picker, boolean isSelected) {
            textViewLabel.setSelected(isSelected);
            textViewLabel.setText(picker.getName());
            onTextViewLabelClickListener.setPicker(picker);
        }

        private class OnClickListener implements View.OnClickListener {
            private Picker picker;

            public void setPicker(Picker picker) {
                this.picker = picker;
            }

            @Override
            public void onClick(View view) {
                if (onPickerItemClickListener != null) {
                    onPickerItemClickListener.onPickerItemClickListener(picker);
                }
            }
        }
    }
}