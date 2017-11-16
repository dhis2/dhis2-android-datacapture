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

package org.dhis2.mobile.ui.adapters.dataEntry;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.dhis2.mobile.io.json.JsonHandler;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.io.models.Group;
import org.dhis2.mobile.io.models.OptionSet;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.AutoCompleteRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.BooleanRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.CheckBoxRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.DatePickerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.GenderRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.IntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.LongTextRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.NegativeIntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.NotSupportedRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.NumberRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.PosIntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.PosOrZeroIntegerRow;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.Row;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.RowTypes;
import org.dhis2.mobile.ui.adapters.dataEntry.rows.TextRow;
import org.dhis2.mobile.utils.TextFileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FieldAdapter extends BaseAdapter {
    public static final String FORM_WITHOUT_SECTION = "default";
    private ArrayList<Row> rows;
    private final String adapterLabel;
    private final Group group;

    private ListView mListView;

    public FieldAdapter(Group group, Context context, ListView listView, boolean readOnly) {
        ArrayList<Field> fields = group.getFields();
        this.group = group;
        this.rows = new ArrayList<Row>();
        this.adapterLabel = group.getLabel();

        CustomOnEditorActionListener customOnEditorActionListener =
                new CustomOnEditorActionListener();

        mListView = listView;

        LayoutInflater inflater = LayoutInflater.from(context);

        if(group.getLabel().equals(FORM_WITHOUT_SECTION)) {
            Collections.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
                }
            });
        }

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.hasOptionSet()) {
                OptionSet optionSet = getOptionSet(context, field.getOptionSet());

                AutoCompleteRow autoCompleteRow = new AutoCompleteRow(inflater, field, optionSet, context);
                autoCompleteRow.setOnEditorActionListener(customOnEditorActionListener);
                autoCompleteRow.setReadOnly(readOnly);
                rows.add(autoCompleteRow);
            } else if (field.getType().equals(RowTypes.TEXT.name())) {
                TextRow textRow = new TextRow(inflater, field);
                textRow.setOnEditorActionListener(customOnEditorActionListener);
                textRow.setReadOnly(readOnly);
                rows.add(textRow);
            } else if (field.getType().equals(RowTypes.LONG_TEXT.name())) {
                LongTextRow longTextRow = new LongTextRow(inflater, field);
                longTextRow.setOnEditorActionListener(customOnEditorActionListener);
                longTextRow.setReadOnly(readOnly);
                rows.add(longTextRow);
            } else if (field.getType().equals(RowTypes.NUMBER.name())) {
                NumberRow numberRow = new NumberRow(inflater, field);
                numberRow.setOnEditorActionListener(customOnEditorActionListener);
                numberRow.setReadOnly(readOnly);
                rows.add(numberRow);
            } else if (field.getType().equals(RowTypes.INTEGER.name())) {
                IntegerRow integerRow = new IntegerRow(inflater, field);
                integerRow.setOnEditorActionListener(customOnEditorActionListener);
                integerRow.setReadOnly(readOnly);
                rows.add(integerRow);
            } else if (field.getType().equals(RowTypes.INTEGER_ZERO_OR_POSITIVE.name())) {
                PosOrZeroIntegerRow posOrZeroIntegerRow = new PosOrZeroIntegerRow(inflater, field);
                posOrZeroIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                posOrZeroIntegerRow.setReadOnly(readOnly);
                rows.add(posOrZeroIntegerRow);
            } else if (field.getType().equals(RowTypes.INTEGER_POSITIVE.name())) {
                PosIntegerRow posIntegerRow = new PosIntegerRow(inflater, field);
                posIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                posIntegerRow.setReadOnly(readOnly);
                rows.add(posIntegerRow);
            } else if (field.getType().equals(RowTypes.INTEGER_NEGATIVE.name())) {
                NegativeIntegerRow negativeIntegerRow = new NegativeIntegerRow(inflater, field);
                negativeIntegerRow.setOnEditorActionListener(customOnEditorActionListener);
                negativeIntegerRow.setReadOnly(readOnly);
                rows.add(negativeIntegerRow);
            } else if (field.getType().equals(RowTypes.BOOLEAN.name())) {
                BooleanRow booleanRow = new BooleanRow(inflater, field);
                booleanRow.setReadOnly(readOnly);
                rows.add(booleanRow);
            } else if (field.getType().equals(RowTypes.TRUE_ONLY.name())) {
                rows.add(new CheckBoxRow(inflater, field));
            } else if (field.getType().equals(RowTypes.DATE.name())) {
                rows.add(new DatePickerRow(inflater, field, this, context));
            } else if (field.getType().equals(RowTypes.GENDER.name())) {
                rows.add(new GenderRow(inflater, field));
            } else{
                rows.add(new NotSupportedRow(inflater, field));
            }
        }
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public int getViewTypeCount() {
        return RowTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getViewType();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rows.get(position).getView(convertView);
    }

    public String getLabel() {
        return adapterLabel;
    }

    public Group getGroup() {
        return group;
    }

    private static OptionSet getOptionSet(Context context, String id) {
        String source = TextFileUtils.readTextFile(context, TextFileUtils.Directory.OPTION_SETS, id);
        try {
            JsonObject jOptionSet = JsonHandler.buildJsonObject(source);
            Gson gson = new Gson();
            return gson.fromJson(jOptionSet, OptionSet.class);
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class CustomOnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            final TextView view = v;
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                int position= mListView.getPositionForView(v);
                mListView.smoothScrollToPosition(position+1);
                mListView.postDelayed(new Runnable() {
                    public void run() {
                        TextView nextField = (TextView)view.focusSearch(View.FOCUS_DOWN);
                        if(nextField != null) {
                            nextField.requestFocus();
                        }
                    }
                }, 200);
                return true;
            }
            return false;
        }
    }
}
