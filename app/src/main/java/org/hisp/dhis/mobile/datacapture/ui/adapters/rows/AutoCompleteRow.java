package org.hisp.dhis.mobile.datacapture.ui.adapters.rows;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.Field;
import org.hisp.dhis.mobile.datacapture.api.models.Option;
import org.hisp.dhis.mobile.datacapture.api.models.OptionSet;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRow implements Row {
    private static final String EMPTY_FIELD = "";
    private Field mField;
    private List<String> mOptions;
    private ArrayAdapter<String> mAdapter;
    private OnFieldValueSetListener mListener;

    public AutoCompleteRow(Field field, OptionSet optionset) {
        mField = field;
        mOptions = new ArrayList<>();
        if (optionset != null && optionset.getOptions() != null &&
                optionset.getOptions().size() > 0) {
            for (Option option : optionset.getOptions()) {
                mOptions.add(option.getName());
            }
        }
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        AutoCompleteRowHolder holder;

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(inflater.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, mOptions);
        }

        if (convertView == null) {
            View root = inflater.inflate(
                    R.layout.listview_row_autocomplete, container, false);

            TextView textLabel = (TextView)
                    root.findViewById(R.id.text_label);
            AutoCompleteTextView autoComplete = (AutoCompleteTextView)
                    root.findViewById(R.id.find_option);
            ImageButton showOptions = (ImageButton)
                    root.findViewById(R.id.show_drop_down_list);

            OnFocusListener onFocusChangeListener = new OnFocusListener();
            EditTextWatcher textWatcher = new EditTextWatcher();
            DropDownButtonListener listener = new DropDownButtonListener();

            holder = new AutoCompleteRowHolder(textLabel, autoComplete, showOptions,
                    listener, onFocusChangeListener, textWatcher);

            root.setTag(holder);
            view = root;
        } else {
            view = convertView;
            holder = (AutoCompleteRowHolder) view.getTag();
        }

        holder.updateViews(mField);
        return view;
    }

    @Override
    public void setListener(OnFieldValueSetListener listener) {
        mListener = listener;
    }

    @Override
    public int getViewType() {
        return RowTypes.AUTO_COMPLETE.ordinal();
    }

    private static class EditTextWatcher implements TextWatcher {
        private Field field;

        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public void afterTextChanged(Editable arg) {
            field.setValue(arg.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
        }

    }

    private static class DropDownButtonListener implements OnClickListener {
        private AutoCompleteTextView autoComplete;

        public void setAutoComplete(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }

        @Override
        public void onClick(View v) {
            autoComplete.showDropDown();
        }

    }

    private static class OnFocusListener implements OnFocusChangeListener {
        private AutoCompleteTextView autoComplete;
        private List<String> options;

        public void setAutoComplete(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                String choice = autoComplete.getText().toString();
                if (!options.contains(choice)) {
                    autoComplete.setText(EMPTY_FIELD);
                }
            }
        }
    }

    private class AutoCompleteRowHolder {
        final TextView textLabel;
        final AutoCompleteTextView autoComplete;
        final ImageButton button;
        final DropDownButtonListener listener;
        final OnFocusListener onFocusListener;
        final EditTextWatcher textWatcher;

        public AutoCompleteRowHolder(TextView textLabel, AutoCompleteTextView autoComplete,
                                     ImageButton button, DropDownButtonListener listener,
                                     OnFocusListener onFocusListener, EditTextWatcher textWatcher) {

            this.textLabel = textLabel;
            this.autoComplete = autoComplete;
            this.button = button;
            this.listener = listener;
            this.onFocusListener = onFocusListener;
            this.textWatcher = textWatcher;
        }

        public void updateViews(Field field) {
            textLabel.setText(field.getLabel());

            onFocusListener.setAutoComplete(autoComplete);
            onFocusListener.setOptions(mOptions);
            autoComplete.setAdapter(mAdapter);
            autoComplete.setOnFocusChangeListener(onFocusListener);

            textWatcher.setField(mField);
            autoComplete.addTextChangedListener(textWatcher);
            autoComplete.setText(mField.getValue());

            listener.setAutoComplete(autoComplete);
            button.setOnClickListener(listener);
            autoComplete.clearFocus();
        }
    }
}