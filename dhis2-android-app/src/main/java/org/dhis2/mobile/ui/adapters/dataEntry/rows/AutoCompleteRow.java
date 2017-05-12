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

import android.content.Context;
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
import org.dhis2.mobile.ui.activities.DataEntryActivity;
import org.dhis2.mobile.ui.adapters.dataEntry.AutoCompleteAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRow implements Row {
    private AutoCompleteAdapter adapter;
    private LayoutInflater inflater;
    private Field field;
    private OptionSet optionset;

    public AutoCompleteRow(LayoutInflater inflater, Field field, OptionSet optionset, Context context) {
        this.inflater = inflater;
        this.field = field;
        this.optionset = optionset;

        ArrayList<String> options = new ArrayList<String>();
        if (optionset != null && optionset.getOptions() != null) {
            for (Option option : optionset.getOptions()) {
                options.add(option.getName());
            }
        }
        adapter = new AutoCompleteAdapter(context);
        adapter.swapData(options);
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
            OnFocusListener onFocusChangeListener = new OnFocusListener(autoComplete,
                    adapter.getData());
            EditTextWatcher textWatcher = new EditTextWatcher(field);

            autoComplete.setOnFocusChangeListener(onFocusChangeListener);
            autoComplete.addTextChangedListener(textWatcher);

            ImageView showOptions = (ImageView) rootView.findViewById(R.id.showDropDownList);
            DropDownButtonListener listener = new DropDownButtonListener(autoComplete);
            showOptions.setOnClickListener(listener);

            holder = new AutoCompleteRowHolder(textLabel, autoComplete, showOptions,
                    listener, onFocusChangeListener, textWatcher);

            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (AutoCompleteRowHolder) view.getTag();
        }

        holder.textLabel.setText(field.getLabel());

        holder.autoComplete.setAdapter(adapter);
        holder.onFocusListener.setValues(holder.autoComplete, adapter.getData());
        holder.autoComplete.setOnFocusChangeListener(holder.onFocusListener);
        holder.textWatcher.setField(field);
        holder.autoComplete.addTextChangedListener(holder.textWatcher);
        holder.autoComplete.setText(field.getValue());

        holder.listener.setAutoComplete(holder.autoComplete);
        holder.button.setOnClickListener(holder.listener);
        holder.autoComplete.clearFocus();
        holder.autoComplete.setOnEditorActionListener(new DataEntryActivity.CustomOnEditorActionListener());

        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.AUTO_COMPLETE.ordinal();
    }

    private class AutoCompleteRowHolder {
        final TextView textLabel;
        final AutoCompleteTextView autoComplete;
        final ImageView button;
        final DropDownButtonListener listener;
        final OnFocusListener onFocusListener;
        final EditTextWatcher textWatcher;

        AutoCompleteRowHolder(TextView textLabel, AutoCompleteTextView autoComplete,
                              ImageView button, DropDownButtonListener listener,
                              OnFocusListener onFocusListener, EditTextWatcher textWatcher) {

            this.textLabel = textLabel;
            this.autoComplete = autoComplete;
            this.button = button;
            this.listener = listener;
            this.onFocusListener = onFocusListener;
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
                if (!options.contains(choice)) {
                    autoComplete.setText(Field.EMPTY_FIELD);
                }
            }
        }
    }
}