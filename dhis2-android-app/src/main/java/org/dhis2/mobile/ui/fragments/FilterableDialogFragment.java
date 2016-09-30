/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.adapters.OnPickerItemClickListener;
import org.dhis2.mobile.ui.adapters.PickerItemAdapter;
import org.dhis2.mobile.ui.models.Picker;


public class FilterableDialogFragment extends DialogFragment {
    // for fragment manager
    public static final String TAG = FilterableDialogFragment.class.getSimpleName();

    // for arguments bundle
    public static final String ARGS_PICKER = "args:picker";

    private OnPickerItemClickDelegate onPickerItemClickDelegate;

    public static FilterableDialogFragment newInstance(Picker picker) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGS_PICKER, picker);

        FilterableDialogFragment fragment = new FilterableDialogFragment();
        fragment.setArguments(arguments);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    public FilterableDialogFragment() {
        // explicit empty constructor
        onPickerItemClickDelegate = new OnPickerItemClickDelegate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filterable, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Picker picker = null;
        if (getArguments() != null) {
            picker = (Picker) getArguments().getSerializable(ARGS_PICKER);
        }

        if (picker == null) {
            return;
        }

        TextView textViewTitle = (TextView) view
                .findViewById(R.id.textview_titlebar_title);
        if (picker.getHint() != null) {
            textViewTitle.setText(picker.getHint());
        }

        ImageView cancelButton = (ImageView) view
                .findViewById(R.id.imageview_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView recyclerView = (ListView) view
                .findViewById(R.id.listview_picker_items);

        final PickerItemAdapter itemAdapter = new PickerItemAdapter(getActivity(), picker);
        itemAdapter.setOnPickerItemClickListener(onPickerItemClickDelegate);
        recyclerView.setAdapter(itemAdapter);

        EditText filterEditText = (EditText) view
                .findViewById(R.id.edittext_filter_picker_items);
        filterEditText.setHint(R.string.search);
        filterEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                itemAdapter.filter(editable.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // stub implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // stub implementation
            }
        });
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener clickListener) {
        onPickerItemClickDelegate.setOnPickerItemClickListener(clickListener);
    }

    private class OnPickerItemClickDelegate implements OnPickerItemClickListener {
        private OnPickerItemClickListener onPickerItemClickListener;

        @Override
        public void onPickerItemClickListener(Picker selectedPicker) {
            if (onPickerItemClickListener != null) {
                onPickerItemClickListener.onPickerItemClickListener(selectedPicker);
            }

            dismiss();
        }

        public void setOnPickerItemClickListener(OnPickerItemClickListener onItemClickListener) {
            this.onPickerItemClickListener = onItemClickListener;
        }
    }
}
