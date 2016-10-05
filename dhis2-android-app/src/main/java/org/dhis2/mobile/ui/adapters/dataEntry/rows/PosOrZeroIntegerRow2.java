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

import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dhis2.mobile.R;
import org.dhis2.mobile.io.models.Field;
import org.dhis2.mobile.utils.IsDisabled;

import java.util.ArrayList;

public class PosOrZeroIntegerRow2 implements Row {
    private final LayoutInflater inflater;
    private final Field field, field2, field3, field4;

    public PosOrZeroIntegerRow2(LayoutInflater inflater, Field field, Field field2, Field field3, Field field4 ) {
        this.inflater = inflater;
        this.field = field;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    @Override
    public View getView(View convertView) {
        View view;
        EditTextHolder holder;
        EditTextHolder holder2;
        EditTextHolder holder3;
        EditTextHolder holder4;

        ArrayList<Field> fields = new ArrayList<>();
        fields.add(field);
        fields.add(field2);
        fields.add(field3);
        fields.add(field4);


        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_integer_positive_or_zero_3, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row);
            EditText editText2 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_2);
            EditText editText3 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_3);
            EditText editText4 = (EditText) rowRoot.findViewById(R.id.edit_integer_pos_row_4);

            TextInputLayout inputLayout = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout);
            TextInputLayout inputLayout2 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_2);
            TextInputLayout inputLayout3 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_3);
            TextInputLayout inputLayout4 = (TextInputLayout) rowRoot.findViewById(R.id.edit_integer_pos_layout_4);


            editText.setFilters(new InputFilter[]{new InpFilter()});
            editText2.setFilters(new InputFilter[]{new InpFilter()});
            editText3.setFilters(new InputFilter[]{new InpFilter()});
            editText4.setFilters(new InputFilter[]{new InpFilter()});


            EditTextWatcher watcher = new EditTextWatcher(field);
            editText.addTextChangedListener(watcher);

            EditTextWatcher watcher2 = new EditTextWatcher(field2);
            editText2.addTextChangedListener(watcher2);

            EditTextWatcher watcher3 = new EditTextWatcher(field3);
            editText3.addTextChangedListener(watcher3);

            EditTextWatcher watcher4 = new EditTextWatcher(field4);
            editText4.addTextChangedListener(watcher4);

            
            holder = new EditTextHolder(label, editText, watcher,inputLayout);


            holder2 = new EditTextHolder(label, editText2, watcher2,inputLayout2);
            holder3 = new EditTextHolder(label, editText3, watcher3,inputLayout3);
            holder4 = new EditTextHolder(label, editText4, watcher4,inputLayout4);
            rowRoot.setTag(R.id.TAG_HOLDER1_ID, holder);
            rowRoot.setTag(R.id.TAG_HOLDER2_ID, holder2);
            rowRoot.setTag(R.id.TAG_HOLDER3_ID, holder3);
            rowRoot.setTag(R.id.TAG_HOLDER4_ID, holder4);



            view = rowRoot;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag(R.id.TAG_HOLDER1_ID);
            holder2 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER2_ID);
            holder3 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER3_ID);
            holder4 = (EditTextHolder) view.getTag(R.id.TAG_HOLDER4_ID);
        }

        ArrayList<EditTextHolder> holders = new ArrayList<>();
        holders.add(holder);
        holders.add(holder2);
        holders.add(holder3);
        holders.add(holder4);


        setupEditTextHolders(holders, fields, view);


        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.INTEGER_ZERO_OR_POSITIVE.ordinal();
    }
    
    private class InpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spStart, int spEnd) {
            
            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return Field.EMPTY_FIELD;
            }
            
            if ((spn.length() > 0) && (spStart == 0) && (str.length() > 0) && (str.charAt(0) == '0')) {
                return Field.EMPTY_FIELD;
            }
           
            return str;
        }       
    }
    private void setupEditTextHolders(ArrayList<EditTextHolder> holders, ArrayList<Field> fields, View view){
        for(int i = 0; i < holders.size(); i++){

            String[] label = fields.get(i).getLabel().split("<");

            holders.get(i).textLabel.setText(label[0].substring(6));
            holders.get(i).textWatcher.setField(fields.get(i));
            holders.get(i).editText.addTextChangedListener(holders.get(i).textWatcher);
            holders.get(i).editText.setText(fields.get(i).getValue());
            holders.get(i).editText.setSelectAllOnFocus(true);
            holders.get(i).editText.clearFocus();

            IsDisabled.setEnabled(holders.get(i).editText, fields.get(i), view.getContext());

        }

    }
}