/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.ui.adapters.dataEntry.rows;

import static android.text.TextUtils.isEmpty;

import static org.dhis2.mobile.io.models.Field.EMPTY_FIELD;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Option;
import org.dhis2.mobile.io.models.OptionSet;
import org.dhis2.mobile.ui.adapters.dataEntry.AutoCompleteAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AutoCompleteRow extends EditTextRow implements Row {
    private AutoCompleteAdapter adapter;
    private LayoutInflater inflater;
    private Field field;
    private OptionSet optionset;
    private Context mContext;
    public boolean readOnly = false;

    private final Map<String, String> mCodeToNameMap;
    private final Map<String, String> mNameToCodeMap;
    private final ArrayList<String> mOptions;

    public AutoCompleteRow(LayoutInflater inflater, Field field, OptionSet optionset, Context context) {
        this.inflater = inflater;
        this.field = field;
        this.optionset = optionset;
        mContext = context;
        mCodeToNameMap = new LinkedHashMap<>();
        mNameToCodeMap = new LinkedHashMap<>();
        if (optionset.getOptions() != null) {
            for (Option option : optionset.getOptions()) {
                mCodeToNameMap.put(option.getCode(), option.getName());
                mNameToCodeMap.put(option.getName(), option.getCode());
            }
        }
        mOptions = new ArrayList<>(mNameToCodeMap.keySet());
        loadOptions();
    }

    @Override
    public View getView(View convertView) {
        View view;
        AutoCompleteRowHolder holder;

        // List<String> options = optionset.getOptions();

        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_autocomplete, null);

            TextView textLabel = (TextView)
                    rootView.findViewById(R.id.text_label);
            AutoCompleteTextView autoComplete = (AutoCompleteTextView)
                    rootView.findViewById(R.id.chooseOption);
            AutoCompleteEditTextWatcher textWatcher = new AutoCompleteEditTextWatcher(field);

            autoComplete.addTextChangedListener(textWatcher);

            ImageView showOptions = (ImageView) rootView.findViewById(R.id.showDropDownList);
            DropDownButtonListener listener = new DropDownButtonListener(autoComplete);
            showOptions.setOnClickListener(listener);

            holder = new AutoCompleteRowHolder(textLabel, autoComplete, showOptions,
                    listener, textWatcher);

            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (AutoCompleteRowHolder) view.getTag();
        }
        RowCosmetics.setTextLabel(field, holder.textLabel);


        loadOptions();

        String name;
        if (mCodeToNameMap.containsKey(field.getValue())) {
            name = mCodeToNameMap.get(field.getValue());
        } else {
            name = EMPTY_FIELD;
        }
        holder.textWatcher.setOptions(mNameToCodeMap);
        holder.textWatcher.setField(field);
        holder.autoComplete.setText(name);
        holder.autoComplete.setAdapter(adapter);
        holder.autoComplete.addTextChangedListener(holder.textWatcher);

        holder.listener.setAutoComplete(holder.autoComplete);
        holder.button.setOnClickListener(holder.listener);
        holder.autoComplete.clearFocus();
        holder.autoComplete.setOnEditorActionListener(mOnEditorActionListener);
        if(readOnly){
            holder.button.setEnabled(false);
            holder.autoComplete.setEnabled(false);
        } else {
            holder.button.setEnabled(true);
            holder.autoComplete.setEnabled(true);
        }
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.AUTO_COMPLETE.ordinal();
    }

    @Override
    public void setReadOnly(boolean value) {
        readOnly = value;
    }

    private void loadOptions() {
        adapter = new AutoCompleteAdapter(mContext);
        adapter.swapData(mOptions);
    }

    private class AutoCompleteRowHolder {
        final TextView textLabel;
        final AutoCompleteTextView autoComplete;
        final ImageView button;
        final DropDownButtonListener listener;
        final AutoCompleteEditTextWatcher textWatcher;

        AutoCompleteRowHolder(TextView textLabel, AutoCompleteTextView autoComplete,
                              ImageView button, DropDownButtonListener listener, AutoCompleteEditTextWatcher textWatcher) {

            this.textLabel = textLabel;
            this.autoComplete = autoComplete;
            this.button = button;
            this.listener = listener;
            this.textWatcher = textWatcher;
        }
    }

    private class DropDownButtonListener implements OnClickListener {
        private AutoCompleteTextView autoComplete;

        DropDownButtonListener(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }

        void setAutoComplete(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }

        @Override
        public void onClick(View v) {
            autoComplete.showDropDown();
        }

    }

    private class OnFocusListener implements OnFocusChangeListener {
        private AutoCompleteTextView autoComplete;
        private List<String> options;

        public OnFocusListener(AutoCompleteTextView autoComplete, List<String> options) {
            this.autoComplete = autoComplete;
            this.options = options;
        }

        public void setValues(AutoCompleteTextView autoComplete, List<String> options) {
            this.autoComplete = autoComplete;
            this.options = options;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                String choice = autoComplete.getText().toString();
                if (!options.contains(choice) && !choice.equals("")) {
                    autoComplete.setText(EMPTY_FIELD);
                }
            }
        }
    }
    class AutoCompleteEditTextWatcher implements TextWatcher {
        private Field field;
        private Map<String, String> nameToCodeMap;

        AutoCompleteEditTextWatcher(Field field) {
            this.field = field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = s != null ? s.toString() : EMPTY_FIELD;
            String newValue = nameToCodeMap.get(name);
            if(newValue==null){
                return;
            }
            if (isEmpty(newValue)) {
                newValue = EMPTY_FIELD;
            }

            if (!newValue.equals(field.getValue())) {
                field.setValue(newValue);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        public void setOptions(Map<String, String> nameToCodeMap) {
            this.nameToCodeMap = nameToCodeMap;
        }
    }
}