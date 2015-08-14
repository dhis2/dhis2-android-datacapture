/*
 * Copyright (c) 2015, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.datacapture.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.hisp.dhis.android.datacapture.R;
import org.hisp.dhis.android.datacapture.ui.adapters.AutoCompleteDialogAdapter;
import org.hisp.dhis.android.datacapture.ui.adapters.AutoCompleteDialogAdapter.OptionAdapterValue;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class AutoCompleteDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener {
    private static final String TAG = AutoCompleteDialogFragment.class.getSimpleName();

    @InjectView(R.id.simple_listview) ListView mListView;
    @InjectView(R.id.dialog_label) TextView mDialogLabel;
    @InjectView(R.id.filter_options) EditText mFilter;

    private AutoCompleteDialogAdapter mAdapter;
    private OnOptionSelectedListener mListener;

    private int mDialogId;
    private CharSequence mDialogTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.Theme_AppCompat_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_auto_complete,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);

        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilter.getWindowToken(), 0);

        mAdapter = new AutoCompleteDialogAdapter(LayoutInflater.from(getActivity()));
        mListView.setAdapter(mAdapter);
        mDialogLabel.setText(mDialogTitle);
    }

    @OnItemClick(R.id.simple_listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            OptionAdapterValue value = mAdapter.getItem(position);
            if (value != null) {
                mListener.onOptionSelected(mDialogId, position, value.id, value.label, value.data);
            }
        }

        dismiss();
    }

    @OnClick(R.id.close_dialog_button)
    public void onDialogCloseButtonClicked() {
        dismiss();
    }

    @OnTextChanged(value = R.id.filter_options,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable editable) {
        mAdapter.getFilter().filter(editable.toString());
    }

    public void setDialogId(int dialogId) {
        mDialogId = dialogId;
    }

    public int getDialogId() {
        return mDialogId;
    }

    /* This method must be called only after onViewCreated() */
    public void setDialogTitle(CharSequence sequence) {
        mDialogTitle = sequence;
        if (mDialogLabel != null) {
            mDialogLabel.setText(sequence);
        }
    }

    /* This method must be called only after onViewCreated() */
    public CharSequence getDialogTitle() {
        if (mDialogLabel != null) {
            return mDialogLabel.getText();
        } else {
            return null;
        }
    }

    public AutoCompleteDialogAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnOptionSetListener(OnOptionSelectedListener listener) {
        mListener = listener;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }

    // blob string is just for cases when we need to
    // provide some additional data to callback
    public interface OnOptionSelectedListener {
        void onOptionSelected(int dialogId, int position, String id, String name, String blob);
    }
}