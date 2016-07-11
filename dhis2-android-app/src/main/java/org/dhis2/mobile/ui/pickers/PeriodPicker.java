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

package org.dhis2.mobile.ui.pickers;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.adapters.dataEntry.DateAdapter;
import org.dhis2.mobile.utils.date.CustomDateIterator;
import org.dhis2.mobile.utils.date.DateHolder;
import org.dhis2.mobile.utils.date.DateIteratorFactory;

import java.util.ArrayList;

public class PeriodPicker extends BaseItemPicker {
    private Button add;
    private Button sub;

    private ArrayList<DateHolder> dates;
    private DateHolder savedDate;

    public PeriodPicker(Context context, View root) {
        super(context, root, R.id.textview_picker, R.string.choose_period, R.string.period);
    }

    @Override
    protected void initDialog(Context context, int dialogTitleId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View datePickerView = inflater.inflate(R.layout.dialog_period_picker, null);

        dialog = new Dialog(context);
        dialog.setContentView(datePickerView);
        dialog.setTitle(dialogTitleId);

        labels = new ArrayList<>();
        dates = new ArrayList<>();

        adapter = new DateAdapter(LayoutInflater.from(context));

        list = (ListView) datePickerView.findViewById(R.id.dates_listview);
        list.setAdapter(adapter);

        add = (Button) datePickerView.findViewById(R.id.more);
        sub = (Button) datePickerView.findViewById(R.id.less);

        disable();
    }

    @Override
    public void enable() {
        super.enable();
        resetSavedDate();
    }

    @Override
    public void disable() {
        super.disable();
        resetSavedDate();
    }

    public void setPeriodType(String periodType, int openFP) {
        final CustomDateIterator<ArrayList<DateHolder>> iterator =
                DateIteratorFactory.getDateIterator(periodType, openFP);

        OnClickListener subButtonClickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                dates = iterator.previous();
                updateDateLabels();
                add.setEnabled(true);
            }
        };

        OnClickListener addButtonClickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (iterator.hasNext()) {
                    dates = iterator.next();
                    updateDateLabels();
                    if (!iterator.hasNext()) {
                        add.setEnabled(false);
                    }
                } else {
                    add.setEnabled(false);
                }
            }
        };

        add.setOnClickListener(addButtonClickListener);
        sub.setOnClickListener(subButtonClickListener);

        dates = iterator.current();
        updateDateLabels();

        if (!iterator.hasNext()) {
            add.setEnabled(false);
        } else {
            add.setEnabled(true);
        }
    }

    private void updateDateLabels() {
        labels.clear();

        for (DateHolder date : dates) {
            labels.add(date.getLabel());
        }
        adapter.notifyDataSetChanged();
        list.smoothScrollToPosition(0);
    }

    public ArrayList<DateHolder> getDates() {
        return dates;
    }

    public void saveSelection(DateHolder date) {
        savedDate = date;
    }

    public DateHolder getSavedSelection() {
        return savedDate;
    }

    private void resetSavedDate() {
        savedDate = null;
    }
}
